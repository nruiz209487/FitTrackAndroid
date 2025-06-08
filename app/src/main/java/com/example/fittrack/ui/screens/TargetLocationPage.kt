package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.MainActivity
import com.example.fittrack.entity.TargetLocationEntity
import com.example.fittrack.service.Service
import com.example.fittrack.ui.ui_elements.NavBar
import kotlinx.coroutines.launch

@Composable
fun TargetLocationsScreen(navController: NavController) {
    val dao = MainActivity.database.trackFitDao()
    val coroutineScope = rememberCoroutineScope()

    var locations by remember { mutableStateOf<List<TargetLocationEntity>>(emptyList()) }
    var locationToDelete by remember { mutableStateOf<TargetLocationEntity?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun refreshLocations() {
        coroutineScope.launch {
            isLoading = true
            locations = dao.getTargetLocations()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshLocations()
    }

    Scaffold(
        bottomBar = { NavBar(navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("createNewTargetLocation") },
                icon = { Icon(Icons.Default.LocationOn, contentDescription = "Nueva ubicación") },
                text = { Text("Nueva Ubicación") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Crea una ubucacion para aumentar tu racha",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (locations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay ubicaciones guardadas")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(locations) { location ->
                        TargetLocationCard(
                            location = location,
                            onDelete = { locationToDelete = it }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    locationToDelete?.let { location ->
        AlertDialog(
            onDismissRequest = { locationToDelete = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Eliminar esta ubicación?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            Service.deleteTargetLocationEntity(location)
                            refreshLocations()
                            locationToDelete = null
                        }
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { locationToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun TargetLocationCard(location: TargetLocationEntity, onDelete: (TargetLocationEntity) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = "Ubicación",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Lat: ${"%.6f".format(location.position.latitude)}, " +
                            "Lng: ${"%.6f".format(location.position.longitude)}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Radio: ${"%.1f".format(location.radiusMeters)} metros",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = { onDelete(location) }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}