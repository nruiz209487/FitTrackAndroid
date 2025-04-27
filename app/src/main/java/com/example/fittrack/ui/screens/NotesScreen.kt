package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fittrack.api.ApiClient
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.ui.ui_elements.NavBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

//APP LISTA DE ECOTANCTOS
@Composable
fun NotesScreen(navController: NavController) {
    var posts by remember { mutableStateOf<List<NoteEntity>>(emptyList()) }
    LaunchedEffect(Unit) {
        posts = withContext(Dispatchers.IO) {
            ApiClient.getPosts()
        }
    }

    Scaffold(
        bottomBar = { NavBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Feed Social",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts) { post ->
                    NoteCard(post)
                }
            }

        }
    }
}

@Composable
fun NoteCard(post: NoteEntity) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(post.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar de ${post.header}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = post.header,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.postText,
                style = MaterialTheme.typography.bodyMedium
            )

        }
    }
}
