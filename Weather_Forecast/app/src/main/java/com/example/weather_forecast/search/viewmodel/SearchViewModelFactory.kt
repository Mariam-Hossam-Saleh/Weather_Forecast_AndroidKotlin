package com.example.weather_forecast.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_forecast.model.repo.WeatherRepository

class SearchViewModelFactory(private val repo : WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SearchViewModel::class.java)){
            SearchViewModel(repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not Found")
        }
    }
}