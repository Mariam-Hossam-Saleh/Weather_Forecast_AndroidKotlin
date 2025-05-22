package com.example.weather_forecast.network

import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.CurrentWeatherResponce
import com.example.weather_forecast.model.pojos.WeatherEntity

interface WeatherRemoteDataSource {
    suspend fun getWeatherOverNetwork(lat: Double, lon: Double, apiKey: String, units: String = "metric"): List<WeatherEntity>
    suspend fun getCurrentWeatherOverNetwork(lat: Double, lon: Double, apiKey: String, units: String = "metric") : CurrentWeatherEntity
}