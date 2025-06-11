package com.example.fittrack.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fittrack.MainActivity
import com.example.fittrack.entity.ExerciseEntity
import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.service.Service
import com.example.fittrack.ui.ui_elements.NavBar
import com.example.fittrack.ui.ui_elements.SearchBarComposable
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Pagina para crear nuvas rutinas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutinePage(navController: NavController) {
    var exercises by remember { mutableStateOf<List<ExerciseEntity>>(emptyList()) }
    var selectedExercises by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var routineName by remember { mutableStateOf("") }
    var routineDescription by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val dao = MainActivity.database.trackFitDao()

    LaunchedEffect(Unit) {
        exercises = dao.getExercises()
    }
    // para el buscador
    val filteredExercises = exercises.filter { exercise ->
        exercise.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Rutina") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("home") {
                            popUpTo("create_routine") { inclusive = true }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { showDialog = true },
                        enabled = selectedExercises.isNotEmpty() && routineName.isNotBlank()
                    ) {
                        Text("Guardar")
                    }
                }
            )
        },
        bottomBar = { NavBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(2.dp)
        ) {
            OutlinedTextField(
                value = routineName,
                onValueChange = { routineName = it },
                label = { Text("Nombre de la rutina") },
                placeholder = { Text("Ej: Rutina de pecho") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = routineDescription,
                onValueChange = { routineDescription = it },
                label = { Text("Descripción de la rutina") },
                placeholder = { Text("Ej: Rutina lunes") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // este es el Searchbar.kt
            SearchBarComposable(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholderText = "Buscar ejercicios..."
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Selecciona ejercicios")
                Text("${selectedExercises.size} seleccionados")
            }

            Spacer(modifier = Modifier.height(8.dp))
            // en caso de que no haya ejercicios
            if (exercises.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay ejercicios disponibles", textAlign = TextAlign.Center)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredExercises) { exercise ->
                        ExerciseSelectionCard(
                            exercise = exercise,
                            isSelected = selectedExercises.contains(exercise.id),
                            onSelectionChange = { isSelected ->
                                selectedExercises = if (isSelected) {
                                    selectedExercises + exercise.id
                                } else {
                                    selectedExercises - exercise.id
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    //comfirmacion
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar rutina") },
            text = {
                Column {
                    Text("¿Deseas crear la rutina '$routineName' con ${selectedExercises.size} ejercicios?")
                    Spacer(modifier = Modifier.height(8.dp))
                    exercises.filter { selectedExercises.contains(it.id) }
                        .forEach { Text("• ${it.name}", modifier = Modifier.padding(start = 8.dp)) }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        saveRoutine(
                            routineName = routineName,
                            routineDescription = routineDescription,
                            selectedExerciseIds = selectedExercises.toList(),
                            allExercises = exercises,
                        ) {
                            navController.navigate("home") {
                                popUpTo("create_routine") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Composable para mostar cada ejercicio y selecionarlo se muestarn el nombre y la imagen
 */
@Composable
fun ExerciseSelectionCard(
    exercise: ExerciseEntity,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectionChange(!isSelected) },
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Box {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(exercise.imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = exercise.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
                Text(
                    text = exercise.name,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Seleccionado",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }
        }
    }
}

/**
 * Guarda la rutina
 */
private fun saveRoutine(
    routineName: String,
    routineDescription: String,
    selectedExerciseIds: List<Int>,
    allExercises: List<ExerciseEntity>,
    onComplete: () -> Unit
) {
    val firstExerciseImage = allExercises
        .firstOrNull { selectedExerciseIds.contains(it.id) }
        ?.imageUri ?: ""

    val routine = RoutineEntity(
        name = routineName,
        description = routineDescription,
        exerciseIds = selectedExerciseIds.joinToString(","),
        imageUri = firstExerciseImage
    )

    MainScope().launch {
        try {
            Service.insertRoutineToApi(routine) // solcitud a service
        } finally {
            onComplete()
        }
    }
}
