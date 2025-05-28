package com.example.weather_forecast.settings.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.weather_forecast.MapActivity
import com.example.weather_forecast.R
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {
    private val mapActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.let { data ->
                val latitude = data.getDoubleExtra("latitude", 0.0)
                val longitude = data.getDoubleExtra("longitude", 0.0)
                if (latitude != 0.0 && longitude != 0.0) {
                    val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    prefs.edit()
                        .putFloat("lastMapLatitude", latitude.toFloat())
                        .putFloat("lastMapLongitude", longitude.toFloat())
                        .apply()
                }
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val languagePreference = findPreference<ListPreference>("language")
        languagePreference?.setOnPreferenceChangeListener { _, newValue ->
            updateLanguage(newValue as String)
            true
        }

        val locationSourcePreference = findPreference<ListPreference>("location_source")
        locationSourcePreference?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == "map") {
                val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                prefs.edit()
                    .remove("lastMapLatitude")
                    .remove("lastMapLongitude")
                    .apply()
                val intent = Intent(requireContext(), MapActivity::class.java).apply {
                    putExtra("latitude", 0.0)
                    putExtra("longitude", 0.0)
                    putExtra("address", "") // Use empty string
                }
                mapActivityLauncher.launch(intent)
            }
            true
        }

        updatePreferenceSummary("language")
        updatePreferenceSummary("location_source")
        updatePreferenceSummary("wind_speed_unit")
        updatePreferenceSummary("pressure_unit")
        updatePreferenceSummary("temperature_unit")
        updatePreferenceSummary("elevation_unit")
        updatePreferenceSummary("visibility_unit")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundResource(R.drawable.mist2)
        listView.setPadding(0, 140, 0, 0)
    }

    private fun updateLanguage(language: String) {
        val locale = when (language) {
            "en" -> Locale.ENGLISH
            "ar" -> Locale("ar")
            else -> Locale.getDefault()
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        requireActivity().recreate()
    }

    private fun updatePreferenceSummary(key: String) {
        val preference = findPreference<ListPreference>(key)
        preference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
    }
}