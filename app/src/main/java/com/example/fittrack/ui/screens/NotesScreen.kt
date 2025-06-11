package com.example.fittrack.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.ui.ui_elements.NavBar
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import com.example.fittrack.ui.ui_elements.SearchBarComposable
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.ui.text.style.TextAlign
import com.example.fittrack.database.TrackFitDao
import com.example.fittrack.service.Service
import com.example.fittrack.ui.helpers.NotificationCreator
import java.util.Locale
import com.example.fittrack.type_converters.formatGlobalTimestamp

/**
 * Pagina que meustra y crea notas
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotesScreen(navController: NavController, dao: TrackFitDao) {
    var allNotes by remember { mutableStateOf<List<NoteEntity>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }
    var hasNotificationPermission by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }
    // Filtro barra busqueda
    val filteredNotes = remember(searchQuery, allNotes) {
        val notes = if (searchQuery.isBlank()) allNotes
        else allNotes.filter {
            it.header.contains(searchQuery, true) ||
                    it.text.contains(searchQuery, true)
        }
        notes.sortedByDescending { note ->
            formatGlobalTimestamp(note.timestamp)
        }
    }

    val groupedNotes = remember(filteredNotes) {
        filteredNotes.groupBy { note ->
            val date = formatGlobalTimestamp(note.timestamp)
            date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
                .replaceFirstChar { it.uppercase() }
        }.toList().sortedByDescending { (_, notes) ->
            notes.maxOfOrNull { formatGlobalTimestamp(it.timestamp) }
        }
    }
    // refresca notas
    fun refreshNotes() {
        MainScope().launch { allNotes = dao.getNotes() }
    }

    LaunchedEffect(Unit) {
        refreshNotes()
        hasNotificationPermission = NotificationCreator.hasNotificationPermission(context)
        // compreuaba el permiso de notificacion
        if (!hasNotificationPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(bottomBar = { NavBar(navController) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            //Si no tiene persmios los pides
            if (!hasNotificationPermission) {
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Icon(Icons.Default.NotificationsOff, contentDescription = "Permisos faltantes")
                    Spacer(Modifier.width(8.dp))
                    Text("Activar notificaciones")
                }
            }
            //  SearchBar.kt
            SearchBarComposable(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholderText = "Buscar ejercicios..."
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (showForm) {
                    item {
                        NoteInputForm(
                            hasNotificationPermission = hasNotificationPermission,
                            onRequestPermission = {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            },
                            onCancel = { showForm = false },
                            onSave = { header, text, notificationDateTime ->
                                saveNote(dao, header, text, notificationDateTime, context) {
                                    showForm = false
                                    refreshNotes()
                                }
                            }
                        )
                    }
                }
                // si hay notas vacias
                if (groupedNotes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (allNotes.isEmpty()) "No hay notas disponibles"
                                else "No se encontraron notas",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // cartas de notas
                    groupedNotes.forEach { (monthYear, notes) ->
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

                        items(notes) { note ->
                            NoteCard(note = note, onDelete = { noteToDelete = it })
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = { showForm = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Nueva Nota")
            }
        }
    }
//elimianr nota
    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Eliminar esta nota?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        MainScope().launch {
                            NotificationCreator.cancelNotification(context, note.id)
                            Service.deleteNote(note)
                            refreshNotes()
                            noteToDelete = null
                        }
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}
//carta de nota
@Composable
fun NoteCard(note: NoteEntity, onDelete: (NoteEntity) -> Unit) {
    val hasNotification = note.timestamp.startsWith("NOTIFICATION:")
    val displayTime = formatGlobalTimestamp(note.timestamp)

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = note.header,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (hasNotification) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = displayTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = note.text,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = { onDelete(note) }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Formulario para crea runa nueva ota
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteInputForm(
    hasNotificationPermission: Boolean,
    onRequestPermission: () -> Unit,
    onCancel: () -> Unit,
    onSave: (String, String, LocalDateTime?) -> Unit
) {
    var header by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var enableNotification by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf("12:00") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(12, 0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nueva Nota", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = header,
                onValueChange = { header = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (enableNotification) Icons.Default.Notifications
                        else Icons.Default.NotificationsOff,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Programar recordatorio")
                }
                Switch(
                    checked = enableNotification,
                    onCheckedChange = {
                        if (it && !hasNotificationPermission) onRequestPermission()
                        else enableNotification = it
                    },
                    enabled = hasNotificationPermission
                )
            }

            if (enableNotification && hasNotificationPermission) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                ?: "Fecha"
                        )
                    }
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(selectedTime)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                FilledTonalButton(
                    onClick = {
                        val notificationDateTime = if (enableNotification && selectedDate != null) {
                            val timeParts = selectedTime.split(":")
                            LocalDateTime.of(
                                selectedDate,
                                java.time.LocalTime.of(timeParts[0].toInt(), timeParts[1].toInt())
                            )
                        } else null
                        onSave(header, content, notificationDateTime)
                    },
                    enabled = header.isNotEmpty() && content.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
//selcionador de fecha
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(datePickerState)
        }
    }
//selcionador de timpo
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Seleccionar hora") },
            text = { TimePicker(timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        showTimePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        )
    }
}
// funcion que guarda una nota

private fun saveNote(
    dao: TrackFitDao,
    header: String,
    text: String,
    notificationDateTime: LocalDateTime?,
    context: Context,
    onComplete: () -> Unit
) {
    val timestampString = if (notificationDateTime != null) {
        "NOTIFICATION:${notificationDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}"
    } else {
        LocalDate.now().format(DateTimeFormatter.ISO_DATE)
    }

    val note = NoteEntity(header = header, text = text, timestamp = timestampString)

    MainScope().launch {
        Service.insertNoteToApi(note)
        if (notificationDateTime != null) {
            val insertedNote = dao.getNotes().find {
                it.header == header && it.text == text && it.timestamp == timestampString
            }
            insertedNote?.let {
                NotificationCreator.scheduleNotification(
                    context = context,
                    notificationId = it.id,
                    title = it.header,
                    content = it.text,
                    dateTime = notificationDateTime,
                    extraData = mapOf(
                        "note_id" to it.id.toString(),
                        "screen" to "notes"
                    )
                )
            }
        }
        onComplete()
    }
}