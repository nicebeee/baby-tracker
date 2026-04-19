package com.babytracker.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.babytracker.app.service.TimerService

class BabyTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TimerService.CHANNEL_ID,
                "Таймер кормления / сна",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Показывает активный таймер в шторке"
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
