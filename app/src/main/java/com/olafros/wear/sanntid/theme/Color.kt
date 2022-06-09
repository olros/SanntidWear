/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
