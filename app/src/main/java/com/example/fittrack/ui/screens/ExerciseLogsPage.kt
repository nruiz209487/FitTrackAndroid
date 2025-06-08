package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.ui.ui_elements.NavBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.fittrack.database.TrackFitDao
import com.example.fittrack.service.Service
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun ExerciseLogsPage(
    exerciseId: Int, navController: NavController, dao: TrackFitDao
) {
    var allLogs by remember { mutableStateOf<List<ExerciseLogEntity>>(emptyList()) }
    var filteredLogs by remember { mutableStateOf<List<ExerciseLogEntity>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var logToDelete by remember { mutableStateOf<ExerciseLogEntity?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    fun refreshLogs() {
        MainScope().launch {
            allLogs = dao.getExerciseLogsById(exerciseId)
            filteredLogs = allLogs
        }
    }

    LaunchedEffect(exerciseId) {
        refreshLogs()
    }

    LaunchedEffect(searchQuery, allLogs) {
        filteredLogs = if (searchQuery.isBlank()) {
            allLogs
        } else {
            allLogs.filter { log ->
                log.date.contains(searchQuery, ignoreCase = true) ||
                        log.weight.toString().contains(searchQuery, ignoreCase = true) ||
                        log.reps.toString().contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        bottomBar = { NavBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Buscar registros...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (allLogs.isEmpty()) {
                        EmptyLogsState()
                    } else {
                        Text(
                            text = "No se encontraron registros con '$searchQuery'",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredLogs) { log ->
                        LogItemCard(
                            log = log,
                            onDelete = { logToDelete = it }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (logToDelete != null) {
        AlertDialog(
            onDismissRequest = { logToDelete = null },
            title = { Text("Confirmar eliminación") },
            text = {
                Column {
                    Text("¿Estás seguro de que quieres eliminar este registro?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${logToDelete?.weight} kg × ${logToDelete?.reps} reps",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = logToDelete?.date ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleting = true
                        MainScope().launch {
                            logToDelete?.let {Service.deleteExerciseLog(it)
                            }
                            refreshLogs()
                            isDeleting = false
                            logToDelete = null
                        }
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { logToDelete = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
@Composable
private fun LogItemCard(log: ExerciseLogEntity, onDelete: (ExerciseLogEntity) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("Peso", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${log.weight} kg",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Repeticiones", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${log.reps}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                if (log.date.isNotEmpty()) {
                    Text(
                        text = log.date,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { onDelete(log) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar registro",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyLogsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📝", style = MaterialTheme.typography.displayMedium
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No hay registros guardados",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Comienza a agregar registros para ver tu progreso",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
