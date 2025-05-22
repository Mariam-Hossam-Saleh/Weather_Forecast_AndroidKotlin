package com.example.weather_forecast.model.pojos

data class Sys(
    val pod: String,
    val type: Int?,
    val id: Int?,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
