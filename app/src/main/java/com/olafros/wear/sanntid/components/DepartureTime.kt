package com.olafros.wear.sanntid.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * DepartureTime displays the time of the departure, formatted relatively
 */
@Composable
fun DepartureTime(expectedDepartureTime: Any, realtime: Boolean, notRelative: Boolean = false) {
    Text(
        if (expectedDepartureTime is String) {
            "${if (realtime) "" else "ca. "}${
                getFormattedRelativeTime(
                    expectedDepartureTime,
                    notRelative
                )
            }"
        } else "--:--",
        fontSize = 13.sp
    )
}

/**
 * Formats a departuretime into relative time `x min` if in less then 10 minutes. `HH:mm` if not
 */
private fun getFormattedRelativeTime(departureTime: String, notRelative: Boolean): String {
    val dateISOFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.UK)
    val currentTime: Date = Calendar.getInstance().time
    return try {
        val endDate = dateISOFormat.parse(departureTime)
        val minutesUntilDeparture = ((endDate!!.time - currentTime.time) / 60000).toInt()
        val targetFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.UK)
        val timeFormat = targetFormat.format(endDate)
        if (notRelative) return timeFormat
        if (minutesUntilDeparture == 0) return "Nå"
        if (minutesUntilDeparture < 10) return "$minutesUntilDeparture min"
        return timeFormat
    } catch (e: ParseException) {
        e.printStackTrace()
        "--:--"
    }
}