package com.example.weather_forecast.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.weather_forecast.databinding.ItemCurrentWeatherBinding
import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
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
        return WeatherViewHolder(binding, onItemClick) // Pass the click listener to ViewHolder
    }

    override fun getItemCount(): Int {
        return weatherEntity.size.coerceAtMost(8) // Limit to 8 items
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
        val binding: ItemCurrentWeatherBinding,
        val onItemClick: OnWeatherClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(currentWeather: WeatherEntity) {
            binding.apply {
                time.text =  SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date(currentWeather.dt * 1000))
                temp.text = "${currentWeather.mainTemp}°C"
                windSpeed.text = "${currentWeather.windSpeed}km/h"
//                tvDescription.text = weather.weatherDescription
//                tvHumidity.text = "Humidity: ${weather.humidity}%"
                root.setOnClickListener {
                    onItemClick.onWeatherClick(currentWeather)
                }
            }
        }
    }
}

//class WeatherDiffCallback : DiffUtil.ItemCallback<WeatherEntity>() {
//    override fun areItemsTheSame(oldItem: WeatherEntity, newItem: WeatherEntity): Boolean {
//        return oldItem.dt == newItem.dt
//    }
//
//    override fun areContentsTheSame(oldItem: WeatherEntity, newItem: WeatherEntity): Boolean {
//        return oldItem == newItem
//    }
//}