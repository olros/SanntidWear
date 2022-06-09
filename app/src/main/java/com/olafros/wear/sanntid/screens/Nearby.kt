package com.olafros.wear.sanntid.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.olafros.wear.sanntid.components.StopPlaceChip
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

data class NearbyResult(
    val id: String,
    val label: String,
    val categories: List<String>,
    val distance: Double
)

class NearbyViewModel : ViewModel() {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    var location by mutableStateOf<Location?>(null)
        private set
    var isDataFetched by mutableStateOf(false)
        private set
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


    fun loadNearbyStopPlaces(location: Location) {
        viewModelScope.launch {
            val request = Request.Builder()
                .url("$ENTUR_API_URL/geocoder/v1/reverse?point.lat=${location.latitude}&point.lon=${location.longitude}&lang=no&size=25&layers=venue")
                .header("ET-Client-Name", Constants.ETClientName)
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

private fun formatDistance(distance: Double): String =
    if (distance < 1) "${distance * 1000} m" else "${String.format(" % .2f", distance)} km"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Nearby(navController: NavHostController, viewModel: NearbyViewModel = viewModel()) {
    val context = LocalContext.current
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    println(locationPermissionsState.allPermissionsGranted)

    Scaffold {
        ScalingLazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                ListHeader {
                    Text("I nærheten")
                }
            }
            if (!locationPermissionsState.allPermissionsGranted) {
                val allPermissionsRevoked =
                    locationPermissionsState.permissions.size == locationPermissionsState.revokedPermissions.size
                item {
                    Text(
                        if (!allPermissionsRevoked) {
                            "For å få presise resultater må du også gi tilgang til din nøyaktige posisjon"
                        } else "For å se holdeplasser i nærheten må du gi tilgang til posisjon. Posisjonen behandles kun på denne enheten og lagres ingen andre steder",
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    Button(
                        onClick = { locationPermissionsState.launchMultiplePermissionRequest() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Gi tilgang")
                    }
                }
            } else if (viewModel.location == null) {
                viewModel.loadLocation(context)
                item {
                    CircularProgressIndicator(strokeWidth = 4.dp)
                }
                item {
                    Text("Laster posisjonen din", textAlign = TextAlign.Center)
                }
            } else if (!viewModel.isDataFetched) {
                viewModel.loadNearbyStopPlaces(viewModel.location!!)
                item {
                    CircularProgressIndicator(strokeWidth = 4.dp)
                }
                item {
                    Text("Leter etter holdeplasser i nærheten", textAlign = TextAlign.Center)
                }
            } else {
                if (viewModel.data.isEmpty()) {
                    item {
                        Text(
                            "Fant ingen holdeplasser i nærheten av deg",
                            textAlign = TextAlign.Center
                        )
                    }
                }
                items(viewModel.data) {
                    StopPlaceChip(navController, it)
                }
            }
        }
    }
}
