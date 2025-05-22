package com.example.weather_forecast.model.pojos

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h") val volume: Double,
    @SerializedName("1h") val rain: Double?
)
