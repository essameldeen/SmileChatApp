package com.task.toshiba.smackapp.Services

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley

class SharedPrefs(context: Context) {

    val FILE_NAME = "prefs"
    val pref: SharedPreferences = context.getSharedPreferences(FILE_NAME, 0)

    val IS_LOGGED_IN = "isLoggedIn"
    val AUTH_TOKEN = "authToken"
    val USER_EMAIL = "userEmail"

    var isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGGED_IN, false)
        set(value) = pref.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var authToken: String
        get() = pref.getString(AUTH_TOKEN, "")
        set(value) = pref.edit().putString(AUTH_TOKEN, value).apply()

    var userEmail: String
        get() = pref.getString(USER_EMAIL, "")
        set(value) = pref.edit().putString(USER_EMAIL, value).apply()

    var volley = Volley.newRequestQueue(context)!!
}