package com.soundbite.retrograde

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

class VolleySingleton(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null

        // Used to instantiate or retrieve singleton instance
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleySingleton(context).also {
                    INSTANCE = it
                }
            }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    // Used to get or send a bitmap image
    val imageLoader: ImageLoader by lazy {
        val imageCache = object : ImageLoader.ImageCache {
            private val cache = LruCache<String, Bitmap>(20)
            override fun getBitmap(url: String): Bitmap {
                return cache.get(url)
            }

            override fun putBitmap(url: String, bitmap: Bitmap) {
                cache.put(url, bitmap)
            }

        }
        ImageLoader(requestQueue, imageCache)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}