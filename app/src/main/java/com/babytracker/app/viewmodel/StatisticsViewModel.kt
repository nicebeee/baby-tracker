package com.babytracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.babytracker.app.data.database.AppDatabase
import com.babytracker.app.data.entities.FeedingSession
import com.babytracker.app.data.entities.SleepSession
import com.babytracker.app.data.repository.AppRepository
import java.util.Calendar

class StatisticsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo: AppRepository = run {
        val db = AppDatabase.getInstance(app)
        AppRepository(db.feedingDao(), db.sleepDao(), db.weightDao(), db.diaperDao())
    }

    val selectedDate = MutableLiveData(startOfDay(System.currentTimeMillis()))

    val feedings: LiveData<List<FeedingSession>> = selectedDate.switchMap { date ->
        repo.feedingsByDay(date, date + DAY_MS)
    }

    val sleeps: LiveData<List<SleepSession>> = selectedDate.switchMap { date ->
        repo.sleepsByDay(date, date + DAY_MS)
    }

    fun previousDay() {
        selectedDate.value = (selectedDate.value ?: 0L) - DAY_MS
    }

    fun nextDay() {
        val next = (selectedDate.value ?: 0L) + DAY_MS
        if (next <= startOfDay(System.currentTimeMillis())) {
            selectedDate.value = next
        }
    }

    fun isToday(): Boolean = selectedDate.value == startOfDay(System.currentTimeMillis())

    companion object {
        const val DAY_MS = 24 * 60 * 60 * 1000L

        fun startOfDay(millis: Long): Long {
            return Calendar.getInstance().apply {
                timeInMillis = millis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
}
