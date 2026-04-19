package com.babytracker.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.babytracker.app.data.entities.FeedingSession

@Dao
interface FeedingDao {
    @Query("SELECT * FROM feeding_sessions ORDER BY startTime DESC")
    fun getAll(): LiveData<List<FeedingSession>>

    @Insert
    suspend fun insert(session: FeedingSession): Long

    @Update
    suspend fun update(session: FeedingSession)

    @Delete
    suspend fun delete(session: FeedingSession)
}
