package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
/***
 * Mustra las localizaciones
 */
@Composable
fun TargetLocationsScreen(navController: NavController) {
    val dao = MainActivity.database.trackFitDao() // Declara la bd
    val coroutineScope = rememberCoroutineScope()
    var locations by remember { mutableStateOf<List<TargetLocationEntity>>(emptyList()) }
    var locationToDelete by remember { mutableStateOf<TargetLocationEntity?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    // reecarga la apgina
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
        //boton para crear una nueva localizacion
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
//elimina una notificacion
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
/**
 * carta de TargetLocation te meutsra el no,bre la ubicaicon en cordenada y te deja elimarla
 */
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