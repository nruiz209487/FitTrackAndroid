package com.example.fittrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.fittrack.api.ApiClient
import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.ui.ui_elements.NavBar

@Composable
fun HomeScreen(navController: NavController) {
    var routines by remember { mutableStateOf<List<RoutineEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        routines = ApiClient.getRoutines()
    }

    Scaffold(
        bottomBar = { NavBar(navController = navController) }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            RoutineGrid(
                routines = routines, onItemClick = { routine ->
                    navController.navigate("routine/${routine.id}")
                })
        }
    }
}


@Composable
fun RoutineGrid(
    routines: List<RoutineEntity>, onItemClick: (RoutineEntity) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(routines) { routine ->
            RoutineCard(
                routine = routine, onClick = { onItemClick(routine) })
        }
    }
}

@Composable
fun RoutineCard(
    routine: RoutineEntity, onClick: () -> Unit
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
            AsyncImage(
                model = ImageRequest.Builder(context).data(routine.imageUri).crossfade(true)
                    .build(),
                contentDescription = "Imagen de la rutina ${routine.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )

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