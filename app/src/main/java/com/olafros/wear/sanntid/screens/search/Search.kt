package com.olafros.wear.sanntid.screens.search

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import com.olafros.wear.sanntid.components.StopPlaceChip
import com.olafros.wear.sanntid.utils.rotaryScroll

/**
 * Search-view where the user's can find StopPlaces
 */
@Composable
fun Search(
    navController: NavHostController,
    viewModel: SearchViewModel = viewModel(),
    scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
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

    Scaffold(positionIndicator = { PositionIndicator(scalingLazyListState = scalingLazyListState) }) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .rotaryScroll(scalingLazyListState),
            state = scalingLazyListState,
        ) {
            item {
                ListHeader {
                    Chip(
                        icon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                        label = { Text(if (searchInput == "") "Søk" else searchInput) },
                        onClick = {
                            val intent: Intent =
                                RemoteInputIntentHelper.createActionRemoteInputIntent()
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
