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
import com.example.trackfit.database.TrackFitDao
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun ExerciseLogsPage(
    exerciseId: Int, navController: NavController, dao: TrackFitDao
) {
    var logs by remember { mutableStateOf<List<ExerciseLogEntity>>(emptyList()) }
    var isDeleting by remember { mutableStateOf(false) }

    fun refreshLogs() {
        MainScope().launch {
            logs = dao.getExerciseLogsById(exerciseId)
        }
    }

    LaunchedEffect(exerciseId) {
        refreshLogs()
    }

    Scaffold(
        bottomBar = { NavBar(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (logs.isEmpty()) {
                EmptyLogsState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(logs) { log ->
                        LogItemCard(log = log, onDelete = { logToDelete ->
                            isDeleting = true
                            MainScope().launch {
                                dao.deleteExerciseLogs(logToDelete)
                                refreshLogs()
                                isDeleting = false
                            }
                        })
                    }
                }
            }
        }
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
