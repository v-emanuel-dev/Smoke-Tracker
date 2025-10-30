package com.ivip.pomodorotimetrackerpro.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.ivip.pomodorotimetrackerpro.MainActivity
import com.ivip.pomodorotimetrackerpro.R
import com.ivip.pomodorotimetrackerpro.domain.model.SessionType
import com.ivip.pomodorotimetrackerpro.domain.model.TimerConfig
import com.ivip.pomodorotimetrackerpro.domain.model.TimerInfo
import com.ivip.pomodorotimetrackerpro.domain.model.TimerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@AndroidEntryPoint
class TimerService : Service() {

    private val binder = TimerBinder()
    private var countDownTimer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    private val _timerInfo = MutableStateFlow(
        TimerInfo(
            currentTime = 25 * 60 * 1000L,
            totalTime = 25 * 60 * 1000L,
            timerState = TimerState.IDLE,
            sessionType = SessionType.WORK,
            completedSessions = 0,
            currentSessionInCycle = 0
        )
    )
    val timerInfo: StateFlow<TimerInfo> = _timerInfo.asStateFlow()

    private var timerConfig: TimerConfig = TimerConfig()
    private var sessionStartTime: Long = 0
    private var onSessionComplete: ((SessionType, Long, Long, Boolean) -> Unit)? = null

    companion object {
        private const val CHANNEL_ID = "timer_service_channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESET = "ACTION_RESET"
        const val ACTION_SKIP = "ACTION_SKIP"
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeVibrator()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTimer()
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESET -> resetTimer()
            ACTION_SKIP -> skipSession()
        }
        return START_STICKY
    }

    private fun initializeVibrator() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun setConfig(config: TimerConfig) {
        timerConfig = config
        if (_timerInfo.value.timerState == TimerState.IDLE) {
            updateTimerForCurrentSession()
        }
    }

    fun setOnSessionCompleteListener(listener: (SessionType, Long, Long, Boolean) -> Unit) {
        onSessionComplete = listener
    }

    fun startTimer() {
        if (_timerInfo.value.timerState == TimerState.RUNNING) return

        val currentInfo = _timerInfo.value
        
        if (currentInfo.timerState == TimerState.IDLE) {
            sessionStartTime = System.currentTimeMillis()
        }

        countDownTimer = object : CountDownTimer(currentInfo.currentTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timerInfo.value = currentInfo.copy(
                    currentTime = millisUntilFinished,
                    timerState = TimerState.RUNNING
                )
                updateNotification()
            }

            override fun onFinish() {
                handleTimerComplete()
            }
        }.start()

        _timerInfo.value = currentInfo.copy(timerState = TimerState.RUNNING)
        startForeground(NOTIFICATION_ID, createNotification())
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        _timerInfo.value = _timerInfo.value.copy(timerState = TimerState.PAUSED)
        updateNotification()
    }

    fun resetTimer() {
        countDownTimer?.cancel()
        updateTimerForCurrentSession()
        stopForeground(STOP_FOREGROUND_REMOVE)
        updateNotification()
    }

    fun skipSession() {
        countDownTimer?.cancel()
        moveToNextSession()
        if (shouldAutoStart()) {
            startTimer()
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE)
            updateNotification()
        }
    }

    private fun handleTimerComplete() {
        val currentInfo = _timerInfo.value
        val endTime = System.currentTimeMillis()
        
        // Tocar som e vibrar
        if (timerConfig.soundEnabled) {
            playAlarmSound()
        }
        if (timerConfig.vibrationEnabled) {
            vibrateDevice()
        }

        // Notificar conclusão da sessão
        onSessionComplete?.invoke(
            currentInfo.sessionType,
            sessionStartTime,
            endTime,
            true
        )

        // Atualizar contador de sessões
        val newCompletedSessions = if (currentInfo.sessionType == SessionType.WORK) {
            currentInfo.completedSessions + 1
        } else {
            currentInfo.completedSessions
        }

        _timerInfo.value = currentInfo.copy(
            timerState = TimerState.FINISHED,
            completedSessions = newCompletedSessions
        )

        // Mover para próxima sessão
        moveToNextSession()

        // Auto-iniciar se configurado
        if (shouldAutoStart()) {
            android.os.Handler(mainLooper).postDelayed({
                startTimer()
            }, 2000)
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    private fun shouldAutoStart(): Boolean {
        return when (_timerInfo.value.sessionType) {
            SessionType.WORK -> timerConfig.autoStartPomodoros
            SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> timerConfig.autoStartBreaks
        }
    }

    private fun moveToNextSession() {
        val currentInfo = _timerInfo.value
        val nextSession: SessionType
        val nextSessionInCycle: Int

        when (currentInfo.sessionType) {
            SessionType.WORK -> {
                val sessionsInCycle = currentInfo.currentSessionInCycle + 1
                nextSession = if (sessionsInCycle >= timerConfig.sessionsUntilLongBreak) {
                    SessionType.LONG_BREAK
                } else {
                    SessionType.SHORT_BREAK
                }
                nextSessionInCycle = sessionsInCycle
            }
            SessionType.SHORT_BREAK -> {
                nextSession = SessionType.WORK
                nextSessionInCycle = currentInfo.currentSessionInCycle
            }
            SessionType.LONG_BREAK -> {
                nextSession = SessionType.WORK
                nextSessionInCycle = 0
            }
        }

        val duration = getDurationForSessionType(nextSession)
        sessionStartTime = System.currentTimeMillis()

        _timerInfo.value = currentInfo.copy(
            currentTime = duration,
            totalTime = duration,
            timerState = TimerState.IDLE,
            sessionType = nextSession,
            currentSessionInCycle = nextSessionInCycle
        )

        updateNotification()
    }

    private fun updateTimerForCurrentSession() {
        val currentInfo = _timerInfo.value
        val duration = getDurationForSessionType(currentInfo.sessionType)
        _timerInfo.value = currentInfo.copy(
            currentTime = duration,
            totalTime = duration,
            timerState = TimerState.IDLE
        )
    }

    private fun getDurationForSessionType(sessionType: SessionType): Long {
        return when (sessionType) {
            SessionType.WORK -> timerConfig.workDuration
            SessionType.SHORT_BREAK -> timerConfig.shortBreakDuration
            SessionType.LONG_BREAK -> timerConfig.longBreakDuration
        }
    }

    private fun playAlarmSound() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setOnCompletionListener { it.release() }
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibrateDevice() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 500, 200, 500),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mostra o timer Pomodoro em execução"
                setSound(null, null)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val currentInfo = _timerInfo.value
        val timeText = formatTime(currentInfo.currentTime)
        val sessionText = when (currentInfo.sessionType) {
            SessionType.WORK -> "Trabalho"
            SessionType.SHORT_BREAK -> "Pausa Curta"
            SessionType.LONG_BREAK -> "Pausa Longa"
        }

        val stateText = when (currentInfo.timerState) {
            TimerState.RUNNING -> "Em execução"
            TimerState.PAUSED -> "Pausado"
            TimerState.IDLE -> "Pronto"
            TimerState.FINISHED -> "Concluído"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("TimeTracker - $sessionText")
            .setContentText("$timeText - $stateText")
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        mediaPlayer?.release()
    }
}
