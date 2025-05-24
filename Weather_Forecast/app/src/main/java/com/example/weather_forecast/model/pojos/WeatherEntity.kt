package com.example.weather_forecast.model.pojos

import androidx.room.Entity
import com.example.weather_forecast.R

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
    val dt: Long,
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
        else -> R.drawable.ic_01d
    }
}

fun getWeatherStateResId(weatherMain: String): Int {
    return when (weatherMain) {
        "Clear" -> R.drawable.clear2
        "Clouds" -> R.drawable.cloud7
        "Rain" -> R.drawable.rain2
        "Snow" -> R.drawable.snow2
        "Thunderstorm" -> R.drawable.thunderstorm2
        "Drizzle" -> R.drawable.drizzle
        "Mist" -> R.drawable.mist2
        "Haze" -> R.drawable.mist4
        else -> R.drawable.clear1
    }
}
