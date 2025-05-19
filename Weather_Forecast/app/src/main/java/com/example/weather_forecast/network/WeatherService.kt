package com.example.weather_forecast.network

import com.example.weather_forecast.database.pojos.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast")
    fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric" // Optional: for Celsius
    ): WeatherResponse
}
