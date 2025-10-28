package com.timetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [PomodoroSessionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TimeTrackerDatabase : RoomDatabase() {
    abstract fun pomodoroSessionDao(): PomodoroSessionDao
}
