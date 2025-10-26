package com.ivip.smoketrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ivip.smoketrack.ui.viewmodel.SmokeTrackUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SmokeTrackUiState,
    onNavigateBack: () -> Unit,
    onSaveGoal: (Int, Int) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
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
                    text = "Meta Diária",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                GoalCard(
                    currentGoal = uiState.dailyGoal?.maxCigarettesPerDay ?: 20,
                    targetReduction = uiState.dailyGoal?.targetReduction ?: 1,
                    onEdit = { showEditDialog = true }
                )
            }

            item {
                Text(
                    text = "Informações",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                InfoCard()
            }

            item {
                Text(
                    text = "Sobre",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                AboutCard()
            }
        }

        if (showEditDialog) {
            EditGoalDialog(
                currentGoal = uiState.dailyGoal?.maxCigarettesPerDay ?: 20,
                currentReduction = uiState.dailyGoal?.targetReduction ?: 1,
                onDismiss = { showEditDialog = false },
                onSave = { maxCigs, reduction ->
                    onSaveGoal(maxCigs, reduction)
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun GoalCard(
    currentGoal: Int,
    targetReduction: Int,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Máximo por Dia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$currentGoal cigarros",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                FilledIconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Redução Semanal",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$targetReduction cigarro(s) por semana",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun EditGoalDialog(
    currentGoal: Int,
    currentReduction: Int,
    onDismiss: () -> Unit,
    onSave: (Int, Int) -> Unit
) {
    var maxCigarettes by remember { mutableStateOf(currentGoal.toString()) }
    var reduction by remember { mutableStateOf(currentReduction.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Meta") },
        text = {
            Column {
                Text("Defina sua meta diária:")
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = maxCigarettes,
                    onValueChange = { maxCigarettes = it.filter { char -> char.isDigit() } },
                    label = { Text("Máximo por dia") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Redução semanal desejada:")
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reduction,
                    onValueChange = { reduction = it.filter { char -> char.isDigit() } },
                    label = { Text("Cigarros por semana") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val max = maxCigarettes.toIntOrNull() ?: currentGoal
                    val red = reduction.toIntOrNull() ?: currentReduction
                    onSave(max, red)
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "💡 Dicas para Reduzir",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            TipItem("Identifique os gatilhos que te fazem fumar")
            TipItem("Substitua o hábito por algo saudável")
            TipItem("Beba água quando sentir vontade")
            TipItem("Pratique exercícios físicos regularmente")
            TipItem("Busque apoio de amigos e família")
        }
    }
}

@Composable
fun TipItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "• ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun AboutCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "SmokeTrack",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Versão 1.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Um aplicativo simples e eficaz para monitorar e reduzir o consumo de cigarros.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Desenvolvido com ❤️ usando Jetpack Compose",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}