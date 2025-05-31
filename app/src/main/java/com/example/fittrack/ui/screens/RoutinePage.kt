package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fittrack.MainActivity
import com.example.fittrack.entity.*
import com.example.trackfit.database.TrackFitDao
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RoutinePage(
    navController: NavController, routineId: Int
) {
    var routine by remember { mutableStateOf<RoutineEntity?>(null) }
    var exercises by remember { mutableStateOf(emptyList<ExerciseEntity>()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var showLogInputs by remember { mutableStateOf(false) }
    var weightText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }

    val dao = MainActivity.database.trackFitDao()

    LaunchedEffect(Unit) {
        routine = dao.getRoutines().find { it.id == routineId }?.also { foundRoutine ->
            val ids = foundRoutine.exerciseIds.split(",").mapNotNull { it.toIntOrNull() }
            exercises = dao.getExercisesByIds(ids)
        }
    }


    LaunchedEffect(showSavedMessage) {
        if (showSavedMessage) {
            delay(2000)
            showSavedMessage = false
        }
    }

    if (exercises.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text("No hay ejercicios para esta rutina.")
        }
        return
    }

    val current = exercises[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = routine?.name ?: "", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        ExerciseCard(current)

        Spacer(Modifier.height(16.dp))

        if (showLogInputs) {
            LogInputCard(
                current = current,
                weightText = weightText,
                onWeightChange = {
                    weightText = it.filter { char -> char.isDigit() || char == '.' }
                },
                repsText = repsText,
                onRepsChange = { repsText = it.filter { char -> char.isDigit() } },
                onCancel = {
                    showLogInputs = false
                    weightText = ""
                    repsText = ""
                },
                onSave = {
                    saveExerciseLog(
                        dao = dao,
                        exerciseId = current.id,
                        weight = weightText,
                        reps = repsText,
                        onStart = { isSaving = true },
                        onComplete = {
                            isSaving = false
                            showLogInputs = false
                            weightText = ""
                            repsText = ""
                            showSavedMessage = true
                        })
                },
                isSaving = isSaving
            )
        }

        if (showSavedMessage) {
            SavedNoteCard()
        }

        Spacer(Modifier.height(16.dp))

        NavigationButtons(
            currentIndex = currentIndex,
            maxIndex = exercises.lastIndex,
            onPrev = { if (currentIndex > 0) currentIndex-- },
            onNext = { if (currentIndex < exercises.lastIndex) currentIndex++ },
            onLogClick = { showLogInputs = true },
            showLogButton = !showLogInputs
        )

        Spacer(Modifier.weight(1f))

        if (!showLogInputs) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Volver")
            }
        }
    }
}

@Composable
private fun ExerciseCard(exercise: ExerciseEntity) {
    Card(
        shape = RoundedCornerShape(12.dp), modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(exercise.imageUri)
                    .crossfade(true).build(),
                contentDescription = exercise.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(Modifier.height(8.dp))

            Text(exercise.name, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(4.dp))

            Text(exercise.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun LogInputCard(
    current: ExerciseEntity,
    weightText: String,
    onWeightChange: (String) -> Unit,
    repsText: String,
    onRepsChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Registrar ${current.name}", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = weightText,
                onValueChange = onWeightChange,
                label = { Text("Peso (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = repsText,
                onValueChange = onRepsChange,
                label = { Text("Repeticiones") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onCancel) {
                    Text("Cancelar")
                }
                Button(
                    onClick = onSave,
                    enabled = weightText.isNotEmpty() && repsText.isNotEmpty() && !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedNoteCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "¡Registro guardado correctamente!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun NavigationButtons(
    currentIndex: Int,
    maxIndex: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onLogClick: () -> Unit,
    showLogButton: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = onPrev, enabled = currentIndex > 0) {
            Text("Anterior")
        }

        if (showLogButton) {
            Button(onClick = onLogClick) {
                Text("Registro")
            }
        }

        Button(onClick = onNext, enabled = currentIndex < maxIndex) {
            Text("Siguiente")
        }
    }
}

private fun saveExerciseLog(
    dao: TrackFitDao,
    exerciseId: Int,
    weight: String,
    reps: String,
    onStart: () -> Unit,
    onComplete: () -> Unit
) {
    onStart()

    val log = ExerciseLogEntity(
        exerciseId = exerciseId,
        date = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
        weight = weight.toFloatOrNull() ?: 0f,
        reps = reps.toIntOrNull() ?: 0
    )

    MainScope().launch {
        dao.insertExerciseLog(log)
        onComplete()
    }
}
