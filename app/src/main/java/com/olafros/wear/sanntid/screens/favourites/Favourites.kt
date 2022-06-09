package com.olafros.wear.sanntid.screens.favourites

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import com.olafros.wear.sanntid.components.StopPlaceChip
import com.olafros.wear.sanntid.components.StopPlaceChipData
import com.olafros.wear.sanntid.utils.Constants
import com.olafros.wear.sanntid.utils.SharedPreferencesManager
import com.olafros.wear.sanntid.utils.transportModeMapper

/**
 * Displays the user's favourite StopPlaces
 */
@Composable
fun Favourites(
    navController: NavHostController,
    viewModel: FavouritesViewModel = viewModel()
) {

    val sharedPreferencesManager = SharedPreferencesManager(
        LocalContext.current,
        Constants.Favourites.KEY,
        Constants.Favourites.DEFAULT
    )
    viewModel.load(sharedPreferencesManager.getStringArray())

    Scaffold {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ListHeader {
                    Text("Favoritter", style = MaterialTheme.typography.title1)
                }
            }
            if (viewModel.isLoading) {
                item {
                    Text("Laster...", textAlign = TextAlign.Center)
                }
            }
            if (!viewModel.isLoading && (viewModel.data == null || viewModel.data?.stopPlaces?.isEmpty() == true)) {
                item {
                    Text("Du har ingen favoritter", textAlign = TextAlign.Center)
                }
            }
            items(viewModel.data?.stopPlaces ?: listOf()) {
                StopPlaceChip(
                    navController,
                    StopPlaceChipData(
                        it!!.id,
                        it.name,
                        it.transportMode!!.map { mode -> transportModeMapper(mode!!) }.distinct()
                            .sorted()
                            .joinToString(", ")
                    )
                )
            }
        }
    }
}
