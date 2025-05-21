package com.example.weather_forecast.network

import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.pojos.WeatherItem
import com.example.weather_forecast.model.pojos.WeatherItem.Companion.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRemoteDataSourceImp(
    private val weatherService: WeatherService
) : WeatherRemoteDataSource {

    override suspend fun getWeatherOverNetwork(
        lat: Double,
        lon: Double,
        apiKey: String
    ): List<WeatherEntity> = withContext(Dispatchers.IO) {
        try {
            val response = weatherService.getWeatherForecast(
                lat = lat,
                lon = lon,
                apiKey = apiKey
            )

            val cityName = response.city.name

            response.list.mapNotNull { weatherItem ->
                try {
                    weatherItem.toEntity(cityName)
                } catch (e: Exception) {
                    null
                }
            }

        } catch (e: Exception) {
            emptyList()
        }
    }
}