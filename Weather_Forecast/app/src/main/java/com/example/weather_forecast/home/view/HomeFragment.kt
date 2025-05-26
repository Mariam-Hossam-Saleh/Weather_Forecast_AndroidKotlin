package com.example.weather_forecast.home.view

import WeatherRemoteDataSourceImp
import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.weather_forecast.R
import com.example.weather_forecast.databinding.FragmentHomeBinding
import com.example.weather_forecast.home.viewmodel.HomeViewModel
import com.example.weather_forecast.home.viewmodel.HomeViewModelFactory
import com.example.weather_forecast.model.database.WeatherDatabase
import com.example.weather_forecast.model.database.WeatherLocalDataSourceImp
import com.example.weather_forecast.model.network.RetrofitHelper
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.pojos.getIconResId
import com.example.weather_forecast.model.pojos.getWeatherStateResId
import com.example.weather_forecast.model.repo.WeatherRepositoryImp
import com.example.weather_forecast.utils.location.LocationPermissionHandler
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(), OnWeatherClickListener {

    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var homeTodayRecyclerView: RecyclerView
    private lateinit var homeWeatherAdapter: HomeWeatherAdapter
    private lateinit var homeTodayAdapter: HomeTodayAdapter
    private lateinit var homeWeatherLayoutManager: LinearLayoutManager
    private lateinit var homeTodayLayoutManager: LinearLayoutManager
    private lateinit var binding: FragmentHomeBinding
    private lateinit var locationHandler: LocationPermissionHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.service, requireContext()),
                WeatherLocalDataSourceImp(WeatherDatabase.getInstance(requireContext()).weatherDao())
            )
        )
        viewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]

        // Initialize LocationPermissionHandler
        locationHandler = LocationPermissionHandler(
            fragment = this,
            onLocationFetched = { latitude, longitude ->
                Log.d("HomeFragment", "Location fetched: lat=$latitude, lon=$longitude")
                viewModel.fetchWeather(latitude, longitude)
                viewModel.fetchCurrentWeather(latitude, longitude)
                binding.cardAllowLocation.visibility = View.GONE
                binding.cardView.visibility = View.VISIBLE
                binding.todayRecycleView.visibility = View.VISIBLE
                binding.nextDaysRecycleView.visibility = View.VISIBLE
            },

            onShowAllowLocationCard = {
                Log.d("HomeFragment", "Showing Allow Location card due to permission denial")
                binding.cardAllowLocation.visibility = View.VISIBLE
                binding.cardView.visibility = View.GONE
                binding.todayRecycleView.visibility = View.GONE
                binding.nextDaysRecycleView.visibility = View.GONE
            }
        )

        // Set up RecyclerView and observers
        setUpRecyclerView()
        setCurrentWeatherInfo()

        viewModel.weatherList.observe(viewLifecycleOwner) { weatherList ->
            homeWeatherAdapter.weatherEntity = weatherList ?: emptyList()
            homeTodayAdapter.weatherEntity = weatherList ?: emptyList()
            homeWeatherAdapter.notifyDataSetChanged()
            homeTodayAdapter.notifyDataSetChanged()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.e("HomeFragment", "Error: $it")
                // Show cached data if available
//                viewModel.getStoredWeather()
//                viewModel.getStoredCurrentWeather()
                // Ensure UI is visible even on error
                binding.cardAllowLocation.visibility = View.GONE
                binding.cardView.visibility = View.VISIBLE
                binding.todayRecycleView.visibility = View.VISIBLE
                binding.nextDaysRecycleView.visibility = View.VISIBLE
            }
        }

        // Request location permission
        locationHandler.requestLocationPermission()

        binding.btnEnableLocation.setOnClickListener {
            Log.d("HomeFragment", "Enable Location button clicked")
            locationHandler.promptToEnableLocation()
        }

        binding.btnRequestLocation.setOnClickListener {
            Log.d("HomeFragment", "Enable Location services button clicked")
            locationHandler.requestLocationPermission()
        }
    }

    private fun setUpRecyclerView() {
        homeWeatherLayoutManager = LinearLayoutManager(requireContext())
        homeWeatherLayoutManager.orientation = RecyclerView.VERTICAL
        homeWeatherAdapter = HomeWeatherAdapter(requireContext(), ArrayList(), this)
        homeRecyclerView = binding.nextDaysRecycleView
        homeRecyclerView.setHasFixedSize(true)
        homeRecyclerView.adapter = homeWeatherAdapter
        homeRecyclerView.layoutManager = homeWeatherLayoutManager

        homeTodayLayoutManager = LinearLayoutManager(requireContext())
        homeTodayLayoutManager.orientation = RecyclerView.HORIZONTAL
        homeTodayAdapter = HomeTodayAdapter(requireContext(), ArrayList(), this)
        homeTodayRecyclerView = binding.todayRecycleView
        homeTodayRecyclerView.setHasFixedSize(true)
        homeTodayRecyclerView.adapter = homeTodayAdapter
        homeTodayRecyclerView.layoutManager = homeTodayLayoutManager
    }

    private fun setCurrentWeatherInfo() {
        viewModel.todayWeather.observe(viewLifecycleOwner) { currentWeather ->
            if (currentWeather != null) {
                binding.apply {
                    currentTemp.text = "${currentWeather.mainTemp}°C"
                    currentState.text = currentWeather.weatherMain
                    currentDateAndTime.text = SimpleDateFormat("EEE, MMM d, HH:mm", Locale.getDefault())
                        .format(Date(currentWeather.dt * 1000))
                    Glide.with(requireContext())
                        .load(getIconResId(currentWeather.weatherIcon))
                        .apply(
                            RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .transform(CenterCrop(), RoundedCorners(30))
                                .override(200, 200)
                        )
                        .into(imgIcon)
                    requireActivity().findViewById<View>(R.id.app_bar_main)
                        .setBackgroundResource(getWeatherStateResId(currentWeather.weatherMain))
                }
            } else {
                Log.d("HomeFragment", "Current weather is null, skipping UI update")
                binding.apply {
                    currentTemp.text = "N/A"
                    currentState.text = "N/A"
                    imgIcon.setImageDrawable(null)
                    requireActivity().findViewById<View>(R.id.app_bar_main)
                        .setBackgroundResource(R.drawable.clear1) // Use a default background
                }
            }
        }
    }

    override fun onWeatherClick(weather: WeatherEntity) {
        Toast.makeText(requireContext(), "Click Listener", Toast.LENGTH_SHORT).show()
    }
}