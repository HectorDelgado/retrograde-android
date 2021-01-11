package com.soundbite.retrograde

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.soundbite.retrograde.model.WeatherForecast
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val consumerKey = BuildConfig.OW_KEY
        val consumerSecret = BuildConfig.OW_SECRET

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        checkPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { possibleLocation: Location? ->
                    possibleLocation?.let { location ->
                        val lat = location.latitude
                        val lon = location.longitude
                        val url = "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&appid=$consumerSecret"

                        lifecycleScope.launch {
                            makeGETRequest(url) { result ->
                                result.onSuccess {
                                    Log.d("logz", "we found JSON data")
                                    val moshi = Moshi.Builder()
                                        .addLast(KotlinJsonAdapterFactory())
                                        .build()

                                    val weatherAdapter: JsonAdapter<WeatherForecast> = moshi.adapter(WeatherForecast::class.java)
                                    val weather = weatherAdapter.fromJson(it.toString())
                                    Log.d("logz", "Weather data>>$weather")
                                }
                                result.onFailure {
                                    Log.d("logz", "we had an error getting JSON data")
                                }
                            }
                        }
                    }


                }
        }
    }


    private fun checkPermissions(permission: String, onSuccess: () -> Unit) {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // permission is now granted
                    onSuccess()
                } else {
                    // permission was not granted
                }
            }

        when {
            ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED -> {
                        // we already had permissions granted
                        onSuccess()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // display an extra UI
            }
            else -> {
                // we need to ask for permission
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private suspend fun makeGETRequest(url: String, result: (Result<JSONObject>) -> Unit) {
        val queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        val jsonObjectRequest = JsonObjectRequest(url, null,
            { response ->
                Log.d("logz", "we found some data!")
                result(Result.success(response))
            },
            { error ->
                Log.d("logz", "we got an error")
                Log.d("logz", error.localizedMessage)
                result(Result.failure(error))
            })
        VolleySingleton.getInstance(this.applicationContext).addToRequestQueue(jsonObjectRequest)
    }
}