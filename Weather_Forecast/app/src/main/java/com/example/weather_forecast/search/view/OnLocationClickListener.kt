package com.example.weather_forecast.search.view

import com.example.weather_forecast.model.pojos.WeatherEntity

interface OnLocationClickListener {
    fun onLocationClick(weather: WeatherEntity)
}