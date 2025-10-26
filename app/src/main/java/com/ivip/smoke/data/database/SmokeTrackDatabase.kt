package com.ivip.smoketrack.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ivip.smoketrack.data.dao.CigaretteDao
import com.ivip.smoketrack.data.dao.DailyGoalDao
import com.ivip.smoketrack.data.entity.Cigarette
import com.ivip.smoketrack.data.entity.DailyGoal

@Database(
    entities = [Cigarette::class, DailyGoal::class],
    version = 1,
    exportSchema = false
)
abstract class SmokeTrackDatabase : RoomDatabase() {

    abstract fun cigaretteDao(): CigaretteDao
    abstract fun dailyGoalDao(): DailyGoalDao

    companion object {
        @Volatile
        private var INSTANCE: SmokeTrackDatabase? = null

        fun getDatabase(context: Context): SmokeTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmokeTrackDatabase::class.java,
                    "smoketrack_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}