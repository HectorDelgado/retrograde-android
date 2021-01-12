package com.soundbite.retrograde

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class SingleLocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Custom Binder class
    inner class MyBinder : Binder() {
        fun getService(): SingleLocationService = this@SingleLocationService
    }

    // Custom callback interface
    interface MCallback {
        fun onLocationRetrieved(latLng: String)
    }

    private val mLocalBinder = MyBinder()
    private lateinit var mCallback: MCallback

    fun setCallback(callback: MCallback) {
        this.mCallback = callback
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun requestLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    val lat = location.latitude
                    val lon = location.longitude
                    mCallback.onLocationRetrieved("Location >>> Lat ${lat} Lon ${lon}")
                }
        } else {
            Log.d("logz", "You don't have the right permissions granted.")
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return mLocalBinder
    }
}