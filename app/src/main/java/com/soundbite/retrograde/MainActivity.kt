package com.soundbite.retrograde

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.soundbite.retrograde.databinding.ActivityMainBinding
import com.soundbite.retrograde.model.WeatherForecast
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private val locationService by lazy { LocationService.getInstance(this) }
    private val weatherService by lazy { WeatherService.getInstance(applicationContext) }
    private val geoCoderService by lazy { GeocoderService.getInstance(this) }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
            getLatestWeather()
        }
    }

    private fun getLatestWeather() {
        locationService.requestOneTimeLocation { result ->
            result.onSuccess { location ->
                val lat = location.latitude
                val lon = location.longitude
                val url = weatherService.generateApiUrl(lat, lon, Imperial())

                lifecycleScope.launch {
                    weatherService.requestWeatherUpdate(url) { jsonResult ->
                        jsonResult.onSuccess { json ->
                            // Setup Moshi for JSON deserialization
                            val moshi = Moshi.Builder()
                                    .addLast(KotlinJsonAdapterFactory())
                                    .build()
                            val weatherAdapter: JsonAdapter<WeatherForecast> =
                                    moshi.adapter(WeatherForecast::class.java)

                            // Deserialize JSON object into our data model
                            weatherAdapter.fromJson(json.toString())?.let { forecast ->
                                geoCoderService.getAddressFromLocation(forecast.lat, forecast.lon) { address ->
                                    runOnUiThread {
                                        updateUI(address, forecast)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            result.onFailure {
                Toast.makeText(
                        this,
                        "There was a problem getting weather updates. Try again.",
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUI(address: Address?, weatherForecast: WeatherForecast) {
        val city = address?.locality ?: "${weatherForecast.lat}, ${weatherForecast.lon}"
        val description = weatherForecast.currentWeather.weather.first().main
        val currentTemp = weatherForecast.currentWeather.temp.roundToInt().toString().appendFahrenheit()
        val lowTemp = weatherForecast.daily.first().temp.min.roundToInt().toString().appendFahrenheit()
        val highTemp = weatherForecast.daily.first().temp.max.roundToInt().toString().appendFahrenheit()
        val alerts = weatherForecast.alerts
        val alertStart = when(alerts.size) {
            0 -> "There are no local alerts."
            else -> {
                "There are weather alerts in your area!\n${alerts.first().description}"
            }
        }


        binding.mLayout.city.text = city
        binding.mLayout.shortDescription.text = description
        binding.mLayout.currentTemp.text = currentTemp
        binding.mLayout.tempLow.text = lowTemp
        binding.mLayout.tempHigh.text = highTemp
        binding.mLayout.weatherAlert.text = alertStart
    }


    private fun checkPermissions(permission: String, onSuccess: () -> Unit) {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    onSuccess()
                } else {
                    Toast.makeText(
                            this,
                            "Location permissions are required for this app to work correctly.",
                            Toast.LENGTH_LONG).show()
                }
            }

        when {
            ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED -> {
                        // we already had permissions granted
                        onSuccess()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(
                        this,
                        "UI: Location permissions are required for this app to work correctly.",
                        Toast.LENGTH_LONG).show()
            }
            else -> {
                // we need to ask for permission
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}