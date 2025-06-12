package com.example.fittrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.fittrack.ui.ui_elements.NavBar
import com.example.fittrack.ui.ui_elements.SearchBarComposable

/**
 *Clase para poder contar los logs relacionados lo hago aqui ya que a la api no  seirve de nada y son menos marrones en el cambio de datos
 */
data class ExerciseWithLogCount(
    val exercise: ExerciseEntity,
    val logCount: Int
)

/**
 * Simplemente una lisat de los ejercicios apra acceder a sus logs
 */
@Composable
fun ExerciseListPage(
    navController: NavController
) {
    var exercisesWithLogs by remember { mutableStateOf<List<ExerciseWithLogCount>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val dao = MainActivity.database.trackFitDao()

    LaunchedEffect(Unit) {
        val exercises = dao.getExercises()
        //covierto los ejercicios a ejercicios con log counts
        val exercisesWithLogCount = exercises.map { exercise ->
            val logCount = dao.getExerciseLogsById(exercise.id).size
            ExerciseWithLogCount(exercise, logCount)
        }
        //basicamente orderno los ejercicios dependiendo del la cantidad de logs y despues por nombre
        exercisesWithLogs = exercisesWithLogCount.sortedWith(
            compareByDescending<ExerciseWithLogCount> { it.logCount }
                .thenBy { it.exercise.name }
        )
    }
    // filtro de busqueda
    val filteredExercises = exercisesWithLogs.filter { exerciseWithLog ->
        exerciseWithLog.exercise.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        bottomBar = { NavBar(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(2.dp)
        ) {
            if (exercisesWithLogs.isEmpty()) {
                Text(
                    text = "No hay ejercicios disponibles.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {

                    Spacer(Modifier.height(16.dp))
                    // El searchbar.kt
                    SearchBarComposable(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholderText = "Buscar ejercicio..."
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredExercises) { exerciseWithLog ->
                            val exercise = exerciseWithLog.exercise
                            val logCount = exerciseWithLog.logCount

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("exercise_logs/${exercise.id}") //navegacion paso como paremtro el id del ejercicio
                                    },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box {//imagen dek ejeciccio
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(exercise.imageUri).crossfade(true).build(),
                                            contentDescription = exercise.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(120.dp)
                                        )

                                        if (logCount > 0) {
                                            Card(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(8.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    text = logCount.toString(),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    modifier = Modifier.padding(
                                                        horizontal = 6.dp,
                                                        vertical = 2.dp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = exercise.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            textAlign = TextAlign.Center
                                        )
                                        //si hay logs muestra la cantidad de logs
                                        if (logCount > 0) {
                                            Text(
                                                text = "$logCount registro${if (logCount != 1) "s" else ""}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center
                                            )
                                        } else {
                                            Text(
                                                text = "Sin registros",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.7f
                                                ),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
