package com.timetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.timetracker.domain.model.TimerState
import com.timetracker.ui.components.CircularTimer
import com.timetracker.ui.components.SessionIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val timerInfo by viewModel.timerInfo.collectAsState()
    val config by viewModel.timerConfig.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TimeTracker") },
                actions = {
                    IconButton(onClick = onNavigateToStatistics) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Estatísticas"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Session Indicator
            SessionIndicator(
                completedSessions = timerInfo.completedSessions,
                currentSession = timerInfo.currentSessionInCycle,
                totalSessions = config.sessionsUntilLongBreak,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Circular Timer
            CircularTimer(
                currentTime = timerInfo.currentTime,
                totalTime = timerInfo.totalTime,
                sessionType = timerInfo.sessionType,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            )

            // Control Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Start/Pause Button
                    FloatingActionButton(
                        onClick = {
                            when (timerInfo.timerState) {
                                TimerState.IDLE, TimerState.PAUSED -> viewModel.startTimer()
                                TimerState.RUNNING -> viewModel.pauseTimer()
                                TimerState.FINISHED -> viewModel.startTimer()
                            }
                        },
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = when (timerInfo.timerState) {
                                TimerState.RUNNING -> Icons.Default.Pause
                                else -> Icons.Default.PlayArrow
                            },
                            contentDescription = when (timerInfo.timerState) {
                                TimerState.RUNNING -> "Pausar"
                                else -> "Iniciar"
                            },
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Reset Button
                    OutlinedButton(
                        onClick = { viewModel.resetTimer() },
                        enabled = timerInfo.timerState != TimerState.IDLE
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Resetar",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Resetar")
                    }

                    // Skip Button
                    OutlinedButton(
                        onClick = { viewModel.skipSession() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Pular",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pular")
                    }
                }
            }

            // Statistics Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${timerInfo.completedSessions}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Sessões Hoje",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
