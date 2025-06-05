package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fittrack.ui.ui_elements.NavBar
import kotlin.math.pow

@Composable
fun IMCScreen(navController: NavHostController) {
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
            var genero by remember { mutableStateOf("") }
            var altura by remember { mutableStateOf(TextFieldValue("")) }
            var peso by remember { mutableStateOf(TextFieldValue("")) }
            var mostrarResultado by remember { mutableStateOf(false) }
            val imc = remember {
                derivedStateOf {
            if (altura.text.isNotBlank() && peso.text.isNotBlank()) {
                (peso.text.toDouble() / altura.text.toDouble().pow(2)) *
                        if (genero == "Hombre") 1.0 else 0.95
                        } else 0.0

                }
            }

            val (clasificacion, recomendacion) = remember(imc.value, genero) {
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
                text = "Calculadora de IMC",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text("Selecciona tu género:", style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilterChip(
                    selected = genero == "Hombre",
                    onClick = { genero = "Hombre" },
                    label = { Text("Hombre") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = genero == "Mujer",
                    onClick = { genero = "Mujer" },
                    label = { Text("Mujer") },
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = altura,
                onValueChange = { altura = it },
                label = { Text("Altura (m)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso (kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = { mostrarResultado = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = genero.isNotEmpty() && altura.text.isNotBlank() && peso.text.isNotBlank()
            ) {
                Text("Calcular IMC")
            }

            if (mostrarResultado && imc.value > 0) {
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
                        Text("Tu IMC: ${"%.1f".format(imc.value)}",
                            style = MaterialTheme.typography.titleLarge)
                        Text("Clasificación: $clasificacion")

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Text("Recomendaciones:",
                            style = MaterialTheme.typography.titleMedium)
                        Text(recomendacion)
                    }
                }
            }
        }
    }
}
