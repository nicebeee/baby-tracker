package com.babytracker.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DiaperType { WET, DIRTY, BOTH }

@Entity(tableName = "diaper_entries")
data class DiaperEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateTime: Long,
    val type: DiaperType,
    val note: String = ""
)
