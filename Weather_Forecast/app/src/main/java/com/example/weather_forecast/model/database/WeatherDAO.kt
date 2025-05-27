package com.example.weather_forecast.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeatherList(weatherList: List<WeatherEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity)

    @Query("SELECT * FROM weather_table ORDER BY dt ASC")
    suspend fun getAllWeather(): List<WeatherEntity>

    @Query("SELECT * FROM weather_table WHERE lat = :lat AND lon = :lon ORDER BY dt ASC")
    suspend fun getCityWeather(lat: Double,lon: Double): List<WeatherEntity>

    @Query("SELECT * FROM current_weather_table")
    suspend fun getCurrentWeather(): CurrentWeatherEntity

    @Query("DELETE FROM current_weather_table")
    suspend fun clearCurrentWeather()

    @Query("DELETE FROM weather_table WHERE dt < :timestamp")
    suspend fun clearOldWeather(timestamp: Long)

    @Query("SELECT * FROM weather_table WHERE isFavorite = 1 AND cityName = :cityName")
    suspend fun getFavoriteWeatherForCity(cityName: String): List<WeatherEntity>

    @Query("""SELECT * FROM weather_table 
              WHERE isFavorite = 1 AND cityName = :cityName
              AND dt = (SELECT MAX(dt) FROM weather_table WHERE cityName = weather_table.cityName AND isFavorite = 1)
              GROUP BY cityName""")
    suspend fun getFavoriteStateForCity(cityName: String): WeatherEntity

    @Query("UPDATE weather_table SET isFavorite = :isFavorite WHERE cityName = :cityName")
    suspend fun updateWeatherFavoriteStatus(cityName: String, isFavorite: Boolean)

    @Query("""
        SELECT * FROM weather_table 
        WHERE isFavorite = 1 
        AND dt = (SELECT MAX(dt) FROM weather_table WHERE cityName = weather_table.cityName AND isFavorite = 1)
        GROUP BY cityName
    """)
    suspend fun getFavoriteWeatherEntities(): List<WeatherEntity>
}
