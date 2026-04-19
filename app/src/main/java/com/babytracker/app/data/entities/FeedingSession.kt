package com.babytracker.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feeding_sessions")
data class FeedingSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val note: String = ""
)
