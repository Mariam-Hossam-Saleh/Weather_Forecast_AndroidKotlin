package com.example.weather_forecast.search.view

import WeatherRemoteDataSourceImp
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_forecast.MapActivity
import com.example.weather_forecast.R
import com.example.weather_forecast.databinding.FragmentSearchBinding
import com.example.weather_forecast.model.database.WeatherDatabase
import com.example.weather_forecast.model.database.WeatherLocalDataSourceImp
import com.example.weather_forecast.model.network.RetrofitHelper
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.repo.WeatherRepositoryImp
import com.example.weather_forecast.search.viewmodel.SearchViewModel
import com.example.weather_forecast.search.viewmodel.SearchViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class SearchFragment : Fragment(), OnLocationClickListener {

    private lateinit var searchViewModelFactory: SearchViewModelFactory
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchLayoutManager: LinearLayoutManager
    private lateinit var binding: FragmentSearchBinding

    private lateinit var fusedClient : FusedLocationProviderClient
    private val LOCATION_ID = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewModelFactory = SearchViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.service, requireContext()),
                WeatherLocalDataSourceImp(WeatherDatabase.getInstance(requireContext()).weatherDao())
            )
        )

        searchViewModel = ViewModelProvider(this, searchViewModelFactory)[SearchViewModel::class.java]

        setUpRecyclerView()

        searchViewModel.weatherList.observe(viewLifecycleOwner) { weatherList ->
            searchAdapter.weatherEntity = weatherList ?: emptyList()
            searchAdapter.notifyDataSetChanged()
        }

        requireActivity().findViewById<View>(R.id.app_bar_main)
            .setBackgroundResource(R.color.white)

        binding.btnMAP.setOnClickListener{
//            openMapView
        }
    }


    private fun setUpRecyclerView() {
        searchLayoutManager = LinearLayoutManager(requireContext())
        searchLayoutManager.orientation = RecyclerView.VERTICAL
        searchAdapter = SearchAdapter(requireContext(), ArrayList(), this)
        searchRecyclerView = binding.searchRecycleView
        searchRecyclerView.setHasFixedSize(true)
        searchRecyclerView.adapter = searchAdapter
        searchRecyclerView.layoutManager = searchLayoutManager
    }

    override fun onLocationClick(weather: WeatherEntity) {
        Toast.makeText(requireContext(), "Click Listener", Toast.LENGTH_SHORT).show()

    }

//    private fun openMapView(latitude: Double, longitude: Double, address: String) {
//        try {
//            val intent = Intent(this, MapActivity::class.java).apply {
//                putExtra("latitude", latitude)
//                putExtra("longitude", longitude)
//                putExtra("address", address)
//            }
//            startActivity(intent)
//        } catch (e: Exception) {
//            Log.e("Map", "Error opening Map view: ${e.message}")
//            Toast.makeText(this, "Failed to open Map", Toast.LENGTH_SHORT).show()
//        }
//    }

}