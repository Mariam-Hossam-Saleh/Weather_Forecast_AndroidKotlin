package com.example.weather_forecast.model.database

import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity

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

    override suspend fun getCurrentWeather(): CurrentWeatherEntity {
        return dao.getCurrentWeather()
    }

    override suspend fun clearWeather() {
        dao.clearWeather()
    }

    override suspend fun clearCurrentWeather() {
        dao.clearCurrentWeather()
    }
}