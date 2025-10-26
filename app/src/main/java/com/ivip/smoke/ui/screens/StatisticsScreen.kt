package com.ivip.smoketrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivip.smoketrack.ui.theme.DangerRed
import com.ivip.smoketrack.ui.theme.HealthGreen
import com.ivip.smoketrack.ui.viewmodel.SmokeTrackUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    uiState: SmokeTrackUiState,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estatísticas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Visão Geral",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                OverviewCard(uiState = uiState)
            }

            item {
                Text(
                    text = "Comparativo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                ComparisonCard(uiState = uiState)
            }

            item {
                Text(
                    text = "Progresso",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                ProgressCard(uiState = uiState)
            }

            item {
                Text(
                    text = "Análise Temporal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                TimeAnalysisCard(uiState = uiState)
            }
        }
    }
}

@Composable
fun OverviewCard(uiState: SmokeTrackUiState) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            StatRow(
                label = "Hoje",
                value = "${uiState.todayCount}",
                unit = "cigarros"
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            StatRow(
                label = "Esta Semana",
                value = "${uiState.weekCount}",
                unit = "cigarros"
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            StatRow(
                label = "Este Mês",
                value = "${uiState.monthCount}",
                unit = "cigarros"
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            StatRow(
                label = "Média Diária",
                value = String.format("%.1f", uiState.averagePerDay),
                unit = "cigarros/dia"
            )
        }
    }
}

@Composable
fun ComparisonCard(uiState: SmokeTrackUiState) {
    val dailyGoal = uiState.dailyGoal?.maxCigarettesPerDay ?: 20
    val todayVsGoal = uiState.todayCount - dailyGoal
    val avgVsGoal = uiState.averagePerDay - dailyGoal

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            ComparisonItem(
                label = "Hoje vs Meta",
                difference = todayVsGoal,
                isPositive = todayVsGoal <= 0
            )

            Spacer(modifier = Modifier.height(16.dp))

            ComparisonItem(
                label = "Média vs Meta",
                difference = avgVsGoal.toInt(),
                isPositive = avgVsGoal <= 0
            )
        }
    }
}

@Composable
fun ComparisonItem(
    label: String,
    difference: Int,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isPositive) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                contentDescription = null,
                tint = if (isPositive) HealthGreen else DangerRed,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${if (difference > 0) "+" else ""}$difference",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) HealthGreen else DangerRed
            )
        }
    }
}

@Composable
fun ProgressCard(uiState: SmokeTrackUiState) {
    val dailyGoal = uiState.dailyGoal?.maxCigarettesPerDay ?: 20
    val weeklyGoal = dailyGoal * 7
    val monthlyGoal = dailyGoal * 30

    val weekProgress = (uiState.weekCount.toFloat() / weeklyGoal).coerceIn(0f, 1f)
    val monthProgress = (uiState.monthCount.toFloat() / monthlyGoal).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            ProgressItem(
                label = "Progresso Semanal",
                current = uiState.weekCount,
                goal = weeklyGoal,
                progress = weekProgress
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProgressItem(
                label = "Progresso Mensal",
                current = uiState.monthCount,
                goal = monthlyGoal,
                progress = monthProgress
            )
        }
    }
}

@Composable
fun ProgressItem(
    label: String,
    current: Int,
    goal: Int,
    progress: Float
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$current / $goal",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = when {
                progress < 0.7f -> HealthGreen
                progress < 0.9f -> MaterialTheme.colorScheme.primary
                else -> DangerRed
            },
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${(progress * 100).toInt()}% da meta",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TimeAnalysisCard(uiState: SmokeTrackUiState) {
    val estimatedYearly = (uiState.averagePerDay * 365).toInt()
    val moneySavedPotential = calculateMoneySaved(uiState)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            AnalysisItem(
                label = "Projeção Anual",
                value = "$estimatedYearly cigarros",
                description = "Se manter a média atual"
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            AnalysisItem(
                label = "Economia Potencial",
                value = "R$ ${String.format("%.2f", moneySavedPotential)}/mês",
                description = "Reduzindo para a meta"
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            AnalysisItem(
                label = "Tempo de Uso",
                value = "${uiState.todayCount * 5} min",
                description = "Estimado hoje (5 min/cigarro)"
            )
        }
    }
}

@Composable
fun AnalysisItem(
    label: String,
    value: String,
    description: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatRow(
    label: String,
    value: String,
    unit: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = unit,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

private fun calculateMoneySaved(uiState: SmokeTrackUiState): Double {
    val dailyGoal = uiState.dailyGoal?.maxCigarettesPerDay ?: 20
    val currentAverage = uiState.averagePerDay
    val potentialSaving = currentAverage - dailyGoal

    if (potentialSaving <= 0) return 0.0

    // Assumindo R$ 15,00 por maço de 20 cigarros
    val pricePerCigarette = 15.0 / 20.0
    return potentialSaving * pricePerCigarette * 30 // Por mês
}