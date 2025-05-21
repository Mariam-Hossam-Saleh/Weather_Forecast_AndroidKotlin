package com.example.weather_forecast.model.pojos

import androidx.room.Entity

@Entity(tableName = "weather_table", primaryKeys = ["dt", "cityName"])
data class WeatherEntity(
    val dt: Long,
    val dateText: String,
    val temp: Double,
    val feelsLike: Double,
    val humidity: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val icon: String,
    val cityName: String
)