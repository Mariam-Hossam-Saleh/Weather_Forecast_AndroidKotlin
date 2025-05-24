package com.example.weather_forecast.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.weather_forecast.databinding.ItemWeatherBinding
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.pojos.getIconResId
import java.text.SimpleDateFormat
import java.util.*


class HomeAdapter(private val context: Context,var weatherEntity: List<WeatherEntity>,private val onItemClick: OnWeatherClickListener) : RecyclerView.Adapter<HomeAdapter.WeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemWeatherBinding.inflate(
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
//        holder.bind(getItem(position))
        Glide.with(context)
            .load(getIconResId(weatherEntity.get(position).icon))
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
        val binding: ItemWeatherBinding,
        val onItemClick: OnWeatherClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(weather: WeatherEntity) {
            binding.apply {
                Date.text = SimpleDateFormat("EEE", Locale.getDefault())
                    .format(Date(weather.dt * 1000))
                minMaxTemp.text = "${weather.temp}/${weather.temp}°C"
                mainStatus.text = weather.weatherMain
//                tvDescription.text = weather.weatherDescription
//                tvHumidity.text = "Humidity: ${weather.humidity}%"
                root.setOnClickListener {
                    onItemClick.onWeatherClick(weather)
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