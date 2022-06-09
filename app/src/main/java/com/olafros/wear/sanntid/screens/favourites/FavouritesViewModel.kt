package com.olafros.wear.sanntid.screens.favourites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olafros.wear.sanntid.StopPlacesListQuery
import com.olafros.wear.sanntid.apolloClient
import kotlinx.coroutines.launch

class FavouritesViewModel : ViewModel() {

    /**
     * If the query to load the favourites is loading
     */
    var isLoading by mutableStateOf(true)
        private set

    /**
     * The StopPlaces, null until loaded
     */
    var data by mutableStateOf<StopPlacesListQuery.Data?>(null)
        private set

    /**
     * Load the given StopPlaces
     */
    fun load(stopPlaces: List<String>) {
        viewModelScope.launch {
            if (stopPlaces.isNotEmpty()) {
                val response =
                    apolloClient().query(
                        StopPlacesListQuery(stopPlaces)
                    )
                        .execute()
                if (!response.hasErrors()) {
                    data = response.dataAssertNoErrors
                }
            }
            isLoading = false
        }
    }
}
