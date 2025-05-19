package com.example.weather_forecast.database.pojos

data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherItem>,
    val city: City
)
