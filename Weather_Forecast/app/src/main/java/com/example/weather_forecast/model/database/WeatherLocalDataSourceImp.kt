package com.example.weather_forecast.model.database

import com.example.weather_forecast.model.pojos.AlertEntity
import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImp(private val dao: WeatherDao) : WeatherLocalDataSource {
    override suspend fun insertWeatherList(weatherList: List<WeatherEntity>) {
        dao.insertWeatherList(weatherList)
    }

    override suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity) {
        dao.insertCurrentWeather(currentWeather)
    }

    override suspend fun getAllWeather(): List<WeatherEntity> {
        return  dao.getAllWeather()
    }

    override suspend fun getCityWeather(lat: Double,lon: Double): List<WeatherEntity> {
        return  dao.getCityWeather(lat,lon)
    }

    override suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherEntity {
        return dao.getCurrentWeather(lat, lon)
    }

    override suspend fun clearOldWeather(timestamp: Long) {
        dao.clearOldWeather(timestamp)
    }

    override suspend fun clearCurrentWeather() {
        dao.clearCurrentWeather()
    }

    override suspend fun getFavoriteStateForCity(cityName: String): WeatherEntity {
        return dao.getFavoriteStateForCity(cityName)
    }

    override suspend fun updateWeatherFavoriteStatus(cityName: String, isFavorite: Boolean) {
            return dao.updateWeatherFavoriteStatus(cityName, isFavorite)
    }

    override suspend fun getFavoriteWeatherForCity(cityName: String): List<WeatherEntity> {
        return dao.getFavoriteWeatherForCity(cityName)
    }

    override suspend fun getFavoriteWeatherEntities(): List<WeatherEntity> {
        return dao.getFavoriteWeatherEntities()
    }

    override suspend fun getDailyWeatherByCity(cityName: String): List<WeatherEntity> {
        return dao.getDailyWeatherByCity(cityName)
    }

    override suspend fun insertAlert(alert: AlertEntity) {
        dao.insertAlert(alert)
    }

    override fun getActiveAlerts(): Flow<List<AlertEntity>> {
        return dao.getActiveAlerts()
    }

    override suspend fun disableAlert(alertId: String) {
        dao.disableAlert(alertId)
    }
}