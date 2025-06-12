package com.example.fittrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.ui.ui_elements.NavBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.fittrack.database.TrackFitDao
import com.example.fittrack.service.Service
import com.example.fittrack.ui.ui_elements.SearchBarComposable
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.fittrack.type_converters.formatGlobalTimestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Pagina que muestra los registros de ejercicios y permite crear nuevos
 */
@Composable
fun ExerciseLogsPage(
    exerciseId: Int, navController: NavController, dao: TrackFitDao
) {
    var allLogs by remember { mutableStateOf<List<ExerciseLogEntity>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var logToDelete by remember { mutableStateOf<ExerciseLogEntity?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    // Estados para crear nuevo registro
    var showCreateDialog by remember { mutableStateOf(false) }
    var weightText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }

    //filtro de la barra de busqueda
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

    //basicamente ordena los logs por fecha
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
        refreshLogs() // actualiza la lista
    }

    // Espera
    LaunchedEffect(showSavedMessage) {
        if (showSavedMessage) {
            delay(3000)
            showSavedMessage = false
        }
    }

    Scaffold(
        bottomBar = { NavBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar registro",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
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

            // mesaje guardado
            if (showSavedMessage) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "✅ ¡Registro creado correctamente!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // si los losg estan vacios
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
                    // si no simplemente lo sitera y llaam a LogItemCard
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

    //  crear nuevo log
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isSaving) {
                    showCreateDialog = false
                    weightText = ""
                    repsText = ""
                }
            },
            title = { Text("Nuevo Registro") },
            text = {
                Column {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = {
                            weightText = it.filter { char -> char.isDigit() || char == '.' }
                        },
                        label = { Text("Peso (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = repsText,
                        onValueChange = {
                            repsText = it.filter { char -> char.isDigit() }
                        },
                        label = { Text("Repeticiones") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (weightText.isNotEmpty() && repsText.isNotEmpty()) {
                            isSaving = true
                            val log = ExerciseLogEntity(
                                exerciseId = exerciseId,
                                date = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                                weight = weightText.toFloatOrNull() ?: 0f,
                                reps = repsText.toIntOrNull() ?: 0
                            )

                            MainScope().launch {
                                Service.insertExerciseLogToApi(log)
                                refreshLogs()
                                isSaving = false
                                showCreateDialog = false
                                weightText = ""
                                repsText = ""
                                showSavedMessage = true
                            }
                        }
                    },
                    enabled = weightText.isNotEmpty() && repsText.isNotEmpty() && !isSaving
                ) {
                    if (isSaving) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Guardando...")
                        }
                    } else {
                        Text("Guardar")
                    }
                }
            },
            //boron cancelar registro
            dismissButton = {
                TextButton(
                    onClick = {
                        if (!isSaving) {
                            showCreateDialog = false
                            weightText = ""
                            repsText = ""
                        }
                    },
                    enabled = !isSaving
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    //elimina un log
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

/**
 * Composable para mostrar un solo log
 */
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
                    //muestar peso
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
                    //mestra repetciones
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
                        text = formatGlobalTimestamp(log.date).format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        //boton para eliminars
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

/**
 * si no hay logs se meustras esto
 */
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
            text = "Toca el botón + para agregar tu primer registro",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}