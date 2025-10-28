package com.timetracker.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    
    fun formatMillisToTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun formatMillisToMinutes(millis: Long): String {
        val minutes = millis / 60000
        return "${minutes}min"
    }

    fun getStartOfDay(timeInMillis: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun getEndOfDay(timeInMillis: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }

    fun formatDate(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timeInMillis))
    }

    fun formatDateTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timeInMillis))
    }

    fun formatTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timeInMillis))
    }

    fun isToday(timeInMillis: Long): Boolean {
        val today = getStartOfDay()
        val tomorrow = today + 24 * 60 * 60 * 1000
        return timeInMillis in today until tomorrow
    }

    fun getWeekStart(timeInMillis: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun minutesToMillis(minutes: Int): Long {
        return minutes * 60 * 1000L
    }

    fun millisToMinutes(millis: Long): Int {
        return (millis / 60000).toInt()
    }
}

object StatisticsUtils {
    
    fun calculateTotalFocusTime(sessions: List<com.timetracker.domain.model.PomodoroSession>): Long {
        return sessions
            .filter { it.sessionType == com.timetracker.domain.model.SessionType.WORK && it.completed }
            .sumOf { it.duration }
    }

    fun calculateAverageSessionDuration(sessions: List<com.timetracker.domain.model.PomodoroSession>): Long {
        val completedSessions = sessions.filter { it.completed }
        return if (completedSessions.isEmpty()) {
            0L
        } else {
            completedSessions.sumOf { it.duration } / completedSessions.size
        }
    }

    fun getCompletedWorkSessions(sessions: List<com.timetracker.domain.model.PomodoroSession>): Int {
        return sessions.count { 
            it.sessionType == com.timetracker.domain.model.SessionType.WORK && it.completed 
        }
    }

    fun getSessionsPerDay(sessions: List<com.timetracker.domain.model.PomodoroSession>): Map<String, Int> {
        return sessions
            .filter { it.completed }
            .groupBy { TimeUtils.formatDate(it.startTime) }
            .mapValues { it.value.size }
    }
}
