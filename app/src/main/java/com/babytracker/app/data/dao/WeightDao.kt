package com.babytracker.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.babytracker.app.data.entities.WeightEntry

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAll(): LiveData<List<WeightEntry>>

    @Insert
    suspend fun insert(entry: WeightEntry): Long

    @Update
    suspend fun update(entry: WeightEntry)

    @Delete
    suspend fun delete(entry: WeightEntry)
}
