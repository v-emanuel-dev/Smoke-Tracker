package com.timetracker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.timetracker.domain.model.TimerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timer_preferences")

class UserPreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val WORK_DURATION = longPreferencesKey("work_duration")
        val SHORT_BREAK_DURATION = longPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION = longPreferencesKey("long_break_duration")
        val SESSIONS_UNTIL_LONG_BREAK = intPreferencesKey("sessions_until_long_break")
        val AUTO_START_BREAKS = booleanPreferencesKey("auto_start_breaks")
        val AUTO_START_POMODOROS = booleanPreferencesKey("auto_start_pomodoros")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val TOTAL_COMPLETED_SESSIONS = intPreferencesKey("total_completed_sessions")
        val COMPLETED_SESSIONS_TODAY = intPreferencesKey("completed_sessions_today")
        val LAST_SESSION_DATE = longPreferencesKey("last_session_date")
    }

    val timerConfig: Flow<TimerConfig> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            TimerConfig(
                workDuration = preferences[PreferencesKeys.WORK_DURATION] ?: 25 * 60 * 1000L,
                shortBreakDuration = preferences[PreferencesKeys.SHORT_BREAK_DURATION] ?: 5 * 60 * 1000L,
                longBreakDuration = preferences[PreferencesKeys.LONG_BREAK_DURATION] ?: 15 * 60 * 1000L,
                sessionsUntilLongBreak = preferences[PreferencesKeys.SESSIONS_UNTIL_LONG_BREAK] ?: 4,
                autoStartBreaks = preferences[PreferencesKeys.AUTO_START_BREAKS] ?: false,
                autoStartPomodoros = preferences[PreferencesKeys.AUTO_START_POMODOROS] ?: false,
                soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
                vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true
            )
        }

    suspend fun updateTimerConfig(config: TimerConfig) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WORK_DURATION] = config.workDuration
            preferences[PreferencesKeys.SHORT_BREAK_DURATION] = config.shortBreakDuration
            preferences[PreferencesKeys.LONG_BREAK_DURATION] = config.longBreakDuration
            preferences[PreferencesKeys.SESSIONS_UNTIL_LONG_BREAK] = config.sessionsUntilLongBreak
            preferences[PreferencesKeys.AUTO_START_BREAKS] = config.autoStartBreaks
            preferences[PreferencesKeys.AUTO_START_POMODOROS] = config.autoStartPomodoros
            preferences[PreferencesKeys.SOUND_ENABLED] = config.soundEnabled
            preferences[PreferencesKeys.VIBRATION_ENABLED] = config.vibrationEnabled
        }
    }

    suspend fun incrementCompletedSessions() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.TOTAL_COMPLETED_SESSIONS] ?: 0
            preferences[PreferencesKeys.TOTAL_COMPLETED_SESSIONS] = current + 1
        }
    }

    val totalCompletedSessions: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.TOTAL_COMPLETED_SESSIONS] ?: 0
        }
}
