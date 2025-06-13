package com.yargisoft.pomodoroapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PomodoroApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}