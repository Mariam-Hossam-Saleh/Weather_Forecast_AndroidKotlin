package com.example.weather_forecast.home.view

import com.example.weather_forecast.model.pojos.WeatherEntity


interface OnWeatherClickListener {
    fun onWeatherClick(weather: WeatherEntity)
}