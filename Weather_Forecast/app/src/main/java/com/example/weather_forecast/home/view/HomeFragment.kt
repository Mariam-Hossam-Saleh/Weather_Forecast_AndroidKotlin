package com.example.weather_forecast.home.view

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.weather_forecast.MapActivity
import com.example.weather_forecast.R
import com.example.weather_forecast.databinding.FragmentHomeBinding
import com.example.weather_forecast.home.viewmodel.HomeViewModel
import com.example.weather_forecast.home.viewmodel.HomeViewModelFactory
import com.example.weather_forecast.model.database.WeatherDatabase
import com.example.weather_forecast.model.database.WeatherLocalDataSourceImp
import com.example.weather_forecast.model.network.RetrofitHelper
import com.example.weather_forecast.model.network.WeatherRemoteDataSourceImp
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.pojos.getIconResId
import com.example.weather_forecast.model.pojos.getWeatherStateResId
import com.example.weather_forecast.model.repo.WeatherRepositoryImp
import com.example.weather_forecast.utils.NetworkUtils
import com.example.weather_forecast.utils.location.LocationPermissionHandler
import com.example.weather_forecast.viewmodel.LocationSourceViewModel
import androidx.preference.PreferenceManager
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(), OnWeatherClickListener {

    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var homeTodayRecyclerView: RecyclerView
    private lateinit var homeWeatherAdapter: HomeWeatherAdapter
    private lateinit var homeTodayAdapter: HomeTodayAdapter
    private lateinit var homeWeatherLayoutManager: LinearLayoutManager
    private lateinit var homeTodayLayoutManager: LinearLayoutManager
    private lateinit var binding: FragmentHomeBinding
    private lateinit var locationHandler: LocationPermissionHandler
    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null
    private var currentCityName: String? = null
    private var isFromSearchFragment: Boolean = false
    private var isFromMapActivity: Boolean = false
    private var isFromMapSelection: Boolean = false
    private val locationSourceViewModel: LocationSourceViewModel by activityViewModels()

    private val mapActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.let { data ->
                val latitude = data.getDoubleExtra("latitude", 0.0)
                val longitude = data.getDoubleExtra("longitude", 0.0)
                val source = data.getStringExtra("source")
                if (latitude != 0.0 && longitude != 0.0 && source == "map") {
                    val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    prefs.edit()
                        .putFloat("lastMapLatitude", latitude.toFloat())
                        .putFloat("lastMapLongitude", longitude.toFloat())
                        .apply()
                    isFromMapActivity = true
                    isFromSearchFragment = false
                    isFromMapSelection = false
                    lastLatitude = latitude
                    lastLongitude = longitude
                    fetchWeatherForLocation(latitude, longitude)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        savedInstanceState?.let {
            lastLatitude = it.getDouble("lastLatitude", 0.0).takeIf { d -> d != 0.0 }
            lastLongitude = it.getDouble("lastLongitude", 0.0).takeIf { d -> d != 0.0 }
            isFromSearchFragment = it.getBoolean("isFromSearchFragment", false)
            isFromMapActivity = it.getBoolean("isFromMapActivity", false)
            isFromMapSelection = it.getBoolean("isFromMapSelection", false)
        }
        arguments?.let {
            val selectedLat = it.getDouble("selected_lat", 0.0)
            val selectedLon = it.getDouble("selected_lon", 0.0)
            isFromMapSelection = it.getBoolean("isFromMapSelection", false)
            if (selectedLat != 0.0 && selectedLon != 0.0) {
                isFromSearchFragment = true
                lastLatitude = selectedLat
                lastLongitude = selectedLon
            }
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.service, requireContext()),
                WeatherLocalDataSourceImp(WeatherDatabase.getInstance(requireContext()).weatherDao())
            )
        )
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]

        locationHandler = LocationPermissionHandler(
            fragment = this,
            onLocationFetched = { latitude, longitude ->
                Log.d("HomeFragment", "Location fetched: lat=$latitude, lon=$longitude")
                if (!isFromSearchFragment && !isFromMapActivity && hasLocationChanged(latitude, longitude)) {
                    lastLatitude = latitude
                    lastLongitude = longitude
                    fetchWeatherForLocation(latitude, longitude)
                }
                binding.cardAllowLocation.visibility = View.GONE
                binding.cardView.visibility = View.VISIBLE
                binding.todayRecycleView.visibility = View.VISIBLE
                binding.nextDaysRecycleView.visibility = View.VISIBLE
                binding.detailsContainer.visibility = View.VISIBLE
            },
            onShowAllowLocationCard = {
                Log.d("HomeFragment", "Showing Allow Location card due to permission denial")
                binding.cardAllowLocation.visibility = View.VISIBLE
                binding.cardView.visibility = View.GONE
                binding.todayRecycleView.visibility = View.GONE
                binding.nextDaysRecycleView.visibility = View.GONE
                binding.detailsContainer.visibility = View.GONE
            }
        )

        setUpRecyclerView()
        setCurrentWeatherInfo()

        // Observe location source changes
        locationSourceViewModel.locationSource.observe(viewLifecycleOwner) { source ->
            Log.d("HomeFragment", "Location source updated: $source")
            initLocationLogic(source)
        }
    }

    private fun initLocationLogic(locationSource: String) {
        Log.d("HomeFragment", "Initializing location logic with source=$locationSource")
        if (isFromSearchFragment) {
            lastLatitude?.let { lat ->
                lastLongitude?.let { lon ->
                    fetchWeatherForLocation(lat, lon)
                }
            }
        } else if (locationSource == "map") {
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val savedLat = prefs.getFloat("lastMapLatitude", 0f).toDouble()
            val savedLon = prefs.getFloat("lastMapLongitude", 0f).toDouble()
            if (savedLat != 0.0 && savedLon != 0.0 && !isFromMapActivity) {
                lastLatitude = savedLat
                lastLongitude = savedLon
                fetchWeatherForLocation(savedLat, savedLon)
            } else if (!isFromMapActivity) {
                val intent = Intent(requireContext(), MapActivity::class.java).apply {
                    putExtra("latitude", lastLatitude ?: 0.0)
                    putExtra("longitude", lastLongitude ?: 0.0)
                    putExtra("address", "")
                }
                mapActivityLauncher.launch(intent)
            } else {
                lastLatitude?.let { lat ->
                    lastLongitude?.let { lon ->
                        fetchWeatherForLocation(lat, lon)
                    }
                }
            }
        } else if (locationSource == "gps") {
            Log.d("HomeFragment", "Requesting location permission for GPS")
            locationHandler.requestLocationPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        // Only handle non-first-launch updates to avoid duplicate calls
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (isFromSearchFragment || isFromMapActivity) {
            lastLatitude?.let { lat ->
                lastLongitude?.let { lon ->
                    fetchWeatherForLocation(lat, lon)
                }
            }
        } else if (prefs.getString("location_source", "gps") == "gps" && !isFromMapActivity) {
            locationHandler.fetchCurrentLocation()
        }
    }

    override fun onStop() {
        super.onStop()
        lastLatitude = null
        lastLongitude = null
        isFromSearchFragment = false
        isFromMapActivity = false
        isFromMapSelection = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("lastLatitude", lastLatitude ?: 0.0)
        outState.putDouble("lastLongitude", lastLongitude ?: 0.0)
        outState.putBoolean("isFromSearchFragment", isFromSearchFragment)
        outState.putBoolean("isFromMapActivity", isFromMapActivity)
        outState.putBoolean("isFromMapSelection", isFromMapSelection)
    }

    private fun setUpRecyclerView() {
        homeWeatherLayoutManager = LinearLayoutManager(requireContext())
        homeWeatherLayoutManager.orientation = RecyclerView.VERTICAL
        homeWeatherAdapter = HomeWeatherAdapter(requireContext(), ArrayList())
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

    @SuppressLint("SetTextI18n")
    private fun setCurrentWeatherInfo() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val temperatureUnit = prefs.getString("temperature_unit", "Celsius") ?: "Celsius"
        val windSpeedUnit = prefs.getString("wind_speed_unit", "m/s") ?: "m/s"
        val pressureUnit = prefs.getString("pressure_unit", "hPa") ?: "hPa"
        val visibilityUnit = prefs.getString("visibility_unit", "Meters") ?: "Meters"

        val temperatureUnitDisplay = when (temperatureUnit) {
            "Celsius" -> getString(R.string.temperature_celsius)
            "Fahrenheit" -> getString(R.string.temperature_fahrenheit)
            "Kelvin" -> getString(R.string.temperature_kelvin)
            else -> getString(R.string.temperature_celsius)
        }
        val windSpeedUnitDisplay = when (windSpeedUnit) {
            "m/s" -> getString(R.string.wind_speed_ms)
            "km/h" -> getString(R.string.wind_speed_kmh)
            "mph" -> getString(R.string.wind_speed_mph)
            else -> getString(R.string.wind_speed_ms)
        }
        val pressureUnitDisplay = when (pressureUnit) {
            "hPa" -> getString(R.string.pressure_hpa)
            "mb" -> getString(R.string.pressure_mb)
            "in Hg" -> getString(R.string.pressure_inhg)
            "mm Hg" -> getString(R.string.pressure_mmhg)
            else -> getString(R.string.pressure_hpa)
        }
        val visibilityUnitDisplay = when (visibilityUnit) {
            "Meters" -> getString(R.string.visibility_meters)
            "Kilometers" -> getString(R.string.visibility_kilometers)
            "Miles" -> getString(R.string.visibility_miles)
            else -> getString(R.string.visibility_meters)
        }

        homeViewModel.todayWeather.observe(viewLifecycleOwner) { currentWeather ->
            currentCityName = currentWeather?.cityName ?: "Unknown City"
            homeViewModel.fetchFavoriteStateForCity(currentCityName!!)
            homeViewModel.getDailyWeatherByCity(currentCityName!!)
            if (currentWeather != null) {
                binding.apply {
                    val temp = when (temperatureUnit) {
                        "Celsius" -> currentWeather.mainTemp
                        "Fahrenheit" -> (currentWeather.mainTemp * 9 / 5) + 32
                        "Kelvin" -> currentWeather.mainTemp + 273.15
                        else -> currentWeather.mainTemp
                    }
                    currentTemp.text = "${String.format("%.1f", temp.toDouble())}${getTemperatureUnitSymbol(temperatureUnit)}"

                    val feelsLike = when (temperatureUnit) {
                        "Celsius" -> currentWeather.mainFeels_like
                        "Fahrenheit" -> (currentWeather.mainFeels_like * 9 / 5) + 32
                        "Kelvin" -> currentWeather.mainFeels_like + 273.15
                        else -> currentWeather.mainFeels_like
                    }
                    currentFeelsLike.text = "${String.format("%.1f", feelsLike.toDouble())}${getTemperatureUnitSymbol(temperatureUnit)}"

                    val windSpeed = when (windSpeedUnit) {
                        "m/s" -> currentWeather.windSpeed
                        "km/h" -> currentWeather.windSpeed * 3.6
                        "mph" -> currentWeather.windSpeed * 2.23694
                        else -> currentWeather.windSpeed
                    }
                    currentWindSpeed.text = "${String.format("%.1f", windSpeed.toDouble())} $windSpeedUnitDisplay"

                    val pressure = when (pressureUnit) {
                        "hPa" -> currentWeather.mainPressure.toDouble()
                        "mb" -> currentWeather.mainPressure.toDouble()
                        "in Hg" -> currentWeather.mainPressure * 0.02953
                        "mm Hg" -> currentWeather.mainPressure * 0.75006
                        else -> currentWeather.mainPressure.toDouble()
                    }
                    currentPressure.text = "${String.format("%.1f", pressure)} $pressureUnitDisplay"

                    val visibility = when (visibilityUnit) {
                        "Meters" -> currentWeather.visibility.toDouble()
                        "Kilometers" -> currentWeather.visibility / 1000.0
                        "Miles" -> currentWeather.visibility / 1609.34
                        else -> currentWeather.visibility.toDouble()
                    }
                    currentVisibility.text = "${String.format("%.1f", visibility)} $visibilityUnitDisplay"

                    currentState.text = currentWeather.weatherMain
                    currentDateAndTime.text = SimpleDateFormat("EEE, MMM d, HH:mm", Locale.getDefault())
                        .format(Date(currentWeather.dt * 1000))
                    currentCity.text = currentCityName
                    humidity.text = "${currentWeather.mainHumidity}%"
                    sunrise.text = formatUnixTimeToLocalTime(currentWeather.sysSunrise)
                    sunset.text = formatUnixTimeToLocalTime(currentWeather.sysSunset)
                    clouds.text = "${currentWeather.clouds}%"
                    Log.i("TempMinMax", "Min: ${currentWeather.mainTemp_min}, Max: ${currentWeather.mainTemp_max}")
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
                Log.d("HomeFragment", "Current weather is null")
                if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                    binding.apply {
                        currentTemp.text = getString(R.string.no_network)
                        currentState.text = ""
                        currentDateAndTime.text = ""
                        imgIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.nowifi))
                        todayRecycleView.visibility = View.GONE
                        nextDaysRecycleView.visibility = View.GONE
                        detailsContainer.visibility = View.GONE
                        requireActivity().findViewById<View>(R.id.app_bar_main)
                            .setBackgroundResource(R.drawable.clear1)
                    }
                }
            }
        }

        homeViewModel.weatherList.observe(viewLifecycleOwner) { weatherList ->
            homeTodayAdapter.weatherEntity = weatherList ?: emptyList()
            homeTodayAdapter.notifyDataSetChanged()
            if (currentCityName != null) {
                homeViewModel.getDailyWeatherByCity(currentCityName!!)
            }
        }

        homeViewModel.dailyWeatherList.observe(viewLifecycleOwner) { dailyWeatherList ->
            homeWeatherAdapter.weatherEntity = dailyWeatherList ?: emptyList()
            homeWeatherAdapter.notifyDataSetChanged()
        }

        homeViewModel.favoriteState.observe(viewLifecycleOwner) { weatherEntity ->
            Log.d("HomeFragment", "Favorite state changed: $weatherEntity")
            if (weatherEntity != null) {
                binding.btnFavorite.setImageResource(
                    if (weatherEntity.isFavorite) R.drawable.favourite_colored
                    else R.drawable.favourite
                )
            } else {
                binding.btnFavorite.setImageResource(R.drawable.favourite)
            }
        }

        homeViewModel.favoriteToggleResult.observe(viewLifecycleOwner) { isFavorite ->
            isFavorite?.let {
                Toast.makeText(
                    requireContext(),
                    getString(
                        if (it) R.string.added_to_favorites else R.string.removed_from_favorites,
                        currentCityName
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.e("HomeFragment", "Error: $it")
            }
        }

        binding.btnEnableLocation.setOnClickListener {
            Log.d("HomeFragment", "Enable Location button clicked")
            locationHandler.promptToEnableLocation()
        }

        binding.btnRequestLocation.setOnClickListener {
            Log.d("HomeFragment", "Enable Location services button clicked")
            locationHandler.requestLocationPermission()
        }

        binding.btnFavorite.setOnClickListener {
            if (lastLatitude != null && lastLongitude != null && currentCityName != null) {
                homeViewModel.toggleFavoriteStatus(currentCityName!!, lastLatitude!!, lastLongitude!!)
            } else {
                Toast.makeText(requireContext(), R.string.no_location, Toast.LENGTH_SHORT).show()
            }
        }

        binding.settings.setOnClickListener {
            Log.d("HomeFragment", "Settings button clicked")
            val navController = NavHostFragment.findNavController(this@HomeFragment)
            navController.navigate(R.id.action_nav_home_to_nav_settings)
            Toast.makeText(requireContext(), R.string.settings_clicked, Toast.LENGTH_SHORT).show()
        }

        binding.search.setOnClickListener {
            Log.d("HomeFragment", "Search button clicked")
            val bundle = Bundle().apply {
                putDouble("selected_lat", lastLatitude ?: 0.0)
                putDouble("selected_lon", lastLongitude ?: 0.0)
            }
            val navController = NavHostFragment.findNavController(this@HomeFragment)
            navController.navigate(R.id.action_nav_home_to_nav_search, bundle)
            Toast.makeText(requireContext(), R.string.manage_cities, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTemperatureUnitSymbol(unit: String): String {
        return when (unit) {
            "Celsius", getString(R.string.temperature_celsius) -> "°C"
            "Fahrenheit", getString(R.string.temperature_fahrenheit) -> "°F"
            "Kelvin", getString(R.string.temperature_kelvin) -> "K"
            else -> "°C"
        }
    }

    private fun formatUnixTimeToLocalTime(dt: Long): String {
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
        val dateTime = Instant.ofEpochSecond(dt)
            .atZone(ZoneId.systemDefault())
        return dateTime.format(formatter)
    }

    private fun fetchWeatherForLocation(lat: Double, lon: Double) {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            Log.d("HomeFragment", "Network available, fetching fresh data for lat=$lat, lon=$lon")
            homeViewModel.fetchWeather(lat, lon)
            homeViewModel.fetchCurrentWeather(lat, lon)
        } else {
            Log.d("HomeFragment", "No network, attempting to show cached data")
            homeViewModel.getStoredCityWeather(lat, lon)
            homeViewModel.getStoredCurrentWeather(lat, lon)
            if (currentCityName != null) {
                homeViewModel.getDailyWeatherByCity(currentCityName!!)
            }
            Toast.makeText(requireContext(), R.string.no_internet, Toast.LENGTH_LONG).show()
        }
    }

    private fun hasLocationChanged(newLat: Double, newLon: Double): Boolean {
        if (lastLatitude == null || lastLongitude == null) return true
        val distance = FloatArray(1)
        android.location.Location.distanceBetween(lastLatitude!!, lastLongitude!!, newLat, newLon, distance)
        return distance[0] > 1000
    }

    override fun onWeatherClick(weather: WeatherEntity) {
        Toast.makeText(requireContext(), "Click Listener", Toast.LENGTH_SHORT).show()
    }
}