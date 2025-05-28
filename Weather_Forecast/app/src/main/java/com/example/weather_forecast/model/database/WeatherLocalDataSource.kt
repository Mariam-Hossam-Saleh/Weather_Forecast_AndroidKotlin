package com.example.weather_forecast.model.database

import com.example.weather_forecast.model.pojos.AlertEntity
import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {

    suspend fun insertWeatherList(weatherList: List<WeatherEntity>)

    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity)

    suspend fun getAllWeather(): List<WeatherEntity>

    suspend fun getCityWeather(lat: Double,lon: Double): List<WeatherEntity>

    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherEntity

    suspend fun clearOldWeather(timestamp: Long)

    suspend fun clearCurrentWeather()

    suspend fun getFavoriteStateForCity(cityName: String): WeatherEntity

    suspend fun updateWeatherFavoriteStatus(cityName: String, isFavorite: Boolean)

    suspend fun getFavoriteWeatherForCity(cityName: String): List<WeatherEntity>

    suspend fun getFavoriteWeatherEntities(): List<WeatherEntity>

    suspend fun getDailyWeatherByCity(cityName: String): List<WeatherEntity>

    suspend fun insertAlert(alert: AlertEntity)

    fun getActiveAlerts(): Flow<List<AlertEntity>>

    suspend fun disableAlert(alertId: String)

}