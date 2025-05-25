package com.example.fittrack.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.ui.ui_elements.NavBar
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapPage(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.4168, -3.7038), 10f)
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
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionState.status.isGranted,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
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

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            NavBar(navController = navController)
        }
    }
}