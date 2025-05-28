package com.example.weather_forecast

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize osmdroid configuration (OpenStreetMap)
        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.map_activity)

        // Initialize MapView
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK) // Use MAPNIK tile source
        map.setBuiltInZoomControls(true) // Enable zoom buttons
        map.setMultiTouchControls(true) // Enable pinch-to-zoom

        // Get initial location from intent
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val address = intent.getStringExtra("address") ?: "Selected Location"

        // Set initial map position
        val startPoint = GeoPoint(latitude, longitude)
        map.controller.apply {
            setZoom(14.0)
            setCenter(startPoint)
        }

        // Add initial marker
        addMarker(startPoint, address)

        // Setup map click listener
        setupMapClickListener()
    }

    private fun addMarker(point: GeoPoint, title: String) {
        map.overlays.removeAll { it is Marker } // Clear existing markers
        Marker(map).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            this.title = title
            map.overlays.add(this)
        }
        map.invalidate()
    }

    private fun setupMapClickListener() {
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let { point ->
                    addMarker(point, "Selected Location")
                    setResult(RESULT_OK, Intent().apply {
                        putExtra("latitude", point.latitude)
                        putExtra("longitude", point.longitude)
                        putExtra("source", "map")
                    })
                    finish()
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        })
        map.overlays.add(0, mapEventsOverlay)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
