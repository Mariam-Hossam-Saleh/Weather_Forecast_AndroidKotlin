package com.example.weather_forecast.model.pojos

import androidx.room.Entity
import com.example.weather_forecast.R

@Entity(tableName = "weather_table", primaryKeys = ["dt", "cityName"])
data class WeatherEntity(
    val dt: Long,
    val lat: Double,
    val lon: Double,
    val cod: String,
    val cnt: Int,
    val dt_txt: String,
    // Main,
    val mainTemp: Double,
    val mainFeels_like: Double,
    val mainTemp_min: Double,
    val mainTemp_max: Double,
    val mainPressure: Int,
    val mainHumidity: Int,
    val mainSea_level: Int?,
    val mainGrnd_level: Int?,
    // Weather
    val weatherId: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val weatherIcon: String,
    // Cloud
    val clouds: Int,
    // Wind
    val windSpeed: Double,
    val windDeg: Int,
    val windGust: Double?,
    //visibility
    val visibility: Int,
    // Pop
    val pop: Double,
    // Rain?,
    val rain: Double?,
    // Sys
    val sysPod: String?,
    // City
    val cityId: Int,
    val cityName: String,
    val cityCoordLon: Double,
    val cityCoordLat: Double,
    val cityCountry: String,
    val cityPopulation: String,
    val cityTimezone: String,
    val citySunrise: String,
    val citySunset: String,
    val isFavorite: Boolean = false
)

@Entity(tableName = "current_weather_table", primaryKeys = ["cityName"])
data class CurrentWeatherEntity(
    val dt: Long,
    val lat: Double,
    val lon: Double,
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

fun getIconResId(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable.ic_01d
        "01n" -> R.drawable.ic_01n
        "02d" -> R.drawable.ic_02d
        "02n" -> R.drawable.ic_02n
        "03d" -> R.drawable.ic_03d
        "03n" -> R.drawable.ic_03n
        "04d" -> R.drawable.ic_04d
        "04n" -> R.drawable.ic_04n
        "09d" -> R.drawable.rain_icon
        "09n" -> R.drawable.rain_icon
        "10d" -> R.drawable.rain_icon
        "10n" -> R.drawable.rain_icon
        "11d" -> R.drawable.tornado
        "11n" -> R.drawable.tornado
        "13d" -> R.drawable.snowflake
        "13n" -> R.drawable.snowflake
        "50d" -> R.drawable.haze
        "50n" -> R.drawable.haze
        else -> R.drawable.ic_01d
    }
}

fun getWeatherStateResId(weatherMain: String): Int {
    return when (weatherMain) {
        "Clear" -> R.drawable.c3
        "Clouds" -> R.drawable.cloud7
        "Rain" -> R.drawable.rain4
        "Snow" -> R.drawable.snow2
        "Thunderstorm" -> R.drawable.thunderstorm2
        "Drizzle" -> R.drawable.drizzle
        "Mist" -> R.drawable.mist2
        "Haze" -> R.drawable.mist4
        else -> R.drawable.clear1
    }
}
