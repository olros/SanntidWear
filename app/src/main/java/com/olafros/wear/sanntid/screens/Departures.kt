package com.olafros.wear.sanntid.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.material.*
import com.olafros.wear.sanntid.DeparturesListQuery
import com.olafros.wear.sanntid.apolloClient
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

const val PAGE_SIZE = 25

class DeparturesViewModel : ViewModel() {

    private var stopPlaceID by mutableStateOf<String?>(null)
    private var numberOfDepartures by mutableStateOf(PAGE_SIZE)
    var data by mutableStateOf<DeparturesListQuery.Data?>(null)
        private set

    fun updateStopPlaceID(id: String) {
        stopPlaceID = id
        load()
    }

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

@Composable
fun Departures(
    navController: NavHostController,
    id: String,
    viewModel: DeparturesViewModel = viewModel()
) {
    viewModel.updateStopPlaceID(id)

    val isLoading = viewModel.data?.stopPlace == null

    val leadingTextStyle = TimeTextDefaults.timeTextStyle()
    val stopPlaceName = viewModel.data?.stopPlace?.name ?: "Laster..."

    Scaffold(
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
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isLoading) {
                item {
                    ListHeader {
                        CompactButton(
                            onClick = { /* TODO: Navigate to quay-page */ },
                            colors = ButtonDefaults.iconButtonColors()
                        ) {
                            Icon(
                                Icons.Rounded.FavoriteBorder, contentDescription = "Favoritt",
                                modifier = Modifier
                                    .size(ButtonDefaults.SmallIconSize)
                                    .wrapContentSize(align = Alignment.Center)
                            )
                        }
                    }
                }
            }
            if (!isLoading) {
                items(viewModel.data!!.stopPlace!!.estimatedCalls) {
                    Departure(it)
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

@Composable
fun Departure(departure: DeparturesListQuery.EstimatedCall) {
    val hasPlatform = (departure.quay?.publicCode ?: "") != ""
    Chip(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (hasPlatform) 52.dp else 42.dp),
        onClick = {},
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
                DepartureTime(departure)
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

@Composable
fun DepartureTime(departure: DeparturesListQuery.EstimatedCall) {
    Text(
        if (departure.expectedDepartureTime is String) {
            "${if (departure.realtime) "" else "ca. "}${getFormattedRelativeTime(departure.expectedDepartureTime)}"
        } else "--:--",
        color = MaterialTheme.colors.onSecondary,
        fontSize = 13.sp
    )
}

private fun getFormattedRelativeTime(departureTime: String): String {
    val dateISOFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.UK)
    val currentTime: Date = Calendar.getInstance().time
    return try {
        val endDate = dateISOFormat.parse(departureTime)
        val minutesUntilDeparture = ((endDate!!.time - currentTime.time) / 60000).toInt()
        Log.d("format", "Min: $minutesUntilDeparture")
        if (minutesUntilDeparture == 0) "Nå" else if (minutesUntilDeparture < 10) "$minutesUntilDeparture min" else {
            val targetFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.UK)
            targetFormat.format(endDate)
        }
    } catch (e: ParseException) {
        e.printStackTrace()
        "--:--"
    }
}
