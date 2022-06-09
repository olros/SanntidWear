package com.olafros.wear.sanntid.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import com.example.android.wearable.composestarter.R
import com.olafros.wear.sanntid.utils.Constants
import java.time.Year

@Composable
fun Home(navController: NavHostController) {
    Scaffold {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ListHeader {
                    Text("Sanntid", style = MaterialTheme.typography.title1)
                }
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
                    Constants.Navigation.FAVOURITES
                )
            }
            item {
                Item(
                    navController,
                    "I nærheten",
                    painterResource(R.drawable.ic_round_my_location_24),
                    Constants.Navigation.NEARBY
                )
            }
            item {
                Text(
                    "Data fra Entur",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            item {
                Text(
                    "sanntid.olafros.com",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 10.dp)
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
