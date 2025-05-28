//package com.example.weather_forecast.settings.view
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.example.weather_forecast.databinding.FragmentSettingsBinding
//
//class SettingsFragment : Fragment() {
//
//    private lateinit var binding: FragmentSettingsBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentSettingsBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//}
package com.example.weather_forecast.settings.view

import android.content.res.Configuration
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.weather_forecast.R
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Handle language preference
        val languagePreference = findPreference<ListPreference>("language")
        languagePreference?.setOnPreferenceChangeListener { _, newValue ->
            updateLanguage(newValue as String)
            true
        }

        // Set summaries for all preferences
        updatePreferenceSummary("language", "Language")
        updatePreferenceSummary("wind_speed_unit", "Wind Speed Unit")
        updatePreferenceSummary("pressure_unit", "Pressure Unit")
        updatePreferenceSummary("temperature_unit", "Temperature Unit")
        updatePreferenceSummary("elevation_unit", "Elevation Unit")
        updatePreferenceSummary("visibility_unit", "Visibility Unit")
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
        requireActivity().recreate() // Restart activity to apply language
    }

    private fun updatePreferenceSummary(key: String, title: String) {
        val preference = findPreference<ListPreference>(key)
        preference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
    }
}