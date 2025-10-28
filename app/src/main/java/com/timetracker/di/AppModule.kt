package com.timetracker.di

import android.content.Context
import androidx.room.Room
import com.timetracker.data.local.PomodoroSessionDao
import com.timetracker.data.local.TimeTrackerDatabase
import com.timetracker.data.local.UserPreferencesManager
import com.timetracker.data.repository.TimerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTimeTrackerDatabase(
        @ApplicationContext context: Context
    ): TimeTrackerDatabase {
        return Room.databaseBuilder(
            context,
            TimeTrackerDatabase::class.java,
            "timetracker_database"
        )
            .fallbackToDestructiveMigration()  // Add this line
            .build()
    }

    @Provides
    @Singleton
    fun providePomodoroSessionDao(database: TimeTrackerDatabase): PomodoroSessionDao {
        return database.pomodoroSessionDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesManager(
        @ApplicationContext context: Context
    ): UserPreferencesManager {
        return UserPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideTimerRepository(
        sessionDao: PomodoroSessionDao,
        preferencesManager: UserPreferencesManager
    ): TimerRepository {
        return TimerRepository(sessionDao, preferencesManager)
    }
}
