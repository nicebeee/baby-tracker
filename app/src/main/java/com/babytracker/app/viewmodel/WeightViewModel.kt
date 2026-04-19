package com.babytracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.babytracker.app.data.database.AppDatabase
import com.babytracker.app.data.entities.WeightEntry
import com.babytracker.app.data.repository.AppRepository
import kotlinx.coroutines.launch

class WeightViewModel(app: Application) : AndroidViewModel(app) {
    private val repo: AppRepository
    val entries: LiveData<List<WeightEntry>>

    init {
        val db = AppDatabase.getInstance(app)
        repo = AppRepository(db.feedingDao(), db.sleepDao(), db.weightDao(), db.diaperDao())
        entries = repo.allWeights
    }

    fun add(date: Long, weightKg: Double, note: String) = viewModelScope.launch {
        repo.insertWeight(WeightEntry(date = date, weightKg = weightKg, note = note))
    }

    fun update(entry: WeightEntry) = viewModelScope.launch { repo.updateWeight(entry) }
    fun delete(entry: WeightEntry) = viewModelScope.launch { repo.deleteWeight(entry) }
}
