package com.example.weather_forecast.utils.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationPermissionHandler(
    private val fragment: Fragment,
    private val onLocationFetched: (latitude: Double, longitude: Double) -> Unit,
    private val onShowAllowLocationCard: () -> Unit
) {
    private val context: Context = fragment.requireContext()
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Permission request launcher
    private val requestPermissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d("LocationPermissionHandler", "Permission result: $permissions")
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            Log.d("LocationPermissionHandler", "Location permission granted")
            context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
                .edit {
                    putBoolean("location_permission_denied", false)
                }
            fetchCurrentLocation()
        } else {
            Log.d("LocationPermissionHandler", "Location permission denied")
            if (!fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
                    .edit {
                        putBoolean("location_permission_denied", true)
                    }
            }
            handlePermissionDenied()
        }
    }

    // Location services enable launcher
    private val enableLocationLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        Log.d("LocationPermissionHandler", "Returned from location settings")
        if (isLocationEnabled()) {
            onLocationFetched.invoke(0.0, 0.0) // Trigger fetchCurrentLocation
            fetchCurrentLocation()
        } else {
            Log.d("LocationPermissionHandler", "Location services still disabled")
            onShowAllowLocationCard.invoke()
//            Toast.makeText(
//                context,
//                "Location services are still disabled. Please enable them.",
//                Toast.LENGTH_LONG
//            ).show()
        }
    }

    fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("LocationPermissionHandler", "Permissions already granted")
                fetchCurrentLocation()
            }

            fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d("LocationPermissionHandler", "Showing permission rationale")
                Toast.makeText(
                    context,
                    "Location access is needed for accurate weather updates. Please grant permission.",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

            else -> {
                Log.d("LocationPermissionHandler", "Checking if permissions are permanently denied")
                val sharedPrefs = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
                val isPermanentlyDenied =
                    sharedPrefs.getBoolean("location_permission_denied", false)
                if (isPermanentlyDenied) {
                    Toast.makeText(
                        context,
                        "Location permissions are permanently denied. Please enable them in app settings.",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                    }
                    fragment.startActivity(intent)
                } else {
                    Log.d("LocationPermissionHandler", "Requesting location permissions")
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation() {
        if (!isLocationEnabled()) {
            Log.d("LocationPermissionHandler", "Location services disabled, prompting user")
            promptToEnableLocation()
            return
        }

        Log.d("LocationPermissionHandler", "Fetching last known location")
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                Log.d("LocationPermissionHandler", "Last location: $location")
                if (location != null) {
                    onLocationFetched.invoke(location.latitude, location.longitude)
                } else {
                    Log.d(
                        "LocationPermissionHandler",
                        "Last location is null, requesting fresh location"
                    )
                    requestFreshLocation()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationPermissionHandler", "Failed to get location: ${exception.message}")
            }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun requestFreshLocation() {
        Log.d("LocationPermissionHandler", "Requesting fresh location")
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d("LocationPermissionHandler", "Fresh location received: $location")
                    onLocationFetched.invoke(location.latitude, location.longitude)
                    fusedLocationClient.removeLocationUpdates(this)
                } ?: run {
                    Log.d("LocationPermissionHandler", "Fresh location is null")
                    handleLocationNotAvailable()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun promptToEnableLocation() {
        Log.d("LocationPermissionHandler", "Prompting user to enable location services")
        Toast.makeText(
            context,
            "Please enable location services for accurate weather updates.",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        enableLocationLauncher.launch(intent)
    }

    private fun handlePermissionDenied() {
        Log.d("LocationPermissionHandler", "handlePermissionDenied called")
        onShowAllowLocationCard.invoke()
    }

    private fun handleLocationNotAvailable() {
        Log.d("LocationPermissionHandler", "handleLocationNotAvailable called")
        Toast.makeText(
            context,
            "Unable to get current location. Showing weather for default location (Cairo).",
            Toast.LENGTH_LONG
        ).show()
        onShowAllowLocationCard.invoke()
        onLocationFetched.invoke(30.0444, 31.2357) // Cairo coordinates
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        Log.d("LocationPermissionHandler", "Location enabled: $isEnabled")
        return isEnabled
    }

    fun showPermissionDeniedDialog() {
        AlertDialog.Builder(context)
            .setTitle("Location Permission Required")
            .setMessage("This app needs location access to provide weather updates for your area. Please grant permission or enable location services.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = android.net.Uri.fromParts("package", context.packageName, null)
                }
                fragment.startActivity(intent)
            }
            .setNegativeButton("Use Default Location") { _, _ ->
                onLocationFetched.invoke(30.0444, 31.2357) // Cairo coordinates
            }
            .setCancelable(false)
            .show()
    }
}