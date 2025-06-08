package com.example.fittrack.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.fittrack.MainActivity
import com.example.fittrack.entity.TargetLocationEntity
import com.example.fittrack.entity.UserEntity
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.*



@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapPage(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val dao = MainActivity.database.trackFitDao()

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var achievedLocation by remember { mutableStateOf<TargetLocationEntity?>(null) }
    var targetLocations by remember { mutableStateOf<List<TargetLocationEntity>>(emptyList()) }
    var user by remember { mutableStateOf<UserEntity?>(null) }

    LaunchedEffect(Unit) {
        targetLocations = dao.getTargetLocations()
        user = dao.getUser()
    }

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.4220541, -122.0853242), 12f) // Centrado en Googleplex
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    fun checkTargetLocationReached(currentLocation: LatLng) {
        targetLocations.forEach { target ->
            val distance = calculateDistance(
                currentLocation.latitude, currentLocation.longitude,
                target.position.latitude, target.position.longitude
            )

            if (distance <= target.radiusMeters) {
                achievedLocation = target
                showSuccessDialog = true
                return
            }
        }
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

                    checkTargetLocationReached(newLocation)

                    scope.launch {
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(newLocation, 15f)
                    }
                }
            }
        }
    }

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            getCurrentLocation()
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            getCurrentLocation()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionState.status.isGranted,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true,
                compassEnabled = true,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                rotationGesturesEnabled = true,
                tiltGesturesEnabled = true
            )
        ) {
            userLocation?.let { loc ->
                Marker(
                    state = MarkerState(position = loc),
                    title = "Tu ubicación",
                    snippet = "Estás aquí"
                )
            }

            targetLocations.forEach { target ->
                Marker(
                    state = MarkerState(position = target.position),
                    title = target.name,
                    snippet = "Ubicación objetivo"
                )

                Circle(
                    center = target.position,
                    radius = target.radiusMeters,
                    fillColor = Color.Blue.copy(alpha = 0.2f),
                    strokeColor = Color.Blue,
                    strokeWidth = 2f
                )
            }
        }

        if (!locationPermissionState.status.isGranted && !locationPermissionState.status.shouldShowRationale) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                Text("Se requiere permiso de ubicación para mostrar tu posición en el mapa")
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp, bottom = 100.dp), // Margen para evitar el NavBar
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val currentZoom = cameraPositionState.position.zoom
                        cameraPositionState.animate(
                            CameraUpdateFactory.zoomTo(currentZoom + 1f),
                            durationMs = 300
                        )
                    }
                },
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Acercar"
                )
            }

            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val currentZoom = cameraPositionState.position.zoom
                        cameraPositionState.animate(
                            CameraUpdateFactory.zoomTo(currentZoom - 1f),
                            durationMs = 300
                        )
                    }
                },
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Alejar"
                )
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            NavBar(navController = navController)
        }
    }

    if (showSuccessDialog && achievedLocation != null) {
        user?.let { it ->
            val today = LocalDate.now()
            val lastStreakDate = it.lastStreakDay.takeIf { it.isNotBlank() }?.let { dateStr ->
                LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
            } ?: LocalDate.MIN

            if (lastStreakDate != null && lastStreakDate != today) {
                it.streakDays = (it.streakDays ?: 0) + 1
                it.lastStreakDay = today.toString()

                LaunchedEffect(Unit) {
                    Service.updateUserApi(it)
                }
            }


        Dialog(
            onDismissRequest = {
                showSuccessDialog = false
                achievedLocation = null
            }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = Color.Green,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¡Ubicación Alcanzada!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Has llegado a:",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = achievedLocation!!.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            showSuccessDialog = false
                            achievedLocation = null
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green
                        )
                    ) {
                        Text(
                            text = "¡Has aumentado tu racha! ${user?.streakDays ?: 0} días",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )


                    }
                }
            }
        }
    }}}
