package com.example.fittrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

//Ejecicio examen IMC
@Composable
fun UserDataScreen(navController: NavController) {

    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var bmi by remember { mutableDoubleStateOf(0.0) }
    var result by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Usuario",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { gender = "Hombre" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (gender == "Hombre") Color(0xFF1976D2) else Color.LightGray,
                    contentColor = if (gender == "Hombre") Color.White else Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Hombre")
            }

            Button(
                onClick = { gender = "Mujer" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (gender == "Mujer") Color(0xFFD81B60) else Color.LightGray,
                    contentColor = if (gender == "Mujer") Color.White else Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text("Mujer")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = height,
            onValueChange = { input ->
                if (input.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$"))) {
                    height = input
                }
            },
            label = { Text("Altura en metros (Ej: 1.75)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { input ->
                if (input.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$"))) {
                    weight = input
                }
            },
            label = { Text("Peso en kg (Ej: 70)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { input ->
                if (input.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$"))) {
                    weight = input
                }
            },
            label = { Text("Nombre en fitness") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { input ->
                if (input.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$"))) {
                    weight = input
                }
            },
            label = { Text("Objetivo fitness") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))



        Button(
            onClick = {
                val h = height.toDoubleOrNull() ?: 0.0
                val w = weight.toDoubleOrNull() ?: 0.0
                val coef = if (gender.lowercase() == "hombre") 1.0 else 0.95

                if (h > 0 && w > 0 && gender.isNotEmpty()) {
                    bmi = (w / (h * h)) * coef
                    result = when {
                        bmi < 18.5 -> "Bajo peso"
                        bmi in 18.5..24.9 -> "Peso normal"
                        bmi in 25.0..29.9 -> "Sobrepeso"
                        else -> "Obesidad"
                    }
                } else {
                    result = "Por favor, completa todos los campos correctamente."
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Calcular IMC")
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (bmi > 0.0) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Tu IMC es: ${"%.2f".format(bmi)}\nEstado: $result",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
