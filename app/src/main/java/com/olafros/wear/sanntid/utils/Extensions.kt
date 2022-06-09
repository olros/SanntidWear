package com.olafros.wear.sanntid.utils

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.wear.compose.material.ScalingLazyListState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * Converts the JSONArray into a MutableList of Strings.
 * Uses `getString` to convert each item in the JSONArray
 */
fun JSONArray.toStringList(): MutableList<String> {
    val list = mutableListOf<String>()
    (0 until this.length()).forEach { cat ->
        list.add(this.getString(cat))
    }
    return list
}

/**
 * Converts the JSONArray into a MutableList of JSONObjects.
 * Uses `getJSONObject` to convert each item in the JSONArray
 */
fun JSONArray.toJSONObjectList(): MutableList<JSONObject> {
    val list = mutableListOf<JSONObject>()
    (0 until this.length()).forEach { cat ->
        list.add(this.getJSONObject(cat))
    }
    return list
}

/**
 * Enables scroll-control from rotary
 * @param scalingLazyListState The ScalingLazyListState
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.rotaryScroll(scalingLazyListState: ScalingLazyListState): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    onRotaryScrollEvent {
        coroutineScope.launch {
            scalingLazyListState.scrollBy(it.verticalScrollPixels)
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        true
    }
        .focusRequester(focusRequester)
        .focusable()
}
