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
import com.example.weather_forecast.R
import com.example.weather_forecast.databinding.ItemDaysWeatherBinding
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.pojos.getIconResId
import java.text.SimpleDateFormat
import java.util.*


class HomeWeatherAdapter(private val context: Context, var weatherEntity: List<WeatherEntity>) : RecyclerView.Adapter<HomeWeatherAdapter.WeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemDaysWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeatherViewHolder(binding,context)
    }

    override fun getItemCount(): Int {
        return weatherEntity.size
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
//        holder.bind(getItem(position))
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
        val binding: ItemDaysWeatherBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(weather: WeatherEntity) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val temperatureUnit = prefs.getString("temperature_unit", "Celsius") ?: "Celsius"
            val temperatureUnitDisplay = when (temperatureUnit) {
                "Celsius" -> context.getString(R.string.temperature_celsius)
                "Fahrenheit" -> context.getString(R.string.temperature_fahrenheit)
                "Kelvin" -> context.getString(R.string.temperature_kelvin)
                else -> context.getString(R.string.temperature_celsius)
            }
            binding.apply {
                Date.text = SimpleDateFormat("EEE", Locale.getDefault())
                    .format(Date(weather.dt * 1000))
                val temp = when (temperatureUnit) {
                    "Celsius" -> weather.mainTemp
                    "Fahrenheit" -> (weather.mainTemp * 9 / 5) + 32
                    "Kelvin" -> weather.mainTemp + 273.15
                    else -> weather.mainTemp
                }
                minMaxTemp.text = "${String.format("%.1f", temp)}${getTemperatureUnitSymbol(temperatureUnitDisplay)}"
                mainStatus.text = weather.weatherMain
            }
        }

        private fun getTemperatureUnitSymbol(unit: String): String {
            return when (unit) {
                "Celsius" -> "°C"
                "Fahrenheit" -> "°F"
                "Kelvin" -> "K"
                "سيلزيوس" -> "°س"
                "كلفن" -> "°ك"
                "فهرنهايت" -> "°ف"
                else -> "°C"
            }
        }
    }
}