package com.olafros.wear.sanntid

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.olafros.wear.sanntid.screens.departures.Departures
import com.olafros.wear.sanntid.screens.favourites.Favourites
import com.olafros.wear.sanntid.screens.home.Home
import com.olafros.wear.sanntid.screens.nearby.Nearby
import com.olafros.wear.sanntid.screens.search.Search
import com.olafros.wear.sanntid.screens.serviceJourney.ServiceJourney
import com.olafros.wear.sanntid.utils.Constants

/**
 * Controls the routes of the application and arguments for different screens
 */
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
            arguments = listOf(navArgument("Id") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("Id")
            if (itemId != null) {
                Departures(navController = navController, stopPlaceID = itemId)
            } else {
                Text("Noe gikk galt")
            }
        }
        composable(
            route = "${Constants.Navigation.SERVICE_JOURNEY}/{serviceJourneyId}/{stopPlaceId}",
            arguments = listOf(navArgument("serviceJourneyId") { type = NavType.StringType }, navArgument("stopPlaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceJourneyId = backStackEntry.arguments?.getString("serviceJourneyId")
            val stopPlaceId = backStackEntry.arguments?.getString("stopPlaceId")
            if (serviceJourneyId != null) {
                ServiceJourney(navController = navController, serviceJourneyId = serviceJourneyId, stopPlaceId = stopPlaceId)
            } else {
                Text("Noe gikk galt")
            }
        }
    }
}