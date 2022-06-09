package com.olafros.wear.sanntid.utils

import com.olafros.wear.sanntid.type.TransportMode

/**
 * Maps a venue to a readable Norwegian string
 * @return Readable string
 */
fun venueMapper(venue: String): String =
    when (venue) {
        "railStation" -> "Tog"
        "onstreetTram" -> "Trikk"
        "tramStation" -> "Trikk"
        "airport" -> "Fly"
        "metroStation" -> "T-bane"
        "onstreetBus" -> "Buss"
        "busStation" -> "Buss"
        "coachStation" -> "Buss"
        "harbourPort" -> "Båt"
        "ferryPort" -> "Båt"
        "ferryStop" -> "Båt"
        "liftStation" -> "Heis"
        else -> "Ukjent"
    }

/**
 * Maps a transportMode to a readable Norwegian string
 * @return Readable string
 */
fun transportModeMapper(transportMode: TransportMode): String =
    when (transportMode) {
        TransportMode.funicular -> "Kabelbane"
        TransportMode.tram -> "Trikk"
        TransportMode.air -> "Fly"
        TransportMode.bus -> "Buss"
        TransportMode.cableway -> "Taubane"
        TransportMode.coach -> "Buss"
        TransportMode.lift -> "Heis"
        TransportMode.metro -> "T-bane"
        TransportMode.monorail -> "Énskinnebane"
        TransportMode.rail -> "Tog"
        TransportMode.trolleybus -> "Trolleybuss"
        TransportMode.water -> "Båt"
        TransportMode.unknown -> "Ukjent"
        else -> "Ukjent"
    }
