package com.example.weather_forecast.network

import com.example.weather_forecast.database.pojos.WeatherItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRemoteDataSourceImp(private val weatherService: WeatherService) : WeatherRemoteDataSource {
    override suspend fun getWeatherOverNetwork(lat: Double, lon: Double, apiKey: String): List<WeatherItem> {
        val response = withContext(Dispatchers.IO) {
            weatherService.getWeatherForecast(
                lat = lat,
                lon = lon,
                apiKey = apiKey
            ).list
        }
        return response
    }
}
