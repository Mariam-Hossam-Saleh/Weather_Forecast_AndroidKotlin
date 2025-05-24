package com.example.weather_forecast.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherList(weatherList: List<WeatherEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity)

    @Query("SELECT * FROM weather_table ORDER BY dt ASC")
    suspend fun getAllWeather(): List<WeatherEntity>

    @Query("SELECT * FROM current_weather_table")
    suspend fun getCurrentWeather(): CurrentWeatherEntity

    @Query("DELETE FROM weather_table")
    suspend fun clearWeather()

    @Query("DELETE FROM current_weather_table")
    suspend fun clearCurrentWeather()
}

//
//@Dao
//interface CurrentWeatherDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity)
//
//    @Query("SELECT * FROM current_weather_table")
//    suspend fun getCurrentWeather(): CurrentWeatherEntity
//
//    @Query("DELETE FROM current_weather_table")
//    suspend fun clearCurrentWeather()
//}