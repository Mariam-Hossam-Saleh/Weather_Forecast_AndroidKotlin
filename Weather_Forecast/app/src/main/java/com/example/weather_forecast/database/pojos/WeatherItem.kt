package com.example.weather_forecast.database.pojos

data class WeatherItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain?, // Nullable because not always present
    val sys: Sys,
    val dt_txt: String
)
