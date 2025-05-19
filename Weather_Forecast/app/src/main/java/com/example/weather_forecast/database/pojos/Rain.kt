package com.example.weather_forecast.database.pojos

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h") val volume: Double
)
