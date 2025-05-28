package com.example.weather_forecast.alert.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.model.pojos.AlertEntity
import com.example.weather_forecast.model.repo.WeatherRepositoryImp
import com.example.weather_forecast.receivers.AlertReceiver
import kotlinx.coroutines.launch

class WeatherAlertViewModel(
    private val repository: WeatherRepositoryImp,
    private val context: Context
) : ViewModel() {

    val alerts: LiveData<List<AlertEntity>> = repository.getActiveAlerts().asLiveData()

    fun scheduleAlert(alert: AlertEntity) {
        viewModelScope.launch {
            repository.insertAlert(alert)
            scheduleAlarm(alert)
        }
    }

    fun cancelAlert(alert: AlertEntity) {
        viewModelScope.launch {
            repository.disableAlert(alert.id)
            cancelAlarm(alert)
        }
    }

    private fun scheduleAlarm(alert: AlertEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("alert_id", alert.id)
            putExtra("latitude", alert.latitude)
            putExtra("longitude", alert.longitude)
            putExtra("alert_type", alert.alertType)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setWindow(
            AlarmManager.RTC_WAKEUP,
            alert.startTime,
            alert.endTime - alert.startTime,
            pendingIntent
        )
    }

    private fun cancelAlarm(alert: AlertEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}