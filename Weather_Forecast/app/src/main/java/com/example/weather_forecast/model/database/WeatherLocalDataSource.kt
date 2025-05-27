package com.example.weather_forecast.model.database

import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity

interface WeatherLocalDataSource {

    suspend fun insertWeatherList(weatherList: List<WeatherEntity>)

    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity)

    suspend fun getAllWeather(): List<WeatherEntity>

    suspend fun getCurrentWeather(): CurrentWeatherEntity

    suspend fun clearOldWeather(timestamp: Long)

    suspend fun clearCurrentWeather()

    suspend fun getFavoriteStateForCity(cityName: String): WeatherEntity

    suspend fun updateWeatherFavoriteStatus(cityName: String, isFavorite: Boolean)

    suspend fun getFavoriteWeatherForCity(cityName: String): List<WeatherEntity>

    suspend fun getFavoriteWeatherEntities(): List<WeatherEntity>

}