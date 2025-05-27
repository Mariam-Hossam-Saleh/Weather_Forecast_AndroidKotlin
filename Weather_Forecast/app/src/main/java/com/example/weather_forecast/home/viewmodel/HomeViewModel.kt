package com.example.weather_forecast.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.repo.WeatherRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class HomeViewModel(private val repo: WeatherRepository) : ViewModel() {

    private val _weatherList = MutableLiveData<List<WeatherEntity>>()
    val weatherList: LiveData<List<WeatherEntity>> = _weatherList

    private val _todayWeather = MutableLiveData<CurrentWeatherEntity>()
    val todayWeather: LiveData<CurrentWeatherEntity> = _todayWeather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _favoriteWeatherEntities = MutableLiveData<List<WeatherEntity>>()
    val favoriteWeatherEntities: LiveData<List<WeatherEntity>> = _favoriteWeatherEntities

    private val _favoriteState = MutableLiveData<WeatherEntity?>()
    val favoriteState: LiveData<WeatherEntity?> = _favoriteState

    private val todayMidnight: ZonedDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
    private val unixTimeSeconds = todayMidnight.toEpochSecond()


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
                )
                _weatherList.value = result
                Log.i("HomeViewModel", "Time: $unixTimeSeconds")
                repo.clearOldWeather(unixTimeSeconds)
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
                    isRemote = false
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

    fun fetchCurrentWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repo.getCurrentWeather(
                    isRemote = true,
                    lat = lat,
                    lon = lon,
                    apiKey = "e82d172019ed90076e2ec824decb3d40"
                )
                _todayWeather.value = result
                repo.clearCurrentWeather()
                repo.insertCurrentWeather(result)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch current weather: ${e.message}"
                Log.e("HomeViewModel", "Error fetching current weather", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getStoredCurrentWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repo.getCurrentWeather(
                    isRemote = false
                )
                _todayWeather.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Failed to get stored current weather: ${e.message}"
                Log.e("HomeViewModel", "Error getting stored current weather", e)
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

    fun toggleFavoriteStatus(cityName: String, isFavorite: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repo.updateWeatherFavoriteStatus(cityName, !isFavorite)
                // Refresh weather list
                _weatherList.value = repo.getAllWeather(isRemote = false)
                // Refresh favorite entities
                _favoriteWeatherEntities.value = repo.getFavoriteWeatherEntities()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update favorite status: ${e.message}"
                Log.e("HomeViewModel", "Error updating favorite status", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchFavoriteWeatherEntities() {
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

    fun fetchFavoriteStateForCity(cityName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repo.getFavoriteStateForCity(cityName)
                _favoriteState.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch favorite state for city: ${e.message}"
                Log.e("HomeViewModel", "Error fetching favorite state for city", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}