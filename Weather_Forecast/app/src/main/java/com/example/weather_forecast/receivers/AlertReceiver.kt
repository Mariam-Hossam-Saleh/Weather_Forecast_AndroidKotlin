package com.example.weather_forecast.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weather_forecast.MainActivity
import com.example.weather_forecast.R

class AlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getStringExtra("alert_id") ?: return
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val alertType = intent.getStringExtra("alert_type") ?: "Notification"

        if (alertType == context.getString(R.string.alert_type_notification)) {
            showNotification(context, alertId, latitude, longitude)
        } else {
            playAlarmSound(context)
        }
    }

    private fun showNotification(context: Context, alertId: String, latitude: Double, longitude: Double) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "weather_alert_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.weather_alerts),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val stopIntent = Intent(context, StopAlertReceiver::class.java).apply {
            putExtra("alert_id", alertId)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            alertId.hashCode(),
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mainIntent = Intent(context, MainActivity::class.java)
        val mainPendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_weather_alert)
            .setContentTitle(context.getString(R.string.weather_alerts))
            .setContentText(context.getString(R.string.alert_for_location, latitude, longitude))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(mainPendingIntent)
            .addAction(R.drawable.ic_stop, context.getString(R.string.stop), stopPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(alertId.hashCode(), notification)
    }

    private fun playAlarmSound(context: Context) {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(context, alarmSound)
        ringtone.play()
    }
}