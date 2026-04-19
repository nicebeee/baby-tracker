package com.babytracker.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.babytracker.app.data.database.AppDatabase
import com.babytracker.app.data.entities.FeedingSession
import com.babytracker.app.data.repository.AppRepository
import kotlinx.coroutines.launch

class FeedingViewModel(app: Application) : AndroidViewModel(app) {
    private val repo: AppRepository
    val sessions: LiveData<List<FeedingSession>>

    private val _isRunning = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning

    private val _elapsedMillis = MutableLiveData(0L)
    val elapsedMillis: LiveData<Long> = _elapsedMillis

    private var startTimeMillis = 0L
    private var timerRunnable: Runnable? = null
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val prefs = app.getSharedPreferences("feeding_prefs", Context.MODE_PRIVATE)

    init {
        val db = AppDatabase.getInstance(app)
        repo = AppRepository(db.feedingDao(), db.sleepDao(), db.weightDao(), db.diaperDao())
        sessions = repo.allFeedings

        // Восстанавливаем таймер если приложение было закрыто
        val savedStart = prefs.getLong(KEY_START, 0L)
        if (savedStart > 0L) {
            startTimeMillis = savedStart
            _isRunning.value = true
            tickTimer()
        }
    }

    fun startTimer() {
        startTimeMillis = System.currentTimeMillis()
        prefs.edit().putLong(KEY_START, startTimeMillis).apply()
        _isRunning.value = true
        tickTimer()
    }

    private fun tickTimer() {
        timerRunnable = Runnable {
            _elapsedMillis.value = System.currentTimeMillis() - startTimeMillis
            timerRunnable?.let { handler.postDelayed(it, 1000) }
        }
        handler.post(timerRunnable!!)
    }

    fun stopTimer() {
        val endTime = System.currentTimeMillis()
        handler.removeCallbacksAndMessages(null)
        prefs.edit().remove(KEY_START).apply()
        _isRunning.value = false
        _elapsedMillis.value = 0L
        viewModelScope.launch {
            repo.insertFeeding(FeedingSession(startTime = startTimeMillis, endTime = endTime))
        }
    }

    fun addManual(startTime: Long, endTime: Long, note: String) {
        viewModelScope.launch {
            repo.insertFeeding(FeedingSession(startTime = startTime, endTime = endTime, note = note))
        }
    }

    fun update(session: FeedingSession) = viewModelScope.launch { repo.updateFeeding(session) }
    fun delete(session: FeedingSession) = viewModelScope.launch { repo.deleteFeeding(session) }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val KEY_START = "feeding_start_time"
    }
}
