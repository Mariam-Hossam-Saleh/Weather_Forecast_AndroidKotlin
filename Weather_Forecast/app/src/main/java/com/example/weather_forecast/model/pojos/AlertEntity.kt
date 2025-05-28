package com.example.weather_forecast.model.pojos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey val id: String,
    val latitude: Double,
    val longitude: Double,
    val startTime: Long,
    val endTime: Long,
    val alertType: String, // "Notification" or "Alarm"
    val isActive: Boolean
)