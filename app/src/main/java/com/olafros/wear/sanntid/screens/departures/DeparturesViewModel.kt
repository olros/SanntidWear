package com.olafros.wear.sanntid.screens.departures

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olafros.wear.sanntid.DeparturesListQuery
import com.olafros.wear.sanntid.apolloClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val PAGE_SIZE = 25

class DeparturesViewModel : ViewModel() {

    private var stopPlaceID by mutableStateOf<String?>(null)
    private var numberOfDepartures by mutableStateOf(PAGE_SIZE)

    /**
     * The departures-data returned from the query. Null until the query is finished
     */
    var data by mutableStateOf<DeparturesListQuery.Data?>(null)
        private set

    /**
     * Sets which StopPlace should be shown
     */
    fun updateStopPlaceID(id: String) {
        stopPlaceID = id
        load()
    }

    /**
     * Increase the amount of departures that should be fetched
     */
    fun fetchMoreDepartures() {
        numberOfDepartures += PAGE_SIZE
    }

    private fun load() {
        if (stopPlaceID != null) {
            viewModelScope.launch {
                val response =
                    apolloClient().query(
                        DeparturesListQuery(
                            stopPlaceID!!,
                            numberOfDepartures
                        )
                    )
                        .execute()
                if (!response.hasErrors()) {
                    data = response.dataAssertNoErrors
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            while (true) {
                if (stopPlaceID != null) {
                    load()
                }
                delay(10000L)
            }
        }
    }
}
