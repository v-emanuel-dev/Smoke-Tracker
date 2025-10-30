package com.ivip.pomodorotimetrackerpro.data.local

import androidx.room.*
import com.ivip.pomodorotimetrackerpro.domain.model.SessionType
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PomodoroSessionEntity): Long

    @Update
    suspend fun updateSession(session: PomodoroSessionEntity): Int

    @Query("SELECT * FROM pomodoro_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<PomodoroSessionEntity>>

    @Query("SELECT * FROM pomodoro_sessions WHERE startTime >= :startDate ORDER BY startTime DESC")
    fun getSessionsFromDate(startDate: Long): Flow<List<PomodoroSessionEntity>>

    @Query("""
        SELECT * FROM pomodoro_sessions 
        WHERE startTime BETWEEN :startOfDay AND :endOfDay
        ORDER BY startTime DESC
    """)
    fun getSessionsForDay(startOfDay: Long, endOfDay: Long): Flow<List<PomodoroSessionEntity>>

    @Query("""
        SELECT COUNT(*) FROM pomodoro_sessions 
        WHERE sessionType = :sessionType AND completed = 1
    """)
    suspend fun getCompletedSessionsCount(sessionType: SessionType): Int

    @Query("DELETE FROM pomodoro_sessions")
    suspend fun deleteAllSessions(): Int
}
