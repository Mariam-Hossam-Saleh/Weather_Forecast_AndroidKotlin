package com.example.weather_forecast.database

import com.example.weather_forecast.model.pojos.WeatherEntity

interface WeatherLocalDataSource {

    suspend fun insertWeatherList(weatherList: List<WeatherEntity>)

    suspend fun getAllWeather(): List<WeatherEntity>

    suspend fun clearWeather()

}