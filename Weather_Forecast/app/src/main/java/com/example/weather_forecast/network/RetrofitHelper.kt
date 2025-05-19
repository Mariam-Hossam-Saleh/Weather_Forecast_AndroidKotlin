package com.example.weather_forecast.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper{
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val retrofitInstance = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service: WeatherService = retrofitInstance.create(WeatherService::class.java)
}