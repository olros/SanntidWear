package com.olafros.wear.sanntid.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.json.JSONArray

class SharedPreferencesManager(
    context: Context,
    private val key: String,
    private val default: String
) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor: SharedPreferences.Editor = preferences.edit()

    fun set(value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    fun setStringArray(array: List<String>) {
        val jsonArray = JSONArray()
        array.forEach { jsonArray.put(it) }
        set(jsonArray.toString())
    }

    fun get() = preferences.getString(key, default)

    fun getStringArray(): List<String> = JSONArray(get()).toStringList()
}
