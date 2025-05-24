//package com.example.weather_forecast
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.example.weather_forecast.databinding.ItemWeatherBinding
//import com.example.weather_forecast.model.pojos.WeatherEntity
//import java.text.SimpleDateFormat
//import java.util.*
//
//class WeatherAdapter(private val onItemClick: (WeatherEntity) -> Unit) :
//    ListAdapter<WeatherEntity, WeatherAdapter.WeatherViewHolder>(WeatherDiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
//        val binding = ItemWeatherBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return WeatherViewHolder(binding, onItemClick) // Pass the click listener to ViewHolder
//    }
//
//    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    class WeatherViewHolder(
//        private val binding: ItemWeatherBinding,
//        private val onItemClick: (WeatherEntity) -> Unit // Receive click listener
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(weather: WeatherEntity) {
//            binding.apply {
//                tvDate.text = SimpleDateFormat("EEE, MMM d, HH:mm", Locale.getDefault())
//                    .format(Date(weather.dt * 1000))
//                tvTemp.text = "${weather.temp}°C"
//                tvWeather.text = weather.weatherMain
//                tvDescription.text = weather.weatherDescription
//                tvHumidity.text = "Humidity: ${weather.humidity}%"
//                Log.i("AdapterTime",Date(weather.dt * 1000).toString())
//                root.setOnClickListener {
//                    onItemClick(weather) // Now accessible
//                }
//            }
//        }
//    }
//}
//
//class WeatherDiffCallback : DiffUtil.ItemCallback<WeatherEntity>() {
//    override fun areItemsTheSame(oldItem: WeatherEntity, newItem: WeatherEntity): Boolean {
//        return oldItem.dt == newItem.dt
//    }
//
//    override fun areContentsTheSame(oldItem: WeatherEntity, newItem: WeatherEntity): Boolean {
//        return oldItem == newItem
//    }
//}