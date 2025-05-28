package com.example.weather_forecast.search.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_forecast.MapActivity
import com.example.weather_forecast.R
import com.example.weather_forecast.databinding.FragmentSearchBinding
import com.example.weather_forecast.model.database.WeatherDatabase
import com.example.weather_forecast.model.database.WeatherLocalDataSourceImp
import com.example.weather_forecast.model.network.RetrofitHelper
import com.example.weather_forecast.model.network.WeatherRemoteDataSourceImp.WeatherRemoteDataSourceImp
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.repo.WeatherRepositoryImp
import com.example.weather_forecast.search.viewmodel.SearchViewModel
import com.example.weather_forecast.search.viewmodel.SearchViewModelFactory

class SearchFragment : Fragment(), OnLocationClickListener {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchLayoutManager: LinearLayoutManager
    private lateinit var binding: FragmentSearchBinding
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    private val mapActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val latitude = data.getDoubleExtra("latitude", 0.0)
                val longitude = data.getDoubleExtra("longitude", 0.0)

                if (latitude != 0.0 && longitude != 0.0) {
                    navigateToHomeWithLocation(latitude, longitude)
                } else {
                    Toast.makeText(requireContext(), "Invalid location selected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        lat = requireArguments().getDouble("selected_lat")
        lon = requireArguments().getDouble("selected_lon")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setUpRecyclerView()
        setupUI()
        searchViewModel.getFavoriteWeatherEntities()
    }

    private fun setupViewModel() {
        searchViewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(
                WeatherRepositoryImp.getInstance(
                    WeatherRemoteDataSourceImp(RetrofitHelper.service, requireContext()),
                    WeatherLocalDataSourceImp(WeatherDatabase.getInstance(requireContext()).weatherDao())
                )
            )
        ).get(SearchViewModel::class.java)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setUpRecyclerView() {
        searchLayoutManager = LinearLayoutManager(requireContext())
        searchLayoutManager.orientation = RecyclerView.VERTICAL
        searchAdapter = SearchAdapter(requireContext(), ArrayList(), this)
        searchRecyclerView = binding.searchRecycleView
        searchRecyclerView.setHasFixedSize(true)
        searchRecyclerView.adapter = searchAdapter
        searchRecyclerView.layoutManager = searchLayoutManager

        searchViewModel.favoriteWeatherEntities.observe(viewLifecycleOwner) { weatherList ->
            searchAdapter.weatherEntity = weatherList ?: emptyList()
            searchAdapter.notifyDataSetChanged()
        }
    }


    private fun setupUI() {
        requireActivity().findViewById<View>(R.id.app_bar_main)
            .setBackgroundResource(R.color.white)

        binding.btnMAP.setOnClickListener {
            openMapActivity()
        }

        binding.searchView.onActionViewExpanded()
        binding.searchView.clearFocus()
    }

    private fun openMapActivity() {
        val intent = Intent(requireContext(), MapActivity::class.java).apply {
            putExtra("latitude", lat)
            putExtra("longitude", lon)
        }
        mapActivityLauncher.launch(intent)
    }

    private fun navigateToHomeWithLocation(lat: Double, lon: Double) {
        findNavController().navigate(
            R.id.action_nav_search_to_nav_home,
            Bundle().apply {
                putDouble("selected_lat", lat)
                putDouble("selected_lon", lon)
            }
        )
    }

    override fun onLocationClick(weather: WeatherEntity) {
        findNavController().navigate(
            R.id.action_nav_search_to_nav_home,
            Bundle().apply {
                putDouble("selected_lat", weather.lat)
                putDouble("selected_lon", weather.lon)
            }
        )
        Toast.makeText(requireContext(), "Showing ${weather.cityName} details", Toast.LENGTH_SHORT).show()
    }
}