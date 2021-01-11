package com.soundbite.retrograde

import java.text.SimpleDateFormat
import java.util.*

fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
    val date = Date(this * 1000)
    return sdf.format(date)
}