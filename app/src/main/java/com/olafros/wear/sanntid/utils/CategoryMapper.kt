package com.olafros.wear.sanntid.utils

import com.olafros.wear.sanntid.type.TransportMode

fun venueMapper(category: String): String =
    when (category) {
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

fun transportModeMapper(category: TransportMode): String =
    when (category) {
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
