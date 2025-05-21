package com.example.weather_forecast.network

import com.example.weather_forecast.model.pojos.WeatherEntity

interface WeatherRemoteDataSource {
    suspend fun getWeatherOverNetwork(lat: Double, lon: Double, apiKey: String, ): List<WeatherEntity>
}