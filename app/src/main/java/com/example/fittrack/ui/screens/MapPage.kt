package com.example.fittrack.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.fittrack.ui.ui_elements.NavBar
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

//Estoy viendo como hacvcerloi
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapPage(navController: NavController) {
    val context = LocalContext.current

    // Cliente de ubicación
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Estado de ubicación del usuario
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    // Estado del permiso de ubicación
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Solicitar permiso y obtener última ubicación
    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let { userLocation = LatLng(it.latitude, it.longitude) }
            }
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Posición de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = userLocation
            ?.let { CameraPosition.fromLatLngZoom(it, 15f) }
            ?: CameraPosition.fromLatLngZoom(LatLng(40.4168, -3.7038), 10f)
    }

    // Mapa con marcador y “punto azul”
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = locationPermissionState.status.isGranted
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
    NavBar(navController = navController)
}
