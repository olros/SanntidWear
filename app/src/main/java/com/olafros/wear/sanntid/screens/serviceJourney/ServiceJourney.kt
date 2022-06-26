package com.olafros.wear.sanntid.screens.serviceJourney

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import com.olafros.wear.sanntid.ServiceJourneyQuery
import com.olafros.wear.sanntid.components.DepartureTime
import com.olafros.wear.sanntid.utils.Constants
import com.olafros.wear.sanntid.utils.rotaryScroll

/**
 * Displays a list of stop-places in a service journey
 */
@Composable
fun ServiceJourney(
    navController: NavHostController,
    serviceJourneyId: String,
    stopPlaceId: String?,
    viewModel: ServiceJourneyViewModel = viewModel(),
    scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
) {
    viewModel.updateServiceJourneyID(serviceJourneyId)

    val isLoading = viewModel.data?.serviceJourney == null

    val serviceJourneyName = viewModel.data?.serviceJourney?.line?.name ?: "Laster..."

    val estimatedCalls = viewModel.data?.serviceJourney?.estimatedCalls ?: listOf()

    Scaffold(
        positionIndicator = { PositionIndicator(scalingLazyListState = scalingLazyListState) },
        timeText = { TimeText() }
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
                        Text(serviceJourneyName, style = MaterialTheme.typography.title1)
                    }
                }
            }
            if (!isLoading) {
                items(estimatedCalls) {
                    ServiceJourneyEstimatedCallChip(navController, it!!, stopPlaceId)
                }
            }
            if (!isLoading) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
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
 * ServiceJourneyEstimatedCallChip displays a estimated call in a ServiceJourney
 */
@Composable
fun ServiceJourneyEstimatedCallChip(
    navController: NavHostController,
    estimatedCall: ServiceJourneyQuery.EstimatedCall,
    currentStopPlaceID: String?
) {
    val stopPlaceId = estimatedCall.quay?.stopPlace?.parent?.id
    Chip(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .border(
                if (currentStopPlaceID != null && currentStopPlaceID == stopPlaceId) Dp.Hairline else Dp.Unspecified,
                Color.White,
                CircleShape
            ),
        onClick = { navController.navigate("${Constants.Navigation.DEPARTURES}/$stopPlaceId") },
        enabled = true,
        label = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    estimatedCall.quay?.stopPlace?.name ?: "Ukjent",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = 4.dp),
                    fontSize = 13.sp
                )
                DepartureTime(estimatedCall.expectedDepartureTime, estimatedCall.realtime, true)
            }
        }
    )
}
