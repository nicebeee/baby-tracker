package com.babytracker.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.babytracker.app.data.entities.DiaperEntry

@Dao
interface DiaperDao {
    @Query("SELECT * FROM diaper_entries ORDER BY dateTime DESC")
    fun getAll(): LiveData<List<DiaperEntry>>

    @Insert
    suspend fun insert(entry: DiaperEntry): Long

    @Update
    suspend fun update(entry: DiaperEntry)

    @Delete
    suspend fun delete(entry: DiaperEntry)
}
