package com.example.weather_forecast

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import com.example.weather_forecast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if present
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        intent?.let {
//            if (it.getBooleanExtra("from_map", false)) {
//                val lat = it.getDoubleExtra("lat_from_map", 0.0)
//                val lon = it.getDoubleExtra("lon_from_map", 0.0)
//
//                val bundle = Bundle().apply {
//                    putDouble("lat_from_map", lat)
//                    putDouble("lon_from_map", lon)
//                    putBoolean("from_map", true)
//                }
//
//                val navController = findNavController(R.id.nav_host_fragment)
//                navController.navigate(R.id.nav_home, bundle)
//            }
//        }
//    }

}