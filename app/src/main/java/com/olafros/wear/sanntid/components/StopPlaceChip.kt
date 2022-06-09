package com.olafros.wear.sanntid.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Text
import com.olafros.wear.sanntid.utils.Constants

/**
 * Necessary data needed to render a StopPlaceChip
 * @param id Id of StopPlace
 * @param label Label of Chip
 * @param secondaryLabel Optional SecondaryLabel of Chip
 */
data class StopPlaceChipData(val id: String, val label: String, val secondaryLabel: String?)

/**
 * StopPlaceChip renders a Material Chip which redirects the user to the StopPlace's departments on click.
 * @param navController A NavHostController
 * @param data StopPlaceChipData
 */
@Composable
fun StopPlaceChip(navController: NavHostController, data: StopPlaceChipData) {
    Chip(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { navController.navigate("${Constants.Navigation.DEPARTURES}/${data.id}") },
        enabled = true,
        secondaryLabel = { if (data.secondaryLabel != null) Text(data.secondaryLabel) },
        label = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    data.label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp
                )
            }
        }
    )
}