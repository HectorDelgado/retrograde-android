package com.soundbite.retrograde

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class GeocoderService private constructor(private val context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: GeocoderService? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: GeocoderService(context).also {
                    INSTANCE = it
                }
            }
    }

    fun getAddressFromLocation(lat: Double, lon: Double, result: (Address?) -> Unit) {
        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(lat, lon, 1).firstOrNull().let {
            result(it)
        }
    }
}