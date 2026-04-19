package com.babytracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.babytracker.app.data.database.AppDatabase
import com.babytracker.app.data.entities.DiaperEntry
import com.babytracker.app.data.entities.DiaperType
import com.babytracker.app.data.repository.AppRepository
import kotlinx.coroutines.launch

class DiaperViewModel(app: Application) : AndroidViewModel(app) {
    private val repo: AppRepository
    val entries: LiveData<List<DiaperEntry>>

    init {
        val db = AppDatabase.getInstance(app)
        repo = AppRepository(db.feedingDao(), db.sleepDao(), db.weightDao(), db.diaperDao())
        entries = repo.allDiapers
    }

    fun add(dateTime: Long, type: DiaperType, note: String) = viewModelScope.launch {
        repo.insertDiaper(DiaperEntry(dateTime = dateTime, type = type, note = note))
    }

    fun update(entry: DiaperEntry) = viewModelScope.launch { repo.updateDiaper(entry) }
    fun delete(entry: DiaperEntry) = viewModelScope.launch { repo.deleteDiaper(entry) }
}
