package com.soundbite.retrograde

import java.text.SimpleDateFormat
import java.util.*

/**
 * Converts a UNIX time stamp into a formatted Date String.
 * Ex: 1610387841 -> 01-11-2021 5:57:21
 */
fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
    val date = Date(this * 1000)
    return sdf.format(date)
}

fun Double.roundTo(decimals: Int): Double {
    return "%.${decimals}f".format(this).toDouble()
}

fun String.appendCelsius(): String {
    return "$this \u2103"
}

fun String.appendFahrenheit(): String {
    return "$this \u2109"
}

fun String.appendKelvin(): String {
    return "$this \u212A"
}