package com.example.weather_forecast.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationSourceViewModel : ViewModel() {
    private val _locationSource = MutableLiveData<String>()
    val locationSource: LiveData<String> get() = _locationSource

    fun setLocationSource(source: String) {
        _locationSource.value = source
    }
}