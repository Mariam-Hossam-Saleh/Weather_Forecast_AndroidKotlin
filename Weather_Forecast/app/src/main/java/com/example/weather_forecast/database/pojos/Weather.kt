package com.example.weather_forecast.database.pojos

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
