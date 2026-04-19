package com.babytracker.app.data.repository

import com.babytracker.app.data.dao.*
import com.babytracker.app.data.entities.*

class AppRepository(
    private val feedingDao: FeedingDao,
    private val sleepDao: SleepDao,
    private val weightDao: WeightDao,
    private val diaperDao: DiaperDao
) {
    val allFeedings = feedingDao.getAll()
    val allSleeps = sleepDao.getAll()

    fun feedingsByDay(from: Long, to: Long) = feedingDao.getByDay(from, to)
    fun sleepsByDay(from: Long, to: Long) = sleepDao.getByDay(from, to)
    val allWeights = weightDao.getAll()
    val allDiapers = diaperDao.getAll()

    suspend fun insertFeeding(s: FeedingSession) = feedingDao.insert(s)
    suspend fun updateFeeding(s: FeedingSession) = feedingDao.update(s)
    suspend fun deleteFeeding(s: FeedingSession) = feedingDao.delete(s)

    suspend fun insertSleep(s: SleepSession) = sleepDao.insert(s)
    suspend fun updateSleep(s: SleepSession) = sleepDao.update(s)
    suspend fun deleteSleep(s: SleepSession) = sleepDao.delete(s)

    suspend fun insertWeight(e: WeightEntry) = weightDao.insert(e)
    suspend fun updateWeight(e: WeightEntry) = weightDao.update(e)
    suspend fun deleteWeight(e: WeightEntry) = weightDao.delete(e)

    suspend fun insertDiaper(e: DiaperEntry) = diaperDao.insert(e)
    suspend fun updateDiaper(e: DiaperEntry) = diaperDao.update(e)
    suspend fun deleteDiaper(e: DiaperEntry) = diaperDao.delete(e)
}
