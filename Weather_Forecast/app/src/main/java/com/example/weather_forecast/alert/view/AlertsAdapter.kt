package com.example.weather_forecast.alert.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_forecast.R
import com.example.weather_forecast.model.pojos.AlertEntity
import java.text.SimpleDateFormat
import java.util.Locale

class AlertsAdapter(
    private val context: Context,
    private var alerts: List<AlertEntity>,
    private val onDisableClick: (AlertEntity) -> Unit
) : RecyclerView.Adapter<AlertsAdapter.AlertViewHolder>() {

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLocation: TextView = itemView.findViewById(R.id.tv_location)
        val tvTimeRange: TextView = itemView.findViewById(R.id.tv_time_range)
        val tvAlertType: TextView = itemView.findViewById(R.id.tv_alert_type)
        val btnDisable: Button = itemView.findViewById(R.id.btn_disable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]
        holder.tvLocation.text = context.getString(R.string.location_lat_lon, alert.latitude, alert.longitude)
        holder.tvTimeRange.text = context.getString(
            R.string.time_range,
            formatTime(alert.startTime),
            formatTime(alert.endTime)
        )
        holder.tvAlertType.text = alert.alertType
        holder.btnDisable.setOnClickListener { onDisableClick(alert) }
    }

    override fun getItemCount(): Int = alerts.size

    fun updateAlerts(newAlerts: List<AlertEntity>) {
        alerts = newAlerts
        notifyDataSetChanged()
    }

    private fun formatTime(timeMillis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(timeMillis)
    }
}