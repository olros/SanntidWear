package com.olafros.wear.sanntid.screens

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
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

class SearchViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var data = mutableStateListOf<StopPlaceChipData>()
        private set

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


@Composable
fun Search(
    navController: NavHostController,
    viewModel: SearchViewModel = viewModel()
) {
    var searchInput by remember { mutableStateOf("") }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { data ->
                val results: Bundle = RemoteInput.getResultsFromIntent(data)
                val input = results.getCharSequence("search")
                if (input != null) {
                    searchInput = input.toString()
                    viewModel.search(searchInput)
                }
            }
        }

    Scaffold {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ListHeader {
                    Chip(
                        icon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                        label = { Text(if (searchInput == "") "Søk" else searchInput) },
                        onClick = {
                            val intent: Intent =
                                RemoteInputIntentHelper.createActionRemoteInputIntent();
                            val remoteInputs: List<RemoteInput> = listOf(
                                RemoteInput.Builder("search")
                                    .setLabel("Søk")
                                    .wearableExtender {
                                        setInputActionType(EditorInfo.IME_ACTION_SEARCH)
                                        setEmojisAllowed(false)
                                    }
                                    .build()
                            )

                            RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                            launcher.launch(intent)
                        }
                    )
                }
            }
            if (!viewModel.isLoading && viewModel.data.isEmpty()) {
                item {
                    Text(
                        if (searchInput == "") "Søk etter stoppesteder og se de neste avgangene derifra" else "Søket ditt ga ingen resultater, prøv igjen med andre søkeord",
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (!viewModel.isLoading) {
                items(viewModel.data) {
                    StopPlaceChip(navController, it)
                }
            }
            if (viewModel.isLoading) {
                item {
                    CircularProgressIndicator(strokeWidth = 4.dp)
                }
            }
        }
    }
}
