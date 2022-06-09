package com.olafros.wear.sanntid.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

val Green200 = Color(0xFF1e2625)
val White = Color(0xFFfafafa)
val Teal200 = Color(0xFF19221F)
val Teal700 = Color(0xFF76E0C7)
val Red400 = Color(0xFFCF6679)

internal val wearColorPalette: Colors = Colors(
    primary = Green200,
    primaryVariant = White,
    secondary = Teal200,
    secondaryVariant = Teal200,
    error = Red400,
    onPrimary = White,
    onSecondary = Teal700,
    onError = Color.Black
)
