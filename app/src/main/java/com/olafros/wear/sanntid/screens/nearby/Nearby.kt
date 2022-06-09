package com.olafros.wear.sanntid.screens.nearby

import android.Manifest
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.olafros.wear.sanntid.components.StopPlaceChip

/**
 * Displays a list of nearby StopPlaces
 */
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

    Scaffold {
        ScalingLazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                ListHeader {
                    Text("I nærheten", style = MaterialTheme.typography.title1)
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
