package com.example.weather_forecast.alert.view

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_forecast.MapActivity
import com.example.weather_forecast.R
import com.example.weather_forecast.alert.viewmodel.WeatherAlertViewModel
import com.example.weather_forecast.alert.viewmodel.WeatherAlertViewModelFactory
import com.example.weather_forecast.databinding.FragmentWeatherAlertBinding
import com.example.weather_forecast.model.database.WeatherDatabase
import com.example.weather_forecast.model.database.WeatherLocalDataSourceImp
import com.example.weather_forecast.model.network.RetrofitHelper
import com.example.weather_forecast.model.network.WeatherRemoteDataSourceImp
import com.example.weather_forecast.model.pojos.AlertEntity
import com.example.weather_forecast.model.repo.WeatherRepositoryImp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class WeatherAlertFragment : Fragment() {

    private lateinit var binding: FragmentWeatherAlertBinding
    private lateinit var viewModel: WeatherAlertViewModel
    private lateinit var alertsAdapter: AlertsAdapter
    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0

    private val mapActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                selectedLatitude = data.getDoubleExtra("latitude", 0.0)
                selectedLongitude = data.getDoubleExtra("longitude", 0.0)
                if (selectedLatitude != 0.0 && selectedLongitude != 0.0) {
                    Toast.makeText(requireContext(), R.string.location_selected, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupViewModel() {
        val repository = WeatherRepositoryImp.getInstance(
            WeatherRemoteDataSourceImp(RetrofitHelper.service, requireContext()),
            WeatherLocalDataSourceImp(WeatherDatabase.getInstance(requireContext()).weatherDao())
        )
        viewModel = ViewModelProvider(
            this,
            WeatherAlertViewModelFactory(
                repository,
                requireContext()
            )
        )[WeatherAlertViewModel::class.java]
    }

    private fun setupRecyclerView() {
        alertsAdapter = AlertsAdapter(requireContext(), emptyList()) { alert ->
            viewModel.cancelAlert(alert)
            Toast.makeText(requireContext(), R.string.alert_disabled, Toast.LENGTH_SHORT).show()
        }
        binding.alertsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = alertsAdapter
        }
    }

    private fun setupListeners() {
        binding.btnAddAlert.setOnClickListener {
            showAddAlertDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.alerts.observe(viewLifecycleOwner) { alerts ->
            alertsAdapter.updateAlerts(alerts)
        }
    }

    private fun showAddAlertDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_alert, null)
        val spinnerAlertType = dialogView.findViewById<Spinner>(R.id.spinner_alert_type)
        val btnSelectLocation = dialogView.findViewById<Button>(R.id.btn_select_location)
        val etStartDate = dialogView.findViewById<EditText>(R.id.et_start_date)
        val etStartTime = dialogView.findViewById<EditText>(R.id.et_start_time)
        val etEndDate = dialogView.findViewById<EditText>(R.id.et_end_date)
        val etEndTime = dialogView.findViewById<EditText>(R.id.et_end_time)

        // Setup alert type spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.alert_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAlertType.adapter = adapter
        }

        // Setup date and time pickers
        val calendar = Calendar.getInstance()
        etStartDate.setOnClickListener { showDatePicker(etStartDate, calendar) }
        etStartTime.setOnClickListener { showTimePicker(etStartTime, calendar) }
        etEndDate.setOnClickListener { showDatePicker(etEndDate, calendar) }
        etEndTime.setOnClickListener { showTimePicker(etEndTime, calendar) }

        // Location selection
        btnSelectLocation.setOnClickListener {
            val intent = Intent(requireContext(), MapActivity::class.java).apply {
                putExtra("latitude", selectedLatitude)
                putExtra("longitude", selectedLongitude)
                putExtra("address", "")
            }
            mapActivityLauncher.launch(intent)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_weather_alert)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val alertType = spinnerAlertType.selectedItem.toString()
                val startDate = etStartDate.text.toString()
                val startTime = etStartTime.text.toString()
                val endDate = etEndDate.text.toString()
                val endTime = etEndTime.text.toString()

                if (selectedLatitude == 0.0 || selectedLongitude == 0.0 || startDate.isEmpty() || startTime.isEmpty() || endDate.isEmpty() || endTime.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val startMillis = parseDateTime(startDate, startTime)
                val endMillis = parseDateTime(endDate, endTime)
                if (startMillis >= endMillis || startMillis < System.currentTimeMillis()) {
                    Toast.makeText(requireContext(), R.string.invalid_date_time, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val alert = AlertEntity(
                    id = UUID.randomUUID().toString(),
                    latitude = selectedLatitude,
                    longitude = selectedLongitude,
                    startTime = startMillis,
                    endTime = endMillis,
                    alertType = alertType,
                    isActive = true
                )
                viewModel.scheduleAlert(alert)
                Toast.makeText(requireContext(), R.string.alert_scheduled, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDatePicker(editText: EditText, calendar: Calendar) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                editText.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(editText: EditText, calendar: Calendar) {
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                editText.setText(String.format("%02d:%02d", hour, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun parseDateTime(date: String, time: String): Long {
        val dateTimeString = "$date $time"
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            sdf.parse(dateTimeString)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}