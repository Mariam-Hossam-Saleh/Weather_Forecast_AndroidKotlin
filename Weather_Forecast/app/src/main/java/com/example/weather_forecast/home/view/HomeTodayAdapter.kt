package com.example.weather_forecast.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.weather_forecast.databinding.ItemCurrentWeatherBinding
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.pojos.getIconResId
import java.text.SimpleDateFormat
import java.util.*


class HomeTodayAdapter(private val context: Context, var weatherEntity: List<WeatherEntity>, private val onItemClick: OnWeatherClickListener) : RecyclerView.Adapter<HomeTodayAdapter.WeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemCurrentWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeatherViewHolder(binding, context)
    }

    override fun getItemCount(): Int {
        return weatherEntity.size.coerceAtMost(8) // Limit to 8 items
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        Glide.with(context)
            .load(getIconResId(weatherEntity.get(position).weatherIcon))
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(CenterCrop(), RoundedCorners(30))
                    .override(200, 200)
//                    .placeholder(R.drawable.loading)
//                    .error(R.drawable.imagefailed)
            )
            .into(holder.binding.imgIcon)
        holder.bind(weatherEntity.get(position))
    }

    class WeatherViewHolder(
        val binding: ItemCurrentWeatherBinding,
        private val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        

        @SuppressLint("SetTextI18n")
        fun bind(weather: WeatherEntity) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val temperatureUnit = prefs.getString("temperature_unit", "Celsius") ?: "Celsius"
            val windSpeedUnit = prefs.getString("wind_speed_unit", "m/s") ?: "m/s"
            val pressureUnit = prefs.getString("pressure_unit", "hPa") ?: "hPa"
            val visibilityUnit = prefs.getString("visibility_unit", "Meters") ?: "Meters"
            binding.apply {
                time.text = SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date(weather.dt * 1000))
                val temprature = when (temperatureUnit) {
                    "Celsius" -> weather.mainTemp
                    "Fahrenheit" -> (weather.mainTemp * 9 / 5) + 32
                    "Kelvin" -> weather.mainTemp + 273.15
                    else -> weather.mainTemp
                }
                temp.text = "${String.format("%.1f", temprature)}${getTemperatureUnitSymbol(temperatureUnit)}"
                // Convert wind speed based on selected unit
                val windspeed = when (windSpeedUnit) {
                    "m/s" -> weather.windSpeed
                    "km/h" -> weather.windSpeed * 3.6
                    "mph" -> weather.windSpeed * 2.23694
                    else -> weather.windSpeed
                }
                windSpeed.text = "${String.format("%.1f", windspeed)} $windSpeedUnit"

            }
        }
        private fun getTemperatureUnitSymbol(unit: String): String {
            return when (unit) {
                "Celsius" -> "°C"
                "Fahrenheit" -> "°F"
                "Kelvin" -> "K"
                else -> "°C"
            }
        }
    }
}
