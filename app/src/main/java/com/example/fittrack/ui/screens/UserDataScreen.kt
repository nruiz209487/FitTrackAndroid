package com.example.fittrack.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.fittrack.MainActivity
import com.example.fittrack.R
import com.example.fittrack.entity.UserEntity
import com.example.fittrack.service.Service
import com.example.fittrack.service.utils.FileUtils
import kotlinx.coroutines.launch
import java.io.File
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDataScreen(navController: NavController) {
    var user by remember { mutableStateOf<UserEntity?>(null) }
    var editedName by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }
    var editedProfileImage by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var editedGender by remember { mutableStateOf("") }
    var editedHeight by remember { mutableStateOf("") }
    var editedWeight by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isFirstTimeSetup by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dao = MainActivity.database.trackFitDao()
    val scope = rememberCoroutineScope()

    val genderOptions = listOf("Masculino", "Femenino", "Otro", "Prefiero no decir")

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val internalPath = FileUtils.copyUriToInternalStorage(context, it)
                selectedImageUri = internalPath.toUri()
                editedProfileImage = internalPath
            } catch (e: Exception) {
                errorMessage = "Error al guardar la imagen"
                showError = true
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        user = dao.getUser()
        user?.let {
            editedName = it.name ?: ""
            editedEmail = it.email
            editedProfileImage = it.profileImage ?: ""
            editedGender = it.gender ?: ""
            editedHeight = it.height?.toString() ?: ""
            editedWeight = it.weight?.toString() ?: ""

            // Verificar si es la primera vez configurando el perfil
            isFirstTimeSetup = it.gender.isNullOrEmpty() || it.height == null || it.weight == null

            if (it.profileImage?.isNotEmpty() == true) {
                selectedImageUri = try {
                    if (it.profileImage.startsWith("content://")) {
                        val internalPath = FileUtils.copyUriToInternalStorage(
                            context,
                            it.profileImage.toUri()
                        )
                        internalPath.toUri()
                    } else {
                        Uri.fromFile(File(it.profileImage))
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isFirstTimeSetup) "Completa tu perfil" else "Editar perfil")
                },
                navigationIcon = {
                    if (!isFirstTimeSetup) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (!isFirstTimeSetup) {
                        IconButton(onClick = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }) {
                            Icon(Icons.Filled.Home, contentDescription = "Go to Home")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (user == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // Mostrar mensaje de bienvenida si es primera vez
                if (isFirstTimeSetup) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "¡Bienvenido a FitTrack!",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Completa tu perfil para una mejor experiencia personalizada.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                ProfileImageSelector(
                    selectedImageUri = selectedImageUri,
                    onImageSelected = { imagePicker.launch("image/*") }
                )

                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = editedEmail,
                    onValueChange = { editedEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                // Campo de género como menú desplegable
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = editedGender,
                        onValueChange = {},
                        label = { Text("Género${if (isFirstTimeSetup) " *" else ""}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        isError = isFirstTimeSetup && editedGender.isEmpty()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    editedGender = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = editedHeight,
                    onValueChange = { editedHeight = it },
                    label = { Text("Altura (cm)${if (isFirstTimeSetup) " *" else ""}") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isFirstTimeSetup && editedHeight.isEmpty()
                )

                OutlinedTextField(
                    value = editedWeight,
                    onValueChange = { editedWeight = it },
                    label = { Text("Peso (kg)${if (isFirstTimeSetup) " *" else ""}") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isFirstTimeSetup && editedWeight.isEmpty()
                )

                // Mostrar campos obligatorios si es primera vez
                if (isFirstTimeSetup) {
                    Text(
                        text = "* Campos obligatorios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        if (editedName.isBlank() || editedEmail.isBlank()) {
                            showError = true
                            errorMessage = "Nombre y email son obligatorios"
                            return@Button
                        }

                        if (isFirstTimeSetup && (editedGender.isEmpty() || editedHeight.isEmpty() || editedWeight.isEmpty())) {
                            showError = true
                            errorMessage = "Por favor completa todos los campos obligatorios"
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            try {
                                val updatedUser = user!!.copy(
                                    name = editedName,
                                    email = editedEmail,
                                    profileImage = editedProfileImage,
                                    gender = editedGender.ifEmpty { null },
                                    height = editedHeight.toDoubleOrNull(),
                                    weight = editedWeight.toDoubleOrNull()
                                )

                                Service.updateUserApi(updatedUser)

                                Toast.makeText(
                                    context,
                                    if (isFirstTimeSetup) "Perfil configurado correctamente" else "Perfil actualizado correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                if (isFirstTimeSetup) {
                                    // Si es primera vez, ir a home
                                    navController.navigate("home") {
                                        popUpTo("user_data") { inclusive = true }
                                    }
                                } else {
                                    // Si es edición, volver atrás
                                    navController.popBackStack()
                                }
                            } catch (e: Exception) {
                                showError = true
                                errorMessage = "Error al actualizar: ${e.message}"
                                e.printStackTrace()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isFirstTimeSetup) "Completar configuración" else "Guardar cambios")
                    }
                }

                // Botón para saltar configuración si es primera vez (opcional)
                if (isFirstTimeSetup) {
                    TextButton(
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("user_data") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Completar más tarde")
                    }
                }
            }
        }

        if (showError) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(
                        onClick = { showError = false }
                    ) {
                        Text("OK")
                    }
                }
            ) {
                Text(errorMessage)
            }
        }
    }
}

@Composable
fun ProfileImageSelector(
    selectedImageUri: Uri?,
    onImageSelected: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = if (selectedImageUri != null) {
                rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(selectedImageUri)
                        .crossfade(true)
                        .build()
                )
            } else {
                painterResource(id = R.drawable.ic_launcher_foreground)
            },
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable(onClick = onImageSelected),
            contentScale = ContentScale.Crop
        )

        TextButton(
            onClick = onImageSelected,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Select image"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cambiar foto")
        }
    }
}