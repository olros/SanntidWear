package com.olafros.wear.sanntid.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.json.JSONArray

/**
 * Provides methods for interacting with SharedPreferences for a specific key
 * @param context Current context
 * @param key Key in SharedPreferences
 * @param default Default value if key doesn't exist in SharedPreferences
 */
class SharedPreferencesManager(
    context: Context,
    private val key: String,
    private val default: String
) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor: SharedPreferences.Editor = preferences.edit()

    /**
     * Set value of key
     * @param value New value of key
     */
    fun set(value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * Sets value of key to a list of string. The list is transformed to a JSONArray before it's saved
     * @param array The list of strings to save
     */
    fun setStringArray(array: List<String>) {
        val jsonArray = JSONArray()
        array.forEach { jsonArray.put(it) }
        set(jsonArray.toString())
    }

    /**
     * Get the value of the key, returns the default value if key doesn't exist
     * @return The value
     */
    fun get() = preferences.getString(key, default)

    /**
     * Get the value of the key transformed to a list of strings
     * @return List of strings
     */
    fun getStringArray(): List<String> = JSONArray(get()).toStringList()
}
