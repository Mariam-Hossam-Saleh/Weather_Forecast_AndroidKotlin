package com.example.weather_forecast.model.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.CurrentWeatherResponce
import com.example.weather_forecast.model.pojos.CurrentWeatherResponce.Companion.toCurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.pojos.WeatherItem.Companion.toWeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

class WeatherRemoteDataSourceImp(
    private val weatherService: WeatherService,
    private val context: Context // Inject context for connectivity check
) : WeatherRemoteDataSource {

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override suspend fun getWeatherOverNetwork(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): List<WeatherEntity> = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            Log.e("WeatherRemoteDataSource", "No internet connection")
            throw Exception("No internet connection")
        }
        try {
            val response = weatherService.getWeatherForecast(
                lat = lat,
                lon = lon,
                apiKey = apiKey,
                units = "metric"
            )
            val cityName = response.city.name
            response.list.mapNotNull { weatherItem ->
                try {
                    weatherItem.toWeatherEntity(cityName)
                } catch (e: Exception) {
                    Log.e("WeatherRemoteDataSource", "Failed to map weather item: ${e.message}")
                    null
                }
            }
        } catch (e: UnknownHostException) {
            Log.e("WeatherRemoteDataSource", "DNS resolution failed: ${e.message}")
            throw Exception("Unable to connect to weather service. Please check your internet connection.")
        } catch (e: Exception) {
            Log.e("WeatherRemoteDataSource", "Failed to fetch forecast: ${e.message}", e)
            throw Exception("Failed to fetch forecast: ${e.message}", e)
        }
    }

    override suspend fun getCurrentWeatherOverNetwork(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): CurrentWeatherEntity = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            Log.e("WeatherRemoteDataSource", "No internet connection")
            throw Exception("No internet connection")
        }
        try {
            val response = weatherService.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = apiKey,
                units = units
            )
            response.toCurrentWeatherEntity(response.name)
        } catch (e: UnknownHostException) {
            Log.e("WeatherRemoteDataSource", "DNS resolution failed: ${e.message}")
            throw Exception("Unable to connect to weather service. Please check your internet connection.")
        } catch (e: Exception) {
            Log.e("WeatherRemoteDataSource", "Failed to fetch current weather: ${e.message}", e)
            throw Exception("Failed to fetch current weather: ${e.message}", e)
        }
    }
}