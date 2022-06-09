package com.olafros.wear.sanntid.utils

object Constants {
    /**
     * Navigation routes
     */
    object Navigation {
        const val HOME = "home"
        const val DEPARTURES = "departures"
        const val SEARCH = "search"
        const val FAVOURITES = "favourites"
        const val NEARBY = "nearby"
    }

    /**
     * Url of the Entur API
     */
    const val ENTUR_API_URL = "https://api.entur.io"

    /**
     * Name of the required header when sending requests to the Entur API
     */
    const val ENTUR_HEADER_KEY = "ET-Client-Name"
    /**
     * Value of the required header when sending requests to the Entur API
     */
    const val ENTUR_HEADER_VALUE = "com_olafros-wear_sanntid"

    /**
     * Key and default value of the favourites storage in SharedPreferences
     */
    object Favourites {
        const val KEY = "FAVOURITE_STOP_PLACES"
        const val DEFAULT = "[]"
    }
}