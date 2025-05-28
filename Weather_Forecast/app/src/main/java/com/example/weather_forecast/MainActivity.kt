package com.example.weather_forecast

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.weather_forecast.databinding.ActivityMainBinding
import com.example.weather_forecast.viewmodel.LocationSourceViewModel
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationSourceViewModel: LocationSourceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedLanguage()
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        locationSourceViewModel = ViewModelProvider(this)[LocationSourceViewModel::class.java]

        // Check if this is the first launch
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            showLocationSourceDialog()
        } else {
            setupContentView()
            // Set initial location source for non-first launch
            val locationSource = prefs.getString("location_source", "gps") ?: "gps"
            locationSourceViewModel.setLocationSource(locationSource)
        }
    }

    private fun setupContentView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun showLocationSourceDialog() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.choose_location_source)
        builder.setMessage(R.string.location_source_message)
        builder.setPositiveButton(R.string.gps) { _, _ ->
            prefs.edit()
                .putString("location_source", "gps")
                .putBoolean("isFirstLaunch", false)
                .apply()
            locationSourceViewModel.setLocationSource("gps")
            setupContentView()
        }
        builder.setNegativeButton(R.string.map) { _, _ ->
            prefs.edit()
                .putString("location_source", "map")
                .putBoolean("isFirstLaunch", false)
                .apply()
            locationSourceViewModel.setLocationSource("map")
            setupContentView()
        }
        builder.setCancelable(false)
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if present
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun applySavedLanguage() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val language = prefs.getString("language", "system") ?: "system"
        val locale = when (language) {
            "en" -> Locale.ENGLISH
            "ar" -> Locale("ar")
            else -> Locale.getDefault()
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

