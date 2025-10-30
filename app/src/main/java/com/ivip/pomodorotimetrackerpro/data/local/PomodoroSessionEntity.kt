package com.ivip.pomodorotimetrackerpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivip.pomodorotimetrackerpro.domain.model.SessionType

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionType: SessionType,
    val startTime: Long,
    val endTime: Long,
    val completed: Boolean,
    val duration: Long
)
