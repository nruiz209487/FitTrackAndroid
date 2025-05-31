package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.api.ApiClient
import com.example.fittrack.entity.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDataScreen(navController: NavController) {
    var user by remember { mutableStateOf<UserEntity?>(null) }
    var editedName by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }
    var editedProfileImage by remember { mutableStateOf("") }
    val apiService = remember { ApiClient }

    LaunchedEffect(Unit) {
        user = apiService.getUser()
        user?.let {
            editedName = it.name ?: ""
            editedEmail = it.email
            editedProfileImage = it.profileImage ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
                // Campo para el nombre
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo para el email
                OutlinedTextField(
                    value = editedEmail,
                    onValueChange = { editedEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                // Campo para la imagen de perfil
                OutlinedTextField(
                    value = editedProfileImage,
                    onValueChange = { editedProfileImage = it },
                    label = { Text("URL de imagen de perfil") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        val updatedUser = user!!.copy(
                            name = editedName,
                            email = editedEmail,
                            profileImage = editedProfileImage
                        )
                        // Llamar a tu función de actualización aquí
                        // apiService.updateUser(updatedUser)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar cambios")
                }
            }
        }
    }
}
