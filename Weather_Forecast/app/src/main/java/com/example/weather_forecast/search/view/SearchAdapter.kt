package com.example.weather_forecast.search.view

import com.example.weather_forecast.home.view.OnWeatherClickListener

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.weather_forecast.databinding.ItemDaysWeatherBinding
import com.example.weather_forecast.databinding.ItemSearchBinding
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.pojos.getIconResId
import com.example.weather_forecast.model.pojos.getWeatherStateResId
import java.text.SimpleDateFormat
import java.util.*


class SearchAdapter(private val context: Context, var weatherEntity: List<WeatherEntity>, private val onItemClick: OnLocationClickListener) : RecyclerView.Adapter<SearchAdapter.WeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeatherViewHolder(binding, onItemClick) // Pass the click listener to ViewHolder
    }

    override fun getItemCount(): Int {
        return weatherEntity.size
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(weatherEntity.get(position))
    }

    class WeatherViewHolder(
        val binding: ItemSearchBinding,
        private val onItemClick: OnLocationClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(weather: WeatherEntity) {
            binding.apply {
                favItem.background = ContextCompat.getDrawable(root.context, getWeatherStateResId(weather.weatherMain))
                cityName.text = weather.cityName
                minMaxTemp.text = "${weather.mainTemp_min}/${weather.mainTemp_max}°C"
                mainTemp.text = "${weather.mainTemp}°C"
                root.setOnClickListener {
                    onItemClick.onLocationClick(weather)
                }
            }
        }
    }
}
