package com.babytracker.app.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {
    private val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun formatDateTime(millis: Long): String = dateTimeFormat.format(Date(millis))
    fun formatDate(millis: Long): String = dateFormat.format(Date(millis))
    fun formatTime(millis: Long): String = timeFormat.format(Date(millis))

    fun formatDuration(startMillis: Long, endMillis: Long): String {
        val diff = endMillis - startMillis
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60
        return if (hours > 0) "%d ч %02d мин" .format(hours, minutes)
        else "%d мин %02d сек".format(minutes, seconds)
    }

    fun formatTimer(elapsedMillis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }
}
