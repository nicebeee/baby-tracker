package com.babytracker.app.viewmodel

import android.app.Application
import android.os.SystemClock
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

    init {
        val db = AppDatabase.getInstance(app)
        repo = AppRepository(db.feedingDao(), db.sleepDao(), db.weightDao(), db.diaperDao())
        sessions = repo.allFeedings
    }

    fun startTimer() {
        startTimeMillis = System.currentTimeMillis()
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
}
