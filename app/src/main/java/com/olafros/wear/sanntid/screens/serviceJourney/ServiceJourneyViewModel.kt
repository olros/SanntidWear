package com.olafros.wear.sanntid.screens.serviceJourney

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olafros.wear.sanntid.ServiceJourneyQuery
import com.olafros.wear.sanntid.apolloClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ServiceJourneyViewModel : ViewModel() {

    private var serviceJourneyID by mutableStateOf<String?>(null)

    /**
     * The servicejourney-data returned from the query. Null until the query is finished
     */
    var data by mutableStateOf<ServiceJourneyQuery.Data?>(null)
        private set

    /**
     * Sets which ServiceJourney should be shown
     */
    fun updateServiceJourneyID(id: String) {
        serviceJourneyID = id
        load()
    }

    private fun load() {
        if (serviceJourneyID != null) {
            viewModelScope.launch {
                val response =
                    apolloClient().query(
                        ServiceJourneyQuery(serviceJourneyID!!)
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
                if (serviceJourneyID != null) {
                    load()
                }
                delay(10000L)
            }
        }
    }
}
