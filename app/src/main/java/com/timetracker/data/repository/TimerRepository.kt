package com.timetracker.data.repository

import com.timetracker.data.local.PomodoroSessionDao
import com.timetracker.data.local.PomodoroSessionEntity
import com.timetracker.data.local.UserPreferencesManager
import com.timetracker.domain.model.PomodoroSession
import com.timetracker.domain.model.SessionType
import com.timetracker.domain.model.TimerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerRepository @Inject constructor(
    private val sessionDao: PomodoroSessionDao,
    private val preferencesManager: UserPreferencesManager
) {

    // Preferences
    val timerConfig: Flow<TimerConfig> = preferencesManager.timerConfig

    suspend fun updateTimerConfig(config: TimerConfig) {
        preferencesManager.updateTimerConfig(config)
    }

    // Sessions
    suspend fun insertSession(session: PomodoroSession): Long {
        val entity = session.toEntity()
        return sessionDao.insertSession(entity)
    }

    suspend fun updateSession(session: PomodoroSession) {
        sessionDao.updateSession(session.toEntity())
    }

    fun getAllSessions(): Flow<List<PomodoroSession>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getSessionsFromDate(startDate: Long): Flow<List<PomodoroSession>> {
        return sessionDao.getSessionsFromDate(startDate).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getSessionsForDay(startOfDay: Long, endOfDay: Long): Flow<List<PomodoroSession>> {
        return sessionDao.getSessionsForDay(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getCompletedSessionsCount(sessionType: SessionType): Int {
        return sessionDao.getCompletedSessionsCount(sessionType)
    }

    suspend fun incrementCompletedSessions() {
        preferencesManager.incrementCompletedSessions()
    }

    val totalCompletedSessions: Flow<Int> = preferencesManager.totalCompletedSessions

    suspend fun deleteAllSessions() {
        sessionDao.deleteAllSessions()
    }

    // Extension functions for mapping
    private fun PomodoroSession.toEntity(): PomodoroSessionEntity {
        return PomodoroSessionEntity(
            id = id,
            sessionType = sessionType,
            startTime = startTime,
            endTime = endTime,
            completed = completed,
            duration = duration
        )
    }

    private fun PomodoroSessionEntity.toDomainModel(): PomodoroSession {
        return PomodoroSession(
            id = id,
            sessionType = sessionType,
            startTime = startTime,
            endTime = endTime,
            completed = completed,
            duration = duration
        )
    }
}
