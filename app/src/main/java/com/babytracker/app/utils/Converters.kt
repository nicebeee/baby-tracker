package com.babytracker.app.utils

import androidx.room.TypeConverter
import com.babytracker.app.data.entities.DiaperType

class Converters {
    @TypeConverter
    fun fromDiaperType(value: DiaperType): String = value.name

    @TypeConverter
    fun toDiaperType(value: String): DiaperType = DiaperType.valueOf(value)
}
