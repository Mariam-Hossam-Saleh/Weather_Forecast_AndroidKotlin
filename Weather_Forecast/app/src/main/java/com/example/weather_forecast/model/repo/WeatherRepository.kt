package com.example.weather_forecast.model.repo

import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity

interface WeatherRepository {
    suspend fun insertWeatherList(weatherList: List<WeatherEntity>)

    suspend fun insertCurrentWeather(CurrentWeather: CurrentWeatherEntity)

    suspend fun getWeatherForecast(
        isRemote: Boolean,
        lat: Double = 0.0,
        lon: Double = 0.0,
        apiKey: String = ""
    ): List<WeatherEntity>

    suspend fun getCurrentWeather(
        isRemote: Boolean,
        lat: Double = 0.0,
        lon: Double = 0.0,
        apiKey: String = ""
    ): CurrentWeatherEntity

    suspend fun getCityWeather(lat: Double,lon: Double): List<WeatherEntity>

    suspend fun clearOldWeather(timestamp: Long)

    suspend fun clearCurrentWeather()

    suspend fun updateWeatherFavoriteStatus(cityName: String, isFavorite: Boolean)

    suspend fun getFavoriteWeatherForCity(cityName: String): List<WeatherEntity>

    suspend fun getFavoriteWeatherEntities(): List<WeatherEntity>

    suspend fun getFavoriteStateForCity(cityName: String): WeatherEntity

}