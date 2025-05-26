package com.example.weather_forecast.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.repo.WeatherRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val repo: WeatherRepository) : ViewModel() {

    private val _weatherList = MutableLiveData<List<WeatherEntity>>()
    val weatherList: LiveData<List<WeatherEntity>> = _weatherList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repo.getAllWeather(
                    isRemote = true,
                    lat = lat,
                    lon = lon,
                    apiKey = "e82d172019ed90076e2ec824decb3d40"
//                    apiKey = "4dc586435c64d43d0d6e2ba439393672"
                )
                _weatherList.value = result
                repo.clearWeather()
                repo.insertWeatherList(result)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch weather: ${e.message}"
                Log.e("HomeViewModel", "Error fetching weather", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getStoredWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repo.getAllWeather(
                    isRemote = false,
                )
                _weatherList.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Failed to get stored weather: ${e.message}"
                Log.e("HomeViewModel", "Error getting stored weather", e)
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

