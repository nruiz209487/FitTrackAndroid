package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fittrack.MainActivity
import com.example.fittrack.entity.UserEntity
import com.example.fittrack.ui.ui_elements.NavBar
import com.example.fittrack.ui.helpers.RoutineGenerator
import kotlinx.coroutines.launch
import kotlin.math.pow

@Composable
fun IMCScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    val dao = MainActivity.database.trackFitDao()
    var user by remember { mutableStateOf<UserEntity?>(null) }
    val genderOptions = listOf("Masculino", "Femenino", "Otro")
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf(TextFieldValue("")) }
    var weight by remember { mutableStateOf(TextFieldValue("")) }
    var showResults by remember { mutableStateOf(false) }
    var generatedRoutines by remember { mutableStateOf(false) }
    var generateRoutines by remember { mutableStateOf(false) }
    var popUpRoutines by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        user = dao.getUser()
    }

    LaunchedEffect(user) {
        user?.let {
            gender = it.gender ?: ""
            height = TextFieldValue(it.height?.toString() ?: "")
            weight = TextFieldValue(it.weight?.toString() ?: "")
        }
    }

    if (popUpRoutines) {
        AlertDialog(
            onDismissRequest = { popUpRoutines = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "¡Rutinas Generadas!",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Text(
                        text = "Tus rutinas personalizadas han sido creadas exitosamente.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ve a la sección de rutinas para ver tu plan semanal personalizado.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { popUpRoutines = false }
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    Scaffold(
        bottomBar = { NavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val imc = remember {
                derivedStateOf {
                    if (height.text.isNotBlank() && weight.text.isNotBlank()) {
                        (weight.text.toDouble() / height.text.toDouble().pow(2)) *
                                if (gender == "Masculino") 1.0 else 0.95
                    } else 0.0
                }
            }

            val (clasificacion, recomendacion) = remember(imc.value, gender) {
                when {
                    imc.value == 0.0 -> Pair("", "")
                    imc.value < 18.5 -> Pair(
                        "Peso bajo",
                        "Rutina para ganar masa muscular:\n" +
                                "• 4 días de fuerza\n" +
                                "• Ejercicios compuestos\n" +
                                "• Superávit calórico"
                    )

                    imc.value < 25 -> Pair(
                        "Peso normal",
                        "Rutina de mantenimiento:\n" +
                                "• 3 días fuerza\n" +
                                "• 2 días cardio\n" +
                                "• Dieta equilibrada"
                    )

                    imc.value < 30 -> Pair(
                        "Sobrepeso",
                        "Rutina para pérdida de grasa:\n" +
                                "• HIIT 3x/semana\n" +
                                "• Entrenamiento de fuerza\n" +
                                "• Déficit calórico"
                    )

                    else -> Pair(
                        "Obesidad",
                        "Rutina inicial:\n" +
                                "• Cardio de bajo impacto\n" +
                                "• Entrenamiento con máquinas\n" +
                                "• Consulta nutricional"
                    )
                }
            }

            Text(
                text = "Crea una rutina personalizada",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text("Selecciona tu género (opcional):", style = MaterialTheme.typography.bodyLarge)


            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                genderOptions.forEach { option ->
                    FilterChip(
                        selected = gender == option,
                        onClick = {
                            gender = if (gender == option) {
                                ""
                            } else {
                                option
                            }
                        },
                        label = { Text(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (gender.isEmpty()) {
                Text(
                    text = "Género: No especificado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Género seleccionado: $gender",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Altura (m)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Ej: 1.75") }
            )

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Peso (kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Ej: 68.5") }
            )

            Button(
                onClick = {
                    showResults = true
                    coroutineScope.launch {
                        user?.let {
                            val updatedUser = it.copy(
                                gender = gender.ifEmpty { null },
                                height = height.text.toDoubleOrNull(),
                                weight = weight.text.toDoubleOrNull()
                            )
                            dao.updateUser(updatedUser)
                        }

                        if (imc.value > 0 && !generatedRoutines) {
                            generateRoutines = true
                            try {
                                val userId = user?.id ?: 1
                                RoutineGenerator.generateAndSaveRoutines(imc.value, userId)
                                generatedRoutines = true
                                popUpRoutines = true
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                generateRoutines = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = height.text.isNotBlank() && weight.text.isNotBlank()
            ) {
                if (generateRoutines) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (generateRoutines) "Generando rutinas..." else "Calcular IMC")
            }

            if (showResults && imc.value > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Tu IMC: ${"%.1f".format(imc.value)}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text("Clasificación: $clasificacion")

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            "Recomendaciones:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(recomendacion)
                    }
                }
            }
        }
    }
}