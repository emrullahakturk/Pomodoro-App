package com.yargisoft.pomodoroapp

import android.app.Application
import com.yargisoft.pomodoroapp.util.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PomodoroApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Bildirim kanalını oluştur
        NotificationHelper.createNotificationChannel(this)
    }
}