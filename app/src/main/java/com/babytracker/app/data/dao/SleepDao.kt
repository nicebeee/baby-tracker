package com.babytracker.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.babytracker.app.data.entities.SleepSession

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_sessions ORDER BY startTime DESC")
    fun getAll(): LiveData<List<SleepSession>>

    @Query("SELECT * FROM sleep_sessions WHERE startTime >= :from AND startTime < :to ORDER BY startTime DESC")
    fun getByDay(from: Long, to: Long): LiveData<List<SleepSession>>

    @Insert
    suspend fun insert(session: SleepSession): Long

    @Update
    suspend fun update(session: SleepSession)

    @Delete
    suspend fun delete(session: SleepSession)
}
