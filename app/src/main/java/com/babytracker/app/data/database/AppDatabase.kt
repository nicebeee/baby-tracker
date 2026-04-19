package com.babytracker.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.babytracker.app.data.dao.*
import com.babytracker.app.data.entities.*
import com.babytracker.app.utils.Converters

@Database(
    entities = [FeedingSession::class, SleepSession::class, WeightEntry::class, DiaperEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedingDao(): FeedingDao
    abstract fun sleepDao(): SleepDao
    abstract fun weightDao(): WeightDao
    abstract fun diaperDao(): DiaperDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "baby_tracker.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
