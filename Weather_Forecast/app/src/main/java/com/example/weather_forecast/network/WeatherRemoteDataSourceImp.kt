package com.example.weather_forecast.network

import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.CurrentWeatherResponce
import com.example.weather_forecast.model.pojos.CurrentWeatherResponce.Companion.toCurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.pojos.WeatherItem.Companion.toWeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRemoteDataSourceImp(
    private val weatherService: WeatherService
) : WeatherRemoteDataSource {

    override suspend fun getWeatherOverNetwork(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): List<WeatherEntity> = withContext(Dispatchers.IO) {
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
                    null
                }
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

//    override suspend fun getCurrentWeatherOverNetwork(
//        lat: Double,
//        lon: Double,
//        apiKey: String,
//        units: String
//    ): CurrentWeatherEntity = withContext(Dispatchers.IO) {
//        try {
//            val response = weatherService.getCurrentWeather(
//                lat = lat,
//                lon = lon,
//                apiKey = apiKey,
//                units = units
//            )
//
//            response.toCurrentWeatherEntity()?.let {
//                Result.success(it)
//            } ?: Result.failure(Exception("Failed to convert response to entity"))
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
    override suspend fun getCurrentWeatherOverNetwork(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): CurrentWeatherEntity = withContext(Dispatchers.IO) {
        try {
            val response = weatherService.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = apiKey,
                units = units
            )

            response.toCurrentWeatherEntity(response.name)
                ?: throw Exception("Failed to convert current weather response")

        } catch (e: Exception) {
            throw Exception("Failed to fetch current weather", e)
        }
    }
//    override suspend fun getCurrentWeatherOverNetwork(
//        lat: Double,
//        lon: Double,
//        apiKey: String,
//        units: String
//    ): CurrentWeatherEntity = withContext(Dispatchers.IO) {
//        try {
//            val responce = weatherService.getCurrentWeather(
//                lat = lat,
//                lon = lon,
//                apiKey = apiKey,
//                units = units
//            )
//            val cityName = responce.name
//            responce.toCurrentWeatherEntity(cityName).apply { currentWeatherItem ->
//                try {
//                    currentWeatherItem.(cityName)
//                } catch (ex : Exception) {
//                    null
//                }
//            }
//        } catch (ex :Exception) {
//            null
//        }
//    }

}