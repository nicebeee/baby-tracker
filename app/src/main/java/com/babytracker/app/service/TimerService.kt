package com.babytracker.app.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.babytracker.app.MainActivity
import com.babytracker.app.R

class TimerService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val type = intent.getStringExtra(EXTRA_TYPE) ?: TYPE_FEEDING
                val startTime = intent.getLongExtra(EXTRA_START_TIME, System.currentTimeMillis())
                try {
                    startForeground(NOTIF_ID, buildNotification(type, startTime))
                } catch (e: Exception) {
                    stopSelf()
                }
            }
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun buildNotification(type: String, startTime: Long): Notification {
        val title = if (type == TYPE_FEEDING) "Кормление идёт..." else "Сон идёт..."
        val icon = if (type == TYPE_FEEDING) R.drawable.ic_feeding else R.drawable.ic_sleep

        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("Нажмите чтобы открыть приложение")
            .setSmallIcon(icon)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setWhen(startTime)
            .setUsesChronometer(true)
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "baby_timer_channel"
        const val NOTIF_ID = 1001
        const val ACTION_START = "com.babytracker.START_TIMER"
        const val ACTION_STOP = "com.babytracker.STOP_TIMER"
        const val EXTRA_TYPE = "type"
        const val EXTRA_START_TIME = "start_time"
        const val TYPE_FEEDING = "feeding"
        const val TYPE_SLEEP = "sleep"
    }
}
