package com.example.weather_forecast.alert.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_forecast.model.repo.WeatherRepositoryImp

class WeatherAlertViewModelFactory(
    private val repository: WeatherRepositoryImp,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherAlertViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherAlertViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}