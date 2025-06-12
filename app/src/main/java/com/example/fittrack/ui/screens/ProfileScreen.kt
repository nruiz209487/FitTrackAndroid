package com.example.fittrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fittrack.MainActivity
import com.example.fittrack.R
import com.example.fittrack.entity.UserEntity
import com.example.fittrack.ui.ui_elements.NavBar
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

/**
 * Composable que sirve como el perfil de usuario
 */
@Composable
fun ProfileScreen(navController: NavController) {
    var user by remember { mutableStateOf<UserEntity?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val dao = MainActivity.database.trackFitDao()  // Declara la bd
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        user = dao.getUser()
    }

    if (showLogoutDialog) {
        //alerta al cerrar la app
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Cerrar sesión",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que quieres cerrar sesión? Se borrarán todos los datos locales y se cerrará la aplicación.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                dao.clearAllData() // borra la db
                                (context as? MainActivity)?.finishAffinity()
                                exitProcess(0) // salida forzada
                            } catch (e: Exception) {
                                (context as? MainActivity)?.finishAffinity()
                                exitProcess(0)
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        bottomBar = { NavBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ProfileHeaderSection(user = user)

            Spacer(modifier = Modifier.height(16.dp))

            if (user != null) {
                UserMetricsSection(user = user!!)
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                CircularProgressIndicator(modifier = Modifier.padding(24.dp))
            }

            ActionButtonsSection(
                navController = navController,
                onLogoutClick = { showLogoutDialog = true }
            )
        }
    }
}
// cabecera con la imagen del usario y su nombre
@Composable
private fun ProfileHeaderSection(user: UserEntity?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            ProfileImage(
                imageUrl = user?.profileImage,
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = user?.name ?: "Invitado",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                if (user?.email != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}
// iamgen de perfil
@Composable
fun ProfileImage(imageUrl: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = CircleShape,
            modifier = modifier
        ) {
            // imagen de perfil
            if (imageUrl != "") {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize()
                )
            } else {
                // imagen por defecto si no esta asignada en la pripiedad del suario
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Imagen de perfil por defecto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize()
                )
            }
        }
    }
}
/**
 * carta de datos se ha quedado asi antes habia mas datos pero se han cambiado de lugar
 */
@Composable
private fun UserMetricsSection(user: UserEntity) {
    val metrics = listOf(
        MetricData("Racha actual", "${user.streakDays} días", Icons.Default.DateRange),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        metrics.forEach { metric ->
            MetricCard(metric = metric)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * carta de datos
 */
@Composable
private fun MetricCard(metric: MetricData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = metric.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = metric.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = metric.value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Composable que basicamente meustar los botones
 */
@Composable
private fun ActionButtonsSection(
    navController: NavController,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // boton para nav user_data
        FilledTonalButton(
            onClick = { navController.navigate("user_data") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = null
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = "Actualizar datos")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Actualizar datos", fontWeight = FontWeight.Medium)
        }
        // boton para nav targetLocation
        FilledTonalButton(
            onClick = { navController.navigate("targetLocation") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            elevation = null
        ) {
            Icon(Icons.Outlined.LocationOn, contentDescription = "Nuevo gimnasio")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Nuevo gimnasio", fontWeight = FontWeight.Medium)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // boton para nav settings
            ActionButton(
                onClick = { navController.navigate("settings") },
                icon = Icons.Outlined.Settings,
                text = "Ajustes",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
            )
            // boton para nav IMCScreen
            ActionButton(
                onClick = { navController.navigate("IMCScreen") },
                icon = Icons.Outlined.MonitorWeight,
                text = "IMC",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
            )
        }
        // boton para cerrar sesion
        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Cerrar sesión", fontWeight = FontWeight.Medium)
        }
    }
}
/**
 * composable para los botones
 */
@Composable
private fun ActionButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor
        ),
        elevation = null
    ) {
        Icon(icon, contentDescription = text)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontWeight = FontWeight.Medium)
    }
}
//datos del user
data class MetricData(
    val title: String,
    val value: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)