package com.example.weather_forecast

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize osmdroid configuration
        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = packageName // Set user agent

        setContentView(R.layout.map_activity)

        // Initialize MapView
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK) // Use MAPNIK tile source
        map.setBuiltInZoomControls(true) // Enable zoom buttons
        map.setMultiTouchControls(true) // Enable pinch-to-zoom

        // Get latitude and longitude from intent
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val address = intent.getStringExtra("address") ?: "Current Location"

        // Set map position and zoom level
        val mapController = map.controller
        mapController.setZoom(14.0) // Initial zoom level
        val startPoint = GeoPoint(latitude, longitude)
        mapController.setCenter(startPoint)

        // Add a marker at the location
        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = address
        map.overlays.add(marker)
        map.invalidate() // Refresh map to show marker
    }

    override fun onResume() {
        super.onResume()
        map.onResume() // Refresh osmdroid
    }

    override fun onPause() {
        super.onPause()
        map.onPause() // Pause osmdroid
    }
}