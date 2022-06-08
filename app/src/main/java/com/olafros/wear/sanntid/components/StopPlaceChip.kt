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

@Composable
fun StopPlaceChip(navController: NavHostController, id: String, label: String, secondaryLabel: String?) {
    Chip(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { navController.navigate("${Constants.Navigation.DEPARTURES}/$id") },
        enabled = true,
        secondaryLabel = { if (secondaryLabel != null) Text(secondaryLabel) },
        label = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp
                )
            }
        }
    )
}