package com.soundbite.retrograde

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class LocationService private constructor(private val context: Context){
    companion object {
        // Single instance of this class
        @Volatile
        private var INSTANCE: LocationService? = null

        /**
         * Creates a new or retrieves a previously constructed instance of this class.
         */
        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: LocationService(context).also {
                        INSTANCE = it
                    }
                }

    }

    private var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun requestOneTimeLocation(result: (Result<Location>) -> Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { possibleLocation: Location? ->
                        possibleLocation?.let { location ->
                            result(Result.success(location))
                        } ?: result(Result.failure(IllegalStateException("addOnSuccessListener was called but the Location was null")))
                    }
                    .addOnFailureListener { exception ->
                        result(Result.failure(exception))
                    }
        } else {
            result(Result.failure(IllegalStateException("You are requesting a users location but don't have the permissions granted.")))
        }

    }

}