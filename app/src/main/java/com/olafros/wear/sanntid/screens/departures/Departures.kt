package com.olafros.wear.sanntid.screens.departures

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.material.*
import com.olafros.wear.sanntid.DeparturesListQuery
import com.olafros.wear.sanntid.components.DepartureTime
import com.olafros.wear.sanntid.utils.Constants
import com.olafros.wear.sanntid.utils.SharedPreferencesManager
import com.olafros.wear.sanntid.utils.rotaryScroll

/**
 * Displays a list of departures
 */
@Composable
fun Departures(
    navController: NavHostController,
    stopPlaceID: String,
    viewModel: DeparturesViewModel = viewModel(),
    scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
) {
    viewModel.updateStopPlaceID(stopPlaceID)

    val isLoading = viewModel.data?.stopPlace == null

    val leadingTextStyle = TimeTextDefaults.timeTextStyle()
    val stopPlaceName = viewModel.data?.stopPlace?.name ?: "Laster..."

    Scaffold(
        positionIndicator = { PositionIndicator(scalingLazyListState = scalingLazyListState) },
        timeText = {
            TimeText(
                startLinearContent = {
                    Text(text = stopPlaceName, style = leadingTextStyle)
                },
                startCurvedContent = {
                    curvedText(text = stopPlaceName, style = CurvedTextStyle(leadingTextStyle))
                },
            )
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .rotaryScroll(scalingLazyListState),
            state = scalingLazyListState,
        ) {
            if (!isLoading) {
                item {
                    ListHeader {
                        FavouriteButton(viewModel.data!!)
                    }
                }
            }
            if (!isLoading) {
                items(viewModel.data!!.stopPlace!!.estimatedCalls) {
                    DepartureChip(navController, it, stopPlaceID)
                }
            }
            if (!isLoading) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            if (!isLoading) {
                item {
                    Button(
                        onClick = { viewModel.fetchMoreDepartures() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Text("Last mer")
                    }
                }
            }
            if (isLoading) {
                item {
                    CircularProgressIndicator(strokeWidth = 4.dp)
                }
            }
        }
    }
}

/**
 * DepartureChip displays a specific departure
 */
@Composable
fun DepartureChip(navController: NavHostController, departure: DeparturesListQuery.EstimatedCall, stopPlaceID: String) {
    val hasPlatform = (departure.quay?.publicCode ?: "") != ""
    Chip(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (hasPlatform) 52.dp else 42.dp),
        onClick = { navController.navigate("${Constants.Navigation.SERVICE_JOURNEY}/${departure.serviceJourney?.id}/$stopPlaceID") },
        enabled = true,
        secondaryLabel = { if (hasPlatform) Text("Plattform ${departure.quay?.publicCode}") },
        label = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    departure.destinationDisplay?.frontText ?: "Ukjent",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = 4.dp),
                    fontSize = 13.sp
                )
                DepartureTime(
                    departure.expectedDepartureTime,
                    departure.realtime
                )
            }
        },
        icon = {
            Text(
                departure.serviceJourney?.line?.publicCode ?: "-",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.defaultMinSize(minWidth = 20.dp),
                textAlign = TextAlign.Center
            )
        }
    )
}

/**
 * FavouriteButton allows the user to save a StopPlace to its favourites
 */
@Composable
fun FavouriteButton(stopPlace: DeparturesListQuery.Data) {
    val stopPlaceId = stopPlace.stopPlace!!.id
    val sharedPreferencesManager = SharedPreferencesManager(
        LocalContext.current,
        Constants.Favourites.KEY,
        Constants.Favourites.DEFAULT
    )
    var isFavourite by remember {
        mutableStateOf(
            sharedPreferencesManager.getStringArray().contains(stopPlaceId)
        )
    }

    fun toggleFavourite() {
        if (isFavourite) {
            sharedPreferencesManager.setStringArray(
                sharedPreferencesManager.getStringArray().filter { it != stopPlaceId })
        } else {
            val currentFavourites = sharedPreferencesManager.getStringArray().toMutableList()
            currentFavourites.add(stopPlaceId)
            sharedPreferencesManager.setStringArray(currentFavourites)
        }
        isFavourite = !isFavourite
    }

    CompactButton(
        onClick = { toggleFavourite() },
        colors = ButtonDefaults.iconButtonColors()
    ) {
        Icon(
            if (isFavourite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            contentDescription = "Favoritt",
            modifier = Modifier
                .size(ButtonDefaults.SmallIconSize)
                .wrapContentSize(align = Alignment.Center)
        )
    }
}
