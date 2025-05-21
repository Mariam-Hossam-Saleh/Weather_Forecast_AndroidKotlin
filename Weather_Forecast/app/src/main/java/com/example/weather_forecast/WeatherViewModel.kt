package com.example.weather_forecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.repo.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

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
                val result = repository.getAllWeather(
                    isRemote = true,
                    lat = lat,
                    lon = lon,
                    apiKey = "e82d172019ed90076e2ec824decb3d40"
                )
                _weatherList.value = result
                repository.insertWeatherList(result) // Fix: pass result directly
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch weather: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onItemClicked(weatherEntity: WeatherEntity) {
        viewModelScope.launch {
            repository.insertWeatherList(listOf(weatherEntity))
            // Optional: Show a toast or update UI
        }
    }
}
