package com.olafros.wear.sanntid.utils

import org.json.JSONArray
import org.json.JSONObject

fun JSONArray.toStringList(): MutableList<String> {
    val list = mutableListOf<String>()
    (0 until this.length()).forEach { cat ->
        list.add(this.getString(cat))
    }
    return list
}

fun JSONArray.toJSONObjectList(): MutableList<JSONObject> {
    val list = mutableListOf<JSONObject>()
    (0 until this.length()).forEach { cat ->
        list.add(this.getJSONObject(cat))
    }
    return list
}
