package com.babytracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.babytracker.app.data.database.AppDatabase
import com.babytracker.app.data.entities.SleepSession
import com.babytracker.app.data.repository.AppRepository
import kotlinx.coroutines.launch

class SleepViewModel(app: Application) : AndroidViewModel(app) {
    private val repo: AppRepository

    val sessions: LiveData<List<SleepSession>>

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
        sessions = repo.allSleeps
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
            repo.insertSleep(SleepSession(startTime = startTimeMillis, endTime = endTime))
        }
    }

    fun addManual(startTime: Long, endTime: Long, note: String) {
        viewModelScope.launch {
            repo.insertSleep(SleepSession(startTime = startTime, endTime = endTime, note = note))
        }
    }

    fun update(session: SleepSession) = viewModelScope.launch { repo.updateSleep(session) }
    fun delete(session: SleepSession) = viewModelScope.launch { repo.deleteSleep(session) }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }
}
