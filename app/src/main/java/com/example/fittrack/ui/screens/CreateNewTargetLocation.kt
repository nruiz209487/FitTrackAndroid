package com.example.fittrack.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fittrack.entity.TargetLocationEntity
import com.example.fittrack.service.Service
import com.example.fittrack.ui.ui_elements.NavBar
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun CreateNewTargetLocation(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Estados para el mapa y ubicación
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var showMapSelector by remember { mutableStateOf(false) }

    // Estados para los campos del formulario
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var radius by remember { mutableStateOf(TextFieldValue("200.0")) }

    // Estados para errores
    var radiusError by remember { mutableStateOf<String?>(null) }
    var showValidationError by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.4220541, -122.0853242), 12f)
    }

    fun getCurrentLocation() {
        if (locationPermissionState.status.isGranted) {
            val cancellationToken = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).addOnSuccessListener { location ->
                location?.let {
                    val newLocation = LatLng(it.latitude, it.longitude)
                    userLocation = newLocation

                    coroutineScope.launch {
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(newLocation, 15f)
                    }
                }
            }
        }
    }

    fun validateRadius(): Boolean {
        radius.text.toDoubleOrNull()?.let { r ->
            when {
                r <= 0 -> {
                    radiusError = "Radio debe ser mayor que 0"
                    return false
                }
                r > 10000 -> {
                    radiusError = "Radio demasiado grande (máx. 10km)"
                    return false
                }
                else -> {
                    radiusError = null
                    return true
                }
            }
        } ?: run {
            radiusError = "Radio inválido"
            return false
        }
    }

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            getCurrentLocation()
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    if (showMapSelector) {
        // Pantalla de selección en el mapa
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = locationPermissionState.status.isGranted,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    compassEnabled = true,
                    zoomGesturesEnabled = true,
                    scrollGesturesEnabled = true,
                    rotationGesturesEnabled = true,
                    tiltGesturesEnabled = true
                ),
                onMapClick = { latLng ->
                    selectedLocation = latLng
                }
            ) {
                // Mostrar ubicación del usuario
                userLocation?.let { loc ->
                    Marker(
                        state = MarkerState(position = loc),
                        title = "Tu ubicación",
                        snippet = "Estás aquí"
                    )
                }

                // Mostrar ubicación seleccionada
                selectedLocation?.let { loc ->
                    Marker(
                        state = MarkerState(position = loc),
                        title = "Ubicación seleccionada",
                        snippet = "Toca 'Confirmar' para usar esta ubicación"
                    )

                    // Mostrar círculo de radio si hay una ubicación seleccionada
                    radius.text.toDoubleOrNull()?.let { radiusValue ->
                        if (radiusValue > 0 && radiusValue <= 10000) {
                            Circle(
                                center = loc,
                                radius = radiusValue,
                                fillColor = Color.Blue.copy(alpha = 0.2f),
                                strokeColor = Color.Blue,
                                strokeWidth = 2f
                            )
                        }
                    }
                }
            }

            // Controles de zoom
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val currentZoom = cameraPositionState.position.zoom
                            cameraPositionState.animate(
                                CameraUpdateFactory.zoomTo(currentZoom + 1f),
                                durationMs = 300
                            )
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Acercar"
                    )
                }

                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val currentZoom = cameraPositionState.position.zoom
                            cameraPositionState.animate(
                                CameraUpdateFactory.zoomTo(currentZoom - 1f),
                                durationMs = 300
                            )
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Alejar"
                    )
                }

                FloatingActionButton(
                    onClick = { getCurrentLocation() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Mi ubicación"
                    )
                }
            }

            // Panel inferior con información y botones
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Selecciona una ubicación en el mapa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    selectedLocation?.let { loc ->
                        Text(
                            text = "Coordenadas: ${String.format(Locale.US, "%.6f", loc.latitude)}, ${String.format(Locale.US, "%.6f", loc.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showMapSelector = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = { showMapSelector = false },
                            modifier = Modifier.weight(1f),
                            enabled = selectedLocation != null
                        ) {
                            Text("Confirmar")
                        }
                    }
                }
            }
        }
    } else {
        // Pantalla principal del formulario
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

                // Card para mostrar la ubicación seleccionada
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedLocation != null)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Ubicación",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (selectedLocation != null) {
                            Text(
                                text = "Coordenadas: ${String.format(Locale.US, "%.6f", selectedLocation!!.latitude)}, ${String.format(Locale.US, "%.6f", selectedLocation!!.longitude)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Coordenadas: ${String.format(Locale.US, "%.6f", selectedLocation!!.latitude)}, ${String.format(Locale.US, "%.6f", selectedLocation!!.longitude)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            Text(
                                text = "No se ha seleccionado ninguna ubicación",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { showMapSelector = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (selectedLocation != null) "Cambiar ubicación" else "Seleccionar en mapa")
                        }
                    }
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

                if (showValidationError && (name.text.isBlank() || selectedLocation == null)) {
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
                        radiusError = null

                        val nameValid = name.text.isNotBlank()
                        val locationValid = selectedLocation != null
                        val radiusValid = validateRadius()

                        if (!nameValid || !locationValid) {
                            showValidationError = true
                        }

                        if (nameValid && locationValid && radiusValid) {
                            try {
                                isLoading = true

                                val newLocation = TargetLocationEntity(
                                    id = 0,
                                    name = name.text,
                                    position = selectedLocation!!,
                                    radiusMeters = radius.text.toDouble()
                                )

                                coroutineScope.launch {
                                    try {
                                        Service.insertTargetLocationsToApi(newLocation)
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