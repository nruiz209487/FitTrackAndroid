package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fittrack.MainActivity
import com.example.fittrack.entity.TargetLocationEntity
import com.example.fittrack.ui.ui_elements.NavBar
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@Composable
fun CreateNewTargetLocation(navController: NavHostController) {
    val dao = MainActivity.database.trackFitDao()
    val coroutineScope = rememberCoroutineScope()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

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
            var name by remember { mutableStateOf(TextFieldValue("")) }
            var latitude by remember { mutableStateOf(TextFieldValue("")) }
            var longitude by remember { mutableStateOf(TextFieldValue("")) }
            var radius by remember { mutableStateOf(TextFieldValue("200.0")) }
            var showValidationError by remember { mutableStateOf(false) }
            var isLoading by remember { mutableStateOf(false) }

            // Estados para errores específicos
            var latitudeError by remember { mutableStateOf<String?>(null) }
            var longitudeError by remember { mutableStateOf<String?>(null) }
            var radiusError by remember { mutableStateOf<String?>(null) }

            fun validateCoordinates(): Boolean {
                var isValid = true

                // Validación de latitud (-90 a 90)
                latitude.text.toDoubleOrNull()?.let { lat ->
                    if (lat < -90 || lat > 90) {
                        latitudeError = "Latitud debe estar entre -90 y 90"
                        isValid = false
                    } else {
                        latitudeError = null
                    }
                } ?: run {
                    latitudeError = "Latitud inválida"
                    isValid = false
                }

                // Validación de longitud (-180 a 180)
                longitude.text.toDoubleOrNull()?.let { lng ->
                    if (lng < -180 || lng > 180) {
                        longitudeError = "Longitud debe estar entre -180 y 180"
                        isValid = false
                    } else {
                        longitudeError = null
                    }
                } ?: run {
                    longitudeError = "Longitud inválida"
                    isValid = false
                }

                // Validación de radio (positivo y razonable)
                radius.text.toDoubleOrNull()?.let { r ->
                    when {
                        r <= 0 -> {
                            radiusError = "Radio debe ser mayor que 0"
                            isValid = false
                        }
                        r > 10000 -> {
                            radiusError = "Radio demasiado grande (máx. 10km)"
                            isValid = false
                        }
                        else -> radiusError = null
                    }
                } ?: run {
                    radiusError = "Radio inválido"
                    isValid = false
                }

                return isValid
            }

            Text(
                text = "Nueva Ubicación Objetivo",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la ubicación") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showValidationError && name.text.isBlank(),
                supportingText = {
                    if (showValidationError && name.text.isBlank()) {
                        Text("Nombre requerido")
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitud") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = latitudeError != null,
                    supportingText = {
                        latitudeError?.let { Text(it) }
                    }
                )

                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitud") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = longitudeError != null,
                    supportingText = {
                        longitudeError?.let { Text(it) }
                    }
                )
            }

            OutlinedTextField(
                value = radius,
                onValueChange = { radius = it },
                label = { Text("Radio (metros)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = radiusError != null,
                supportingText = {
                    radiusError?.let { Text(it) }
                }
            )

            if (showValidationError && name.text.isBlank()) {
                Text(
                    text = "Por favor, completa todos los campos correctamente",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Button(
                onClick = {
                    // Resetear errores previos
                    showValidationError = false
                    latitudeError = null
                    longitudeError = null
                    radiusError = null

                    if (name.text.isBlank()) {
                        showValidationError = true
                    }

                    if (validateCoordinates()) {
                        try {
                            isLoading = true

                            val newLocation = TargetLocationEntity(
                                id = 0,
                                name = name.text,
                                position = LatLng(
                                    latitude.text.toDouble(),
                                    longitude.text.toDouble()
                                ),
                                radiusMeters = radius.text.toDouble()
                            )

                            coroutineScope.launch {
                                try {
                                    dao.insertTargetLocations(listOf(newLocation))
                                    showSuccessDialog = true
                                } catch (e: Exception) {
                                    errorMessage = "Error al guardar: ${e.localizedMessage}"
                                    showErrorDialog = true
                                } finally {
                                    isLoading = false
                                }
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error inesperado: ${e.localizedMessage}"
                            showErrorDialog = true
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar Ubicación")
                }
            }
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                navController.popBackStack()
            },
            title = { Text("Ubicación guardada") },
            text = { Text("La ubicación se ha guardado correctamente") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}