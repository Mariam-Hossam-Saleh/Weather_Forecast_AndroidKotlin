package com.example.weather_forecast.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_forecast.model.repo.WeatherRepository

class HomeViewModelFactory(private val repo : WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not Found")
        }
    }
}