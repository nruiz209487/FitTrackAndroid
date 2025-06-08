package com.example.fittrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fittrack.MainActivity
import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.ui.ui_elements.NavBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.fittrack.R
import com.example.fittrack.entity.UserEntity
import com.example.fittrack.service.Service
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {

    var currentUser by remember { mutableStateOf<UserEntity?>(null) }
    var allRoutines by remember { mutableStateOf<List<RoutineEntity>>(emptyList()) }
    var filteredRoutines by remember { mutableStateOf<List<RoutineEntity>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var routineToDelete by remember { mutableStateOf<RoutineEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val dao = MainActivity.database.trackFitDao()

    fun loadRoutines() {
        coroutineScope.launch {
            allRoutines = dao.getRoutines()
            filteredRoutines = allRoutines
            currentUser = dao.getUser()
        }
    }

    LaunchedEffect(Unit) {
        loadRoutines()
    }

    LaunchedEffect(searchQuery, allRoutines) {
        filteredRoutines = if (searchQuery.isBlank()) {
            allRoutines
        } else {
            allRoutines.filter { routine ->
                routine.name.contains(searchQuery, ignoreCase = true) ||
                        routine.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            UserTopBar(
                user = currentUser,
                onStreakClick = { navController.navigate("map") }
            )
        },
        bottomBar = { NavBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_routine") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear nueva rutina",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(2.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Buscar rutinas...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (filteredRoutines.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (allRoutines.isEmpty()) {
                            "No hay rutinas disponibles"
                        } else {
                            "No se encontraron rutinas con '$searchQuery'"
                        },
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                RoutineGrid(
                    routines = filteredRoutines,
                    onItemClick = { routine ->
                        navController.navigate("routine/${routine.id}")
                    },
                    onDeleteRoutine = { routine ->
                        routineToDelete = routine
                    }
                )
            }
        }
    }

    if (routineToDelete != null) {
        AlertDialog(
            onDismissRequest = { routineToDelete = null },
            title = { Text("Confirmar eliminación") },
            text = {
                Column {
                    Text("¿Estás seguro de que quieres eliminar esta rutina?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = routineToDelete?.name ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            routineToDelete?.let { Service.deleteRoutine( it) }
                            loadRoutines()
                            routineToDelete = null
                        }
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { routineToDelete = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun UserTopBar(user: UserEntity?, onStreakClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProfileImage(
            imageUrl = user?.profileImage ?: "",
            modifier = Modifier.size(32.dp)
        )
        if ((user?.streakDays ?: 0) > 0) {
            Text(
                modifier = Modifier
                    .clickable(onClick = onStreakClick)
                    .padding(horizontal = 8.dp),
                text = "${user?.streakDays} 🔥",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun ProfileImage(imageUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Surface(
        shape = CircleShape,
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Perfil",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun RoutineGrid(
    routines: List<RoutineEntity>,
    onItemClick: (RoutineEntity) -> Unit,
    onDeleteRoutine: (RoutineEntity) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(routines) { routine ->
            RoutineCard(
                routine = routine,
                onClick = { onItemClick(routine) },
                onDelete = { onDeleteRoutine(routine) }
            )
        }
    }
}

@Composable
fun RoutineCard(
    routine: RoutineEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(250.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(routine.imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen de la rutina ${routine.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar rutina",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = routine.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = routine.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
