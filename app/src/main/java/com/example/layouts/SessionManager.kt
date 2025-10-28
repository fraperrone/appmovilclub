package com.example.layouts

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "UserSession"
    private const val KEY_USER_ID = "USER_ID"
    private const val KEY_USER_NAME = "USER_NAME"
    private const val KEY_IS_LOGGED_IN = "IS_LOGGED_IN"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getUserName(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_NAME, null)
    }

    fun getUserId(context: Context): Int {
        return getPreferences(context).getInt(KEY_USER_ID, -1)
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearSession(context: Context) {
        getPreferences(context).edit().clear().apply()
    }
}