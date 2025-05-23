package com.example.weather_forecast.network

import com.example.weather_forecast.model.pojos.CurrentWeatherResponce
import com.example.weather_forecast.model.pojos.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric" // for Celsius
    ): WeatherResponse

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric" // for Celsius
    ): CurrentWeatherResponce
}
