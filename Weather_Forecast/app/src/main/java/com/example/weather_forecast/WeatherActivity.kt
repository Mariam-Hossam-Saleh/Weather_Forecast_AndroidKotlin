//package com.example.weather_forecast
//
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.weather_forecast.model.database.WeatherDatabase
//import com.example.weather_forecast.model.database.WeatherLocalDataSourceImp
//import com.example.weather_forecast.databinding.ActivityWeatherBinding
//import com.example.weather_forecast.model.repo.WeatherRepository
//import com.example.weather_forecast.model.repo.WeatherRepositoryImp
//import com.example.weather_forecast.model.network.RetrofitHelper
//import com.example.weather_forecast.model.network.WeatherRemoteDataSourceImp
//import com.example.weather_forecast.model.network.WeatherService
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class WeatherActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityWeatherBinding
//    private val viewModel: WeatherViewModel by viewModels {
//        WeatherViewModelFactory(
//            WeatherRepositoryImp.getInstance(
//                WeatherRemoteDataSourceImp(RetrofitHelper.service),
//                WeatherLocalDataSourceImp(WeatherDatabase.getInstance(this).weatherDao())
//            )
//        )
//    }
//    private lateinit var weatherAdapter: WeatherAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityWeatherBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupRecyclerView()
//        setupObservers()
//
//        // Default location (London)
//        viewModel.fetchWeather(30.8025, 26.8206)
//        viewModel.fetchCurrentWeather(30.8025, 26.8206)
//
//        binding.btnFetch.setOnClickListener {
//            val lat = binding.etLatitude.text.toString().toDoubleOrNull() ?: 51.5074
//            val lon = binding.etLongitude.text.toString().toDoubleOrNull() ?: -0.1278
//            viewModel.fetchWeather(lat, lon)
//        }
//    }
//
//    private fun setupRecyclerView() {
//        weatherAdapter = WeatherAdapter { weatherItem ->
//            viewModel.onItemClicked(weatherItem)
//            Toast.makeText(this, "Saved weather data", Toast.LENGTH_SHORT).show()
//        }
//        binding.rvWeather.apply {
//            layoutManager = LinearLayoutManager(this@WeatherActivity)
//            adapter = weatherAdapter
//        }
//    }
//
//    private fun setupObservers() {
//        viewModel.weatherList.observe(this, Observer { weatherList ->
//            weatherAdapter.submitList(weatherList)
//        })
//
//        viewModel.isLoading.observe(this, Observer { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//        })
//
//        viewModel.errorMessage.observe(this, Observer { errorMessage ->
//            errorMessage?.let {
//                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
//            }
//        })
//    }
//
//    private fun createWeatherService(): WeatherService {
//        return Retrofit.Builder()
//            .baseUrl("https://api.openweathermap.org/data/2.5/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(WeatherService::class.java)
//    }
//}
//
//class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return WeatherViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
