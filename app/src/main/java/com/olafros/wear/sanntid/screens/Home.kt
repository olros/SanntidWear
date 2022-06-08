package com.olafros.wear.sanntid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import com.example.android.wearable.composestarter.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.olafros.wear.sanntid.utils.Constants

@Composable
fun Home(navController: NavHostController) {
    Scaffold {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ListHeader {
                    Text(text = "Sanntid")
                }
            }
            item {
                Item(
                    navController,
                    "Test",
                    rememberVectorPainter(Icons.Rounded.Done),
//                     "${Constants.Navigation.DEPARTURES}/58366"
                     "${Constants.Navigation.DEPARTURES}/NSR:StopPlace:59872"
                )
            }
            item {
                Item(
                    navController,
                    "Søk",
                    rememberVectorPainter(Icons.Rounded.Search),
                     Constants.Navigation.SEARCH
                )
            }
            item {
                Item(
                    navController,
                    "Favoritter",
                    rememberVectorPainter(Icons.Rounded.FavoriteBorder),
                    null
                    // Constants.Navigation.FAVOURITES
                )
            }
            item {
                Item(
                    navController,
                    "I nærheten",
                    painterResource(R.drawable.ic_round_my_location_24),
                    null
                    // Constants.Navigation.NEARBY
                )
            }
        }
    }
}

@Composable
fun Item(navController: NavHostController, title: String, icon: Painter, destination: String?) {
    Chip(
        onClick = { if (destination != null) navController.navigate(destination) },
        enabled = destination != null,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        icon = {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier
                    .size(ChipDefaults.IconSize)
                    .wrapContentSize(align = Alignment.Center),
            )
        }
    )
}
