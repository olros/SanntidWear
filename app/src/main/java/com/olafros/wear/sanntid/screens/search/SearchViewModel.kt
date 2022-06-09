package com.olafros.wear.sanntid.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class SearchViewModel : ViewModel() {

    /**
     * Whether the query is loading
     */
    var isLoading by mutableStateOf(false)

    /**
     * The search-results
     */
    var data = mutableStateListOf<StopPlaceChipData>()
        private set

    /**
     * Search for a StopPlace by its name
     * @param input The search-input
     */
    fun search(input: String) {
        viewModelScope.launch {
            isLoading = true

            val request = Request.Builder()
                .url("$ENTUR_API_URL/geocoder/v1/autocomplete?lang=no&size=25&text=$input&layers=venue")
                .header(Constants.ENTUR_HEADER_KEY, Constants.ENTUR_HEADER_VALUE)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    isLoading = false
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        val json = JSONObject(response.body!!.string())
                        val features = json.getJSONArray("features").toJSONObjectList()
                        data.clear()
                        features
                            .map { it.getJSONObject("properties") }
                            .forEach {

                                data.add(
                                    StopPlaceChipData(
                                        it.getString("id"),
                                        it.getString("label"),
                                        it.getJSONArray("category").toStringList()
                                            .map { cat -> venueMapper(cat) }.distinct().sorted()
                                            .joinToString(", ")
                                    )
                                )
                            }
                        isLoading = false
                    }
                }
            })
        }
    }
}
