package com.soundbite.retrograde

import android.content.Context
import android.util.Log
import androidx.annotation.StringDef
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.annotation.RetentionPolicy

class WeatherService private constructor(private val context: Context){
    companion object {
        @Volatile
        private var INSTANCE: WeatherService? = null

        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: WeatherService(context).also {
                        INSTANCE = it
                    }
                }
    }

    fun generateApiUrl(lat: Double, lon: Double, units: MeasurementUnit): String =
            "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&units=${units.value}&appid=${BuildConfig.OW_SECRET}"

    suspend fun requestWeatherUpdate(url: String, result: (Result<JSONObject>) -> Unit) = withContext(Dispatchers.IO) {
        val jsonObjectRequest = JsonObjectRequest(url, null,
                { response ->
                    Log.d("logz", "we have a successful response")
                    result(Result.success(response))
                },
                { error ->
                    Log.d("logz", "we failed to get a reponse")
                    result(Result.failure(error))
                }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }
}