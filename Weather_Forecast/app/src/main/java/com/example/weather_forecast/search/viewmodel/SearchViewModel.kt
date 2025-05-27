package com.example.weather_forecast.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.repo.WeatherRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val repo: WeatherRepository) : ViewModel() {

    private val _favoriteWeatherEntities = MutableLiveData<List<WeatherEntity>>()
    val favoriteWeatherEntities: LiveData<List<WeatherEntity>> = _favoriteWeatherEntities

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getFavoriteWeatherEntities() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repo.getFavoriteWeatherEntities()
                _favoriteWeatherEntities.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch favorite weather entities: ${e.message}"
                Log.e("HomeViewModel", "Error fetching favorite weather entities", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onItemClicked(weatherEntity: WeatherEntity) {
        viewModelScope.launch {
            repo.insertWeatherList(listOf(weatherEntity))
        }
    }
}

