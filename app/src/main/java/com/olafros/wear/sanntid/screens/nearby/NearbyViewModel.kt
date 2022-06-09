package com.olafros.wear.sanntid.screens.nearby

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.olafros.wear.sanntid.components.StopPlaceChipData
import com.olafros.wear.sanntid.utils.Constants
import com.olafros.wear.sanntid.utils.Constants.ENTUR_API_URL
import com.olafros.wear.sanntid.utils.toJSONObjectList
import com.olafros.wear.sanntid.utils.toStringList
import com.olafros.wear.sanntid.utils.venueMapper
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

/**
 * A result from a nearby-query
 */
data class NearbyResult(
    val id: String,
    val label: String,
    val categories: List<String>,
    val distance: Double
)

class NearbyViewModel : ViewModel() {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    /**
     * The user's location, null until found
     */
    var location by mutableStateOf<Location?>(null)
        private set

    /**
     * Whether the nearby StopPlaces have been loaded
     */
    var isDataFetched by mutableStateOf(false)
        private set

    /**
     * The nearby StopPlaces
     */
    var data = mutableStateListOf<StopPlaceChipData>()
        private set

    private val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.locations.isNotEmpty()) {
                location = locationResult.locations.last()
                stopLocationUpdates()
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() = fusedLocationClient?.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )

    /**
     * Load the user's location
     */
    @SuppressLint("MissingPermission")
    fun loadLocation(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient!!.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location == null) startLocationUpdates()
                else this.location = location
            }
            .addOnFailureListener { it.printStackTrace() }
    }

    /**
     * Load StopPlaces around the given location
     * @param location The location to find StopPlaces around
     */
    fun loadNearbyStopPlaces(location: Location) {
        viewModelScope.launch {
            val request = Request.Builder()
                .url("$ENTUR_API_URL/geocoder/v1/reverse?point.lat=${location.latitude}&point.lon=${location.longitude}&lang=no&size=25&layers=venue")
                .header(Constants.ENTUR_HEADER_KEY, Constants.ENTUR_HEADER_VALUE)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    isDataFetched = false
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        val json = JSONObject(response.body!!.string())
                        val features = json.getJSONArray("features").toJSONObjectList()
                        data.clear()
                        features
                            .map { it.getJSONObject("properties") }
                            .map {
                                NearbyResult(
                                    it.getString("id"),
                                    it.getString("label"),
                                    it.getJSONArray("category").toStringList()
                                        .map { cat -> venueMapper(cat) }.distinct().sorted(),
                                    it.getDouble("distance")
                                )
                            }
                            .sortedBy { it.distance }
                            .forEach {
                                data.add(
                                    StopPlaceChipData(
                                        it.id, it.label, "${formatDistance(it.distance)} - ${
                                            it.categories.joinToString(", ")
                                        }"
                                    )
                                )
                            }

                        isDataFetched = true
                    }
                }
            })
        }
    }
}

/**
 * Formats metric distance. In metres if less then a kilometre, in kilometres else
 * @param distance the distance in kilometres
 */
private fun formatDistance(distance: Double): String =
    if (distance < 1) "${String.format("%.0f", distance * 1000)} m" else "${
        String.format(
            "%.2f",
            distance
        )
    } km"
