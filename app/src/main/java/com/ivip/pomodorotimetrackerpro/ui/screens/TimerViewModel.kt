package com.ivip.pomodorotimetrackerpro.ui.screens

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ivip.pomodorotimetrackerpro.data.repository.TimerRepository
import com.ivip.pomodorotimetrackerpro.domain.model.PomodoroSession
import com.ivip.pomodorotimetrackerpro.domain.model.SessionType
import com.ivip.pomodorotimetrackerpro.domain.model.TimerConfig
import com.ivip.pomodorotimetrackerpro.domain.model.TimerInfo
import com.ivip.pomodorotimetrackerpro.service.TimerService
import com.ivip.pomodorotimetrackerpro.util.StatisticsUtils
import com.ivip.pomodorotimetrackerpro.util.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsData(
    val totalFocusTime: Long = 0L,
    val completedWorkSessions: Int = 0,
    val todayCompletedSessions: Int = 0,
    val averageSessionDuration: Long = 0L,
    val currentStreak: Int = 0
)

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val repository: TimerRepository,
    application: Application
) : AndroidViewModel(application) {

    private var timerService: TimerService? = null
    private var bound = false

    private val _timerInfo = MutableStateFlow(
        TimerInfo(
            currentTime = 25 * 60 * 1000L,
            totalTime = 25 * 60 * 1000L,
            timerState = com.ivip.pomodorotimetrackerpro.domain.model.TimerState.IDLE,
            sessionType = SessionType.WORK,
            completedSessions = 0,
            currentSessionInCycle = 0
        )
    )
    val timerInfo: StateFlow<TimerInfo> = _timerInfo.asStateFlow()

    private val _timerConfig = MutableStateFlow(TimerConfig())
    val timerConfig: StateFlow<TimerConfig> = _timerConfig.asStateFlow()

    private val _statistics = MutableStateFlow(StatisticsData())
    val statistics: StateFlow<StatisticsData> = _statistics.asStateFlow()

    private val _allSessions = MutableStateFlow<List<PomodoroSession>>(emptyList())
    val allSessions: StateFlow<List<PomodoroSession>> = _allSessions.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            bound = true

            // Observar o timer info do service
            viewModelScope.launch {
                timerService?.timerInfo?.collect { info ->
                    _timerInfo.value = info
                }
            }

            // Configurar callback para conclusão de sessão
            timerService?.setOnSessionCompleteListener { sessionType, startTime, endTime, completed ->
                viewModelScope.launch {
                    saveSession(sessionType, startTime, endTime, completed)
                }
            }

            // Aplicar configuração atual ao service
            timerService?.setConfig(_timerConfig.value)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
            timerService = null
        }
    }

    init {
        loadTimerConfig()
        loadSessions()
        bindTimerService()
    }

    private fun bindTimerService() {
        val intent = Intent(getApplication(), TimerService::class.java)
        getApplication<Application>().bindService(
            intent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun loadTimerConfig() {
        viewModelScope.launch {
            repository.timerConfig.collect { config ->
                _timerConfig.value = config
                timerService?.setConfig(config)
            }
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            repository.getAllSessions().collect { sessions ->
                _allSessions.value = sessions
                updateStatistics(sessions)
            }
        }
    }

    private fun updateStatistics(sessions: List<PomodoroSession>) {
        val today = TimeUtils.getStartOfDay()
        val todaySessions = sessions.filter { it.startTime >= today }

        _statistics.value = StatisticsData(
            totalFocusTime = StatisticsUtils.calculateTotalFocusTime(sessions),
            completedWorkSessions = StatisticsUtils.getCompletedWorkSessions(sessions),
            todayCompletedSessions = StatisticsUtils.getCompletedWorkSessions(todaySessions),
            averageSessionDuration = StatisticsUtils.calculateAverageSessionDuration(sessions),
            currentStreak = calculateCurrentStreak(sessions)
        )
    }

    private fun calculateCurrentStreak(sessions: List<PomodoroSession>): Int {
        if (sessions.isEmpty()) return 0

        val today = TimeUtils.getStartOfDay()
        var streak = 0
        var currentDay = today

        while (true) {
            val dayStart = currentDay
            val dayEnd = TimeUtils.getEndOfDay(currentDay)
            val hasSessions = sessions.any { 
                it.startTime in dayStart..dayEnd && it.completed
            }

            if (hasSessions) {
                streak++
                currentDay -= 24 * 60 * 60 * 1000 // Voltar um dia
            } else {
                break
            }

            // Limite de 365 dias
            if (streak >= 365) break
        }

        return streak
    }

    private suspend fun saveSession(
        sessionType: SessionType,
        startTime: Long,
        endTime: Long,
        completed: Boolean
    ) {
        val session = PomodoroSession(
            sessionType = sessionType,
            startTime = startTime,
            endTime = endTime,
            completed = completed,
            duration = endTime - startTime
        )
        repository.insertSession(session)

        if (completed && sessionType == SessionType.WORK) {
            repository.incrementCompletedSessions()
        }
    }

    fun updateTimerConfig(config: TimerConfig) {
        viewModelScope.launch {
            repository.updateTimerConfig(config)
        }
    }

    fun startTimer() {
        timerService?.startTimer()
    }

    fun pauseTimer() {
        timerService?.pauseTimer()
    }

    fun resetTimer() {
        timerService?.resetTimer()
    }

    fun skipSession() {
        timerService?.skipSession()
    }

    fun getTodaySessions(): Flow<List<PomodoroSession>> {
        val startOfDay = TimeUtils.getStartOfDay()
        val endOfDay = TimeUtils.getEndOfDay()
        return repository.getSessionsForDay(startOfDay, endOfDay)
    }

    override fun onCleared() {
        super.onCleared()
        if (bound) {
            getApplication<Application>().unbindService(serviceConnection)
            bound = false
        }
    }
}
