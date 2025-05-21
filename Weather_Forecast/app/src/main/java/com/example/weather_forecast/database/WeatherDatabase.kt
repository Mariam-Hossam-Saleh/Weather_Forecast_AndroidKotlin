package com.example.weather_forecast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather_forecast.model.pojos.WeatherEntity

@Database(entities = [WeatherEntity::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getWeatherDao() : WeatherDao
    companion object {
        @Volatile
        private var INSTANCE : WeatherDatabase? = null
        fun getInstance(context : Context) : WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, WeatherDatabase::class.java, "weather_database")
                    .build()
                INSTANCE = instance
                instance}
        }
    }
}