package com.example.weather_forecast.home.view

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.example.weather_forecast.model.database.WeatherDatabase
import com.example.weather_forecast.model.database.WeatherLocalDataSourceImp
import com.example.weather_forecast.databinding.FragmentHomeBinding
import com.example.weather_forecast.home.viewmodel.HomeViewModel
import com.example.weather_forecast.home.viewmodel.HomeViewModelFactory
import com.example.weather_forecast.model.repo.WeatherRepositoryImp
import com.example.weather_forecast.model.network.RetrofitHelper
import com.example.weather_forecast.model.network.WeatherRemoteDataSourceImp
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.pojos.getIconResId
import com.example.weather_forecast.model.pojos.getWeatherStateResId

class HomeFragment : Fragment(),OnWeatherClickListener {

    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var homeLayoutManager: LinearLayoutManager
    private lateinit var binding: FragmentHomeBinding


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

        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.service),
                WeatherLocalDataSourceImp(WeatherDatabase.getInstance(requireContext()).weatherDao())
            )
        )
        viewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
//        viewModel.fetchWeather(30.8025, 26.8206)
//        viewModel.fetchCurrentWeather(53.2257, 27.1731)
//        viewModel.getStoredWeather()
        setUpRecyclerView()
        setCurrentWeatherInfo()

        viewModel.weatherList.observe(viewLifecycleOwner){ weatherList ->
            homeAdapter.weatherEntity = weatherList
            homeAdapter.notifyDataSetChanged()
        }
    }

//    private fun setupObservers() {
//        viewModel.weatherList.observe(viewLifecycleOwner, Observer { weatherList ->
//            weatherAdapter.submitList(weatherList)
//        })
//
//        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//        })
//
//        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
//            errorMessage?.let {
//                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
//            }
//        })
//    }

    private fun setUpRecyclerView(){
        homeLayoutManager = LinearLayoutManager(requireContext())
        homeLayoutManager.orientation = RecyclerView.VERTICAL
        homeAdapter = HomeAdapter(requireContext(),ArrayList(),this)
        homeRecyclerView = binding.nextDaysRecycleView
        homeRecyclerView.adapter = homeAdapter
        homeRecyclerView.layoutManager = homeLayoutManager
    }


    private fun setCurrentWeatherInfo() {
        viewModel.currentWeather.observe(viewLifecycleOwner) { currentWeather ->
            binding.apply {
                currentTemp.text = "${currentWeather.mainTemp}°C"
                currentState.text = "${currentWeather.weatherMain}"
//                currentHumidity.text = "${currentWeather.humidity}%"
//                currentWindSpeed.text = "${currentWeather.windSpeed} m/s"
//                currentPressure.text = "${currentWeather.pressure} hPa"
//                currentDescription.text = currentWeather.description
                Glide.with(requireContext())
                    .load(getIconResId(currentWeather.weatherIcon))
                    .apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(CenterCrop(), RoundedCorners(30))
                            .override(200, 200)
//                    .placeholder(R.drawable.loading)
//                    .error(R.drawable.imagefailed)
                    )
                    .into(imgIcon)
                requireActivity().findViewById<View>(R.id.app_bar_main).setBackgroundResource(getWeatherStateResId(currentWeather.weatherMain))

            }
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }

    override fun onResume() {
        super.onResume()
//        requireActivity().findViewById<View>(R.id.app_bar_main).setBackgroundResource(getWeatherStateResId())

    }

    override fun onWeatherClick(weather: WeatherEntity) {
        Toast.makeText(requireContext(), "Click Listener", Toast.LENGTH_SHORT).show()
    }
}
