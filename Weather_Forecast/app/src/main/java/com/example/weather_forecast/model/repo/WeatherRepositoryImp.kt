package com.example.weather_forecast.model.repo

import com.example.weather_forecast.model.database.WeatherLocalDataSource
import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.network.WeatherRemoteDataSource

class WeatherRepositoryImp (
    private var weatherRemoteDataSource : WeatherRemoteDataSource,
    private var weatherLocalDataSource : WeatherLocalDataSource
) : WeatherRepository{
    companion object{
        private var instance : WeatherRepositoryImp? = null
        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource,
            weatherLocalDataSource: WeatherLocalDataSource
        ) : WeatherRepositoryImp {
            return instance ?: synchronized(this){
                val temp = WeatherRepositoryImp(weatherRemoteDataSource,weatherLocalDataSource)
                instance = temp
                temp
            }
        }
    }
    override suspend fun insertWeatherList(weatherList: List<WeatherEntity>) {
        weatherLocalDataSource.insertWeatherList(weatherList)
    }

    override suspend fun insertCurrentWeather(CurrentWeather: CurrentWeatherEntity) {
        weatherLocalDataSource.insertCurrentWeather(CurrentWeather)
    }

    override suspend fun getWeatherForecast(
        isRemote: Boolean,
        lat: Double,
        lon: Double,
        apiKey: String
    ): List<WeatherEntity> {
        return  if(isRemote) {
            weatherRemoteDataSource.getWeatherOverNetwork(lat,lon,apiKey)
        }
        else {
            weatherLocalDataSource.getAllWeather()
        }
    }

    override suspend fun getCurrentWeather(
        isRemote: Boolean,
        lat: Double,
        lon: Double,
        apiKey: String
    ): CurrentWeatherEntity {
        return  if(isRemote) {
            weatherRemoteDataSource.getCurrentWeatherOverNetwork(lat,lon,apiKey)
        }
        else {
            weatherLocalDataSource.getCurrentWeather()
        }
    }

    override suspend fun getCityWeather(lat: Double,lon: Double): List<WeatherEntity>{
        return weatherLocalDataSource.getCityWeather(lat,lon)
    }

    override suspend fun clearOldWeather(timestamp: Long) {
        weatherLocalDataSource.clearOldWeather(timestamp)
    }

    override suspend fun clearCurrentWeather() {
        weatherLocalDataSource.clearCurrentWeather()
    }

    override suspend fun updateWeatherFavoriteStatus(cityName: String, isFavorite: Boolean) {
        weatherLocalDataSource.updateWeatherFavoriteStatus(cityName, isFavorite)
    }

    override suspend fun getFavoriteWeatherForCity(cityName: String): List<WeatherEntity> {
        return weatherLocalDataSource.getFavoriteWeatherForCity(cityName)
    }

    override suspend fun getFavoriteWeatherEntities(): List<WeatherEntity> {
        return weatherLocalDataSource.getFavoriteWeatherEntities()
    }

    override suspend fun getFavoriteStateForCity(cityName: String): WeatherEntity {
        return weatherLocalDataSource.getFavoriteStateForCity(cityName)
    }

}