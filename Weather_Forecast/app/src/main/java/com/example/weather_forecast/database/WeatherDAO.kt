package com.example.weather_forecast.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_forecast.model.pojos.WeatherEntity

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherList(weatherList: List<WeatherEntity>)

    @Query("SELECT * FROM weather_table ORDER BY dt ASC")
    suspend fun getAllWeather(): List<WeatherEntity>

    @Query("DELETE FROM weather_table")
    suspend fun clearWeather()
}
