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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.ui.ui_elements.NavBar
import com.example.trackfit.database.TrackFitDao
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
@Composable
fun NotesScreen(navController: NavController, dao: TrackFitDao) {
    var posts by remember { mutableStateOf<List<NoteEntity>>(emptyList()) }
    var showForm by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    fun refreshNotes() {
        MainScope().launch {
            posts = dao.getNotes()
        }
    }

    LaunchedEffect(Unit) {
        refreshNotes()
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (showForm) {
                    item {
                        NoteInputForm(
                            onCancel = { showForm = false },
                            onSave = { header, text ->
                                isSaving = true
                                saveNote(
                                    dao = dao,
                                    header = header,
                                    text = text,
                                    onStart = {},
                                    onComplete = {
                                        isSaving = false
                                        showForm = false
                                        refreshNotes()
                                    })
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                items(posts) { post ->
                    NoteCard(post = post, onDelete = { noteToDelete ->
                        isDeleting = true
                        MainScope().launch {
                            dao.deleteNote(noteToDelete)
                            refreshNotes()
                            isDeleting = false
                        }
                    })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FilledTonalButton(
                onClick = { showForm = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving && !isDeleting
            ) {
                Text("Crear Nueva Nota")
            }
        }
    }
}

@Composable
fun NoteCard(post: NoteEntity, onDelete: (NoteEntity) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                    Text(
                        text = post.header,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = post.timestamp,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            letterSpacing = 0.3.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = post.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = { onDelete(post) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar Nota",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


@Composable
fun NoteInputForm(
    onCancel: () -> Unit, onSave: (String, String) -> Unit
) {
    var headerText by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear Nueva Nota", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = headerText,
                onValueChange = { headerText = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = contentText,
                onValueChange = { contentText = it },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onCancel, shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
                FilledTonalButton(
                    onClick = { onSave(headerText, contentText) },
                    enabled = headerText.isNotEmpty() && contentText.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

private fun saveNote(
    dao: TrackFitDao, header: String, text: String, onStart: () -> Unit, onComplete: () -> Unit
) {
    onStart()
    val note = NoteEntity(
        header = header, text = text, timestamp = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
    )
    MainScope().launch {
        dao.insertNote(note)
        onComplete()
    }
}
