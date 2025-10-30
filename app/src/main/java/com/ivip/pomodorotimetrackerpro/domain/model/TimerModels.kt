package com.ivip.pomodorotimetrackerpro.domain.model

enum class TimerState {
    IDLE,
    RUNNING,
    PAUSED,
    FINISHED
}

enum class SessionType {
    WORK,
    SHORT_BREAK,
    LONG_BREAK
}

data class TimerConfig(
    val workDuration: Long = 25 * 60 * 1000L, // 25 minutos em milissegundos
    val shortBreakDuration: Long = 5 * 60 * 1000L, // 5 minutos
    val longBreakDuration: Long = 15 * 60 * 1000L, // 15 minutos
    val sessionsUntilLongBreak: Int = 4,
    val autoStartBreaks: Boolean = false,
    val autoStartPomodoros: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

data class PomodoroSession(
    val id: Long = 0,
    val sessionType: SessionType,
    val startTime: Long,
    val endTime: Long,
    val completed: Boolean,
    val duration: Long
)

data class TimerInfo(
    val currentTime: Long,
    val totalTime: Long,
    val timerState: TimerState,
    val sessionType: SessionType,
    val completedSessions: Int,
    val currentSessionInCycle: Int
)
