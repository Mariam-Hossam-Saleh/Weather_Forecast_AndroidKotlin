package com.example.weather_forecast.database

import com.example.weather_forecast.model.pojos.WeatherEntity

class WeatherLocalDataSourceImp(private val dao: WeatherDao) : WeatherLocalDataSource {
    override suspend fun insertWeatherList(weatherList: List<WeatherEntity>) {
        dao.insertWeatherList(weatherList)
    }

    override suspend fun getAllWeather(): List<WeatherEntity> {
        return  dao.getAllWeather()
    }

    override suspend fun clearWeather() {
        dao.clearWeather()
    }
}