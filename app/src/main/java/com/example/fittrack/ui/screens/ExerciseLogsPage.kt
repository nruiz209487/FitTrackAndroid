package com.example.fittrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.ui.ui_elements.NavBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.fittrack.database.TrackFitDao
import com.example.fittrack.service.Service
import com.example.fittrack.ui.ui_elements.SearchBarComposable
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import com.example.fittrack.type_converters.formatGlobalTimestamp
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ExerciseLogsPage(
    exerciseId: Int, navController: NavController, dao: TrackFitDao
) {
    var allLogs by remember { mutableStateOf<List<ExerciseLogEntity>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var logToDelete by remember { mutableStateOf<ExerciseLogEntity?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    val filteredLogs = remember(searchQuery, allLogs) {
        val logs = if (searchQuery.isBlank()) allLogs
        else allLogs.filter { log ->
            log.date.contains(searchQuery, ignoreCase = true) ||
                    log.weight.toString().contains(searchQuery, ignoreCase = true) ||
                    log.reps.toString().contains(searchQuery, ignoreCase = true)
        }
        logs.sortedByDescending { log ->
            formatGlobalTimestamp(log.date)
        }
    }

    val groupedLogs = remember(filteredLogs) {
        filteredLogs.groupBy { log ->
            val date = formatGlobalTimestamp(log.date)
            date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
                .replaceFirstChar { it.uppercase() }
        }.toList().sortedByDescending { (_, logs) ->
            logs.maxOfOrNull { formatGlobalTimestamp(it.date) }
        }
    }

    fun refreshLogs() {
        MainScope().launch {
            allLogs = dao.getExerciseLogsById(exerciseId)
        }
    }

    LaunchedEffect(exerciseId) {
        refreshLogs()
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
            SearchBarComposable(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholderText = "Buscar registros..."
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (groupedLogs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (allLogs.isEmpty()) {
                                EmptyLogsState()
                            } else {
                                Text(
                                    text = "No se encontraron registros",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    groupedLogs.forEach { (monthYear, logs) ->
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                        alpha = 0.3f
                                    )
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = monthYear,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        items(logs) { log ->
                            LogItemCard(log = log, onDelete = { logToDelete = it })
                        }
                    }
                }
            }
        }
    }

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
                            logToDelete?.let { Service.deleteExerciseLog(it) }
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
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            "Peso",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${log.weight} kg",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Repeticiones",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${log.reps}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                if (log.date.isNotEmpty()) {
                    Text(
                        text = formatGlobalTimestamp(log.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { onDelete(log) }) {
                Icon(
                    Icons.Default.Delete,
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📝",
            style = MaterialTheme.typography.displayMedium
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
