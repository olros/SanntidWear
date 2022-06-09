package com.olafros.wear.sanntid

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.olafros.wear.sanntid.screens.*
import com.olafros.wear.sanntid.utils.Constants

@Composable
fun Navigation() {
    val navController = rememberSwipeDismissableNavController()
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Constants.Navigation.HOME
    ) {
        composable(Constants.Navigation.HOME) {
            Home(navController = navController)
        }
        composable(Constants.Navigation.SEARCH) {
            Search(navController = navController)
        }
        composable(Constants.Navigation.FAVOURITES) {
            Favourites(navController = navController)
        }
        composable(Constants.Navigation.NEARBY) {
            Nearby(navController = navController)
        }
        composable(
            route = "${Constants.Navigation.DEPARTURES}/{Id}",
            arguments = listOf(
                navArgument("Id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("Id")
            if (itemId != null) {
                Departures(navController = navController, id = itemId)
            } else {
                Text("No gikk galt")
            }
        }
    }
}