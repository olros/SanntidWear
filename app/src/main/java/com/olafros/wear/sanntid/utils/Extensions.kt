package com.olafros.wear.sanntid.utils

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
