package com.olafros.wear.sanntid.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import com.olafros.wear.sanntid.StopPlacesListQuery
import com.olafros.wear.sanntid.apolloClient
import com.olafros.wear.sanntid.components.StopPlaceChip
import com.olafros.wear.sanntid.utils.Constants
import com.olafros.wear.sanntid.utils.SharedPreferencesManager
import com.olafros.wear.sanntid.utils.transportModeMapper
import kotlinx.coroutines.launch

class FavouritesViewModel : ViewModel() {

    var data by mutableStateOf<StopPlacesListQuery.Data?>(null)
        private set

    fun load(stopPlaces: List<String>) {
        viewModelScope.launch {
            val response =
                apolloClient().query(
                    StopPlacesListQuery(stopPlaces)
                )
                    .execute()
            if (!response.hasErrors()) {
                data = response.dataAssertNoErrors
            }
        }
    }
}

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
                    Text("Favoritter")
                }
            }
            if (viewModel.data != null && viewModel.data?.stopPlaces?.isEmpty() == true) {
                item {
                    Text(
                        "Du har ikke valgt noen favoritter",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(viewModel.data?.stopPlaces ?: listOf()) {
                StopPlaceChip(
                    navController,
                    it!!.id,
                    it.name,
                    it.transportMode!!.map { mode -> transportModeMapper(mode!!) }.distinct()
                        .joinToString(", ")
                )
            }
        }
    }
}
