package com.ivip.pomodorotimetrackerpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivip.pomodorotimetrackerpro.util.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val config by viewModel.timerConfig.collectAsState()
    var workMinutes by remember { mutableStateOf(TimeUtils.millisToMinutes(config.workDuration)) }
    var shortBreakMinutes by remember { mutableStateOf(TimeUtils.millisToMinutes(config.shortBreakDuration)) }
    var longBreakMinutes by remember { mutableStateOf(TimeUtils.millisToMinutes(config.longBreakDuration)) }
    var sessionsUntilLongBreak by remember { mutableStateOf(config.sessionsUntilLongBreak) }
    var autoStartBreaks by remember { mutableStateOf(config.autoStartBreaks) }
    var autoStartPomodoros by remember { mutableStateOf(config.autoStartPomodoros) }
    var soundEnabled by remember { mutableStateOf(config.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(config.vibrationEnabled) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Timer Durations Section
            Text(
                text = "Durações",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            TimeSettingSlider(
                label = "Tempo de Foco",
                value = workMinutes,
                onValueChange = { workMinutes = it },
                valueRange = 1f..60f,
                unit = "min"
            )

            TimeSettingSlider(
                label = "Pausa Curta",
                value = shortBreakMinutes,
                onValueChange = { shortBreakMinutes = it },
                valueRange = 1f..30f,
                unit = "min"
            )

            TimeSettingSlider(
                label = "Pausa Longa",
                value = longBreakMinutes,
                onValueChange = { longBreakMinutes = it },
                valueRange = 5f..60f,
                unit = "min"
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Session Settings
            Text(
                text = "Sessões",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            TimeSettingSlider(
                label = "Sessões até pausa longa",
                value = sessionsUntilLongBreak,
                onValueChange = { sessionsUntilLongBreak = it },
                valueRange = 2f..8f,
                unit = "sessões"
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Auto-start Settings
            Text(
                text = "Início Automático",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            SettingSwitch(
                label = "Iniciar pausas automaticamente",
                checked = autoStartBreaks,
                onCheckedChange = { autoStartBreaks = it }
            )

            SettingSwitch(
                label = "Iniciar sessões automaticamente",
                checked = autoStartPomodoros,
                onCheckedChange = { autoStartPomodoros = it }
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Notification Settings
            Text(
                text = "Notificações",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            SettingSwitch(
                label = "Som do alarme",
                checked = soundEnabled,
                onCheckedChange = { soundEnabled = it }
            )

            SettingSwitch(
                label = "Vibração",
                checked = vibrationEnabled,
                onCheckedChange = { vibrationEnabled = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.updateTimerConfig(
                        config.copy(
                            workDuration = TimeUtils.minutesToMillis(workMinutes),
                            shortBreakDuration = TimeUtils.minutesToMillis(shortBreakMinutes),
                            longBreakDuration = TimeUtils.minutesToMillis(longBreakMinutes),
                            sessionsUntilLongBreak = sessionsUntilLongBreak,
                            autoStartBreaks = autoStartBreaks,
                            autoStartPomodoros = autoStartPomodoros,
                            soundEnabled = soundEnabled,
                            vibrationEnabled = vibrationEnabled
                        )
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Configurações")
            }
        }
    }
}

@Composable
fun TimeSettingSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    unit: String
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = valueRange,
            steps = (valueRange.endInclusive - valueRange.start - 1).toInt()
        )
    }
}

@Composable
fun SettingSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
