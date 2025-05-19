package com.example.weather_forecast.network

import com.example.weather_forecast.database.pojos.WeatherItem

interface WeatherRemoteDataSource {
    suspend fun getWeatherOverNetwork(lat: Double, lon: Double, apiKey: String): List<WeatherItem>
}