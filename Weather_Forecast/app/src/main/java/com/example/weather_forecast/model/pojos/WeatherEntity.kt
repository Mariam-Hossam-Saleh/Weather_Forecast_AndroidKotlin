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

@Entity(tableName = "current_weather_table", primaryKeys = ["cityName"])
data class CurrentWeatherEntity(
    // Coord
    val coordLon: Double,
    val coordLat: Double,
    // Weather
    val weatherId: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val weatherIcon: String,
    // base
    val base: String,
    // Main,
    val mainTemp: Double,
    val mainFeels_like: Double,
    val mainTemp_min: Double,
    val mainTemp_max: Double,
    val mainPressure: Int,
    val mainHumidity: Int,
    val mainSea_level: Int?,
    val mainGrnd_level: Int?,
    //visibility
    val visibility: Int,
    // Wind
    val windSpeed: Double,
    val windDeg: Int,
    val windGust: Double?,

    // Rain?,
    val rain: Double?,
    val clouds: Int,
    val dt: Long,
    //Sys
    val sysType: Int?,
    val sysId: Int?,
    val sysCountry: String,
    val sysSunrise: Long,
    val sysSunset: Long,

    val timezone: Int,
    val cityId: Int,
    val cityName: String,
    val cod: Int
)