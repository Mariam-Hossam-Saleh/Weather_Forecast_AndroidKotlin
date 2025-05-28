package com.example.weather_forecast.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.repo.WeatherRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class HomeViewModel(private val repo: WeatherRepository) : ViewModel() {

    private val _weatherList = MutableLiveData<List<WeatherEntity>>()
    val weatherList: LiveData<List<WeatherEntity>> = _weatherList

    private val _dailyWeatherList = MutableLiveData<List<WeatherEntity>>()
    val dailyWeatherList: LiveData<List<WeatherEntity>> = _dailyWeatherList

    private val _todayWeather = MutableLiveData<CurrentWeatherEntity?>()
    val todayWeather: LiveData<CurrentWeatherEntity?> = _todayWeather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _favoriteState = MutableLiveData<WeatherEntity?>()
    val favoriteState: LiveData<WeatherEntity?> = _favoriteState

    private val _favoriteToggleResult = MutableLiveData<Boolean?>()
    val favoriteToggleResult: LiveData<Boolean?> = _favoriteToggleResult

    private val todayMidnight: ZonedDateTime = LocalDate.now().atStartOfDay(ZoneOffset.UTC)
    private val unixTimeSeconds = todayMidnight.toEpochSecond()

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repo.getWeatherForecast(
                    isRemote = true,
                    lat = lat,
                    lon = lon,
                    apiKey = "e82d172019ed90076e2ec824decb3d40"
                )
                repo.clearOldWeather(unixTimeSeconds)
                repo.insertWeatherList(result)
                _weatherList.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch weather: ${e.message}"
                Log.e("HomeViewModel", "Error fetching weather", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getStoredCityWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repo.getCityWeather(lat, lon)
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
                repo.insertCurrentWeather(result)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch current weather: ${e.message}"
                Log.e("HomeViewModel", "Error fetching current weather", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getStoredCurrentWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repo.getCurrentWeather(isRemote = false,lat, lon)
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

    fun toggleFavoriteStatus(cityName: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val currentFavorite = repo.getFavoriteStateForCity(cityName)
                val newFavoriteStatus = currentFavorite?.isFavorite != true
                repo.updateWeatherFavoriteStatus(cityName, newFavoriteStatus)
                _favoriteState.value = repo.getFavoriteStateForCity(cityName)
                _favoriteToggleResult.value = newFavoriteStatus
                _weatherList.value = repo.getCityWeather(lat, lon)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update favorite status: ${e.message}"
                Log.e("HomeViewModel", "Error updating favorite status", e)
            } finally {
                _isLoading.value = false
                _favoriteToggleResult.value = null
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

    fun getDailyWeatherByCity(cityName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repo.getDailyWeatherByCity(cityName)
                _dailyWeatherList.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Failed to get stored daily weather: ${e.message}"
                Log.e("HomeViewModel", "Error getting stored daily weather", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}