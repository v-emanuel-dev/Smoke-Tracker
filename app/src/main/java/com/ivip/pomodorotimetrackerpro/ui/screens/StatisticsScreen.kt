package com.ivip.pomodorotimetrackerpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivip.pomodorotimetrackerpro.domain.model.PomodoroSession
import com.ivip.pomodorotimetrackerpro.domain.model.SessionType
import com.ivip.pomodorotimetrackerpro.ui.components.StatisticsCard
import com.ivip.pomodorotimetrackerpro.util.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val statistics by viewModel.statistics.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estatísticas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Statistics Cards
            item {
                Text(
                    text = "Resumo Geral",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatisticsCard(
                        title = "Hoje",
                        value = "${statistics.todayCompletedSessions}",
                        subtitle = "sessões",
                        modifier = Modifier.weight(1f)
                    )
                    StatisticsCard(
                        title = "Sequência",
                        value = "${statistics.currentStreak}",
                        subtitle = "dias",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatisticsCard(
                        title = "Total",
                        value = "${statistics.completedWorkSessions}",
                        subtitle = "sessões",
                        modifier = Modifier.weight(1f)
                    )
                    StatisticsCard(
                        title = "Tempo Total",
                        value = TimeUtils.formatMillisToMinutes(statistics.totalFocusTime),
                        subtitle = "de foco",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Session History
            item {
                Text(
                    text = "Histórico de Sessões",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            if (allSessions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        ) {
                            Text(
                                text = "Nenhuma sessão registrada ainda.\nComece seu primeiro Pomodoro!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(allSessions.take(20)) { session ->
                    SessionHistoryItem(session = session)
                }
            }
        }
    }
}

@Composable
fun SessionHistoryItem(
    session: PomodoroSession,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (session.sessionType) {
                        SessionType.WORK -> "🎯 Sessão de Foco"
                        SessionType.SHORT_BREAK -> "☕ Pausa Curta"
                        SessionType.LONG_BREAK -> "🌴 Pausa Longa"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = TimeUtils.formatDateTime(session.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(
                    text = if (session.completed) "✓ Concluída" else "✗ Incompleta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (session.completed) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = TimeUtils.formatMillisToMinutes(session.duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
