package com.ai.mylibrary

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager private constructor(context: Context){
    private var sharedPreferences: SharedPreferences
    init {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    companion object {
        private const val PREFS_NAME = "MyAppPreferences"

        @Volatile
        private var instance: SharedPreferencesManager? = null

        fun getInstance(context: Context): SharedPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(context).also { instance = it }
            }
        }
    }
    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue) ?: defaultValue
    }
}