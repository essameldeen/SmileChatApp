package com.task.toshiba.smackapp.Controllers

import android.app.Application
import android.content.SharedPreferences
import com.task.toshiba.smackapp.Services.SharedPrefs

class App : Application() {
    companion object {
        lateinit var sharedPreferences: SharedPrefs
    }

    override fun onCreate() {
        sharedPreferences = SharedPrefs(applicationContext)
        super.onCreate()
    }
}