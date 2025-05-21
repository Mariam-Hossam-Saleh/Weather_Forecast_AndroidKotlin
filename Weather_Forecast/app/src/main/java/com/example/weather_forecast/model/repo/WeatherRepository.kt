package com.example.weather_forecast.model.repo

import com.example.weather_forecast.model.pojos.WeatherEntity

interface WeatherRepository {
    suspend fun insertWeatherList(weatherList: List<WeatherEntity>)

    suspend fun getAllWeather(
        isRemote: Boolean,
        lat: Double = 0.0,
        lon: Double = 0.0,
        apiKey: String = ""
    ): List<WeatherEntity>

    suspend fun clearWeather()
}