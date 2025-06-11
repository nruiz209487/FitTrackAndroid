package com.example.fittrack.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.MainActivity
import com.example.fittrack.entity.UserEntity
import com.example.fittrack.service.Service
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Pagina del login basicamente  verifica que auentique con fireabse y despues llama a la funcion se service loginornewuser
 */
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()
    var mail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    fun processUserLogin(user: FirebaseUser) {
        scope.launch {
            try {
                val userEntity = UserEntity(
                    name = user.displayName ?: "Usuario",
                    email = user.email ?: mail,
                    streakDays = 1,
                    profileImage = user.photoUrl?.toString() ?: "",
                    lastStreakDay = LocalDate.now().minusDays(1).toString(),
                    password = password,
                    gender = "",
                    height = 0.0,
                    weight = 0.0
                )

                val success = Service.registerOrLogin(userEntity)
                isLoading = false

                if (success) {
                    Toast.makeText(
                        context, "Sesión iniciada correctamente", Toast.LENGTH_SHORT
                    ).show()


                    Service.insertLogsFromApi()
                    Service.insertNotesFromApi()
                    Service.insertRoutinesFromApi()
                    Service.insertTargetLocationsFromApi()


                    val dao = MainActivity.database.trackFitDao()
                    val existingUser = dao.getUser()
                    Service.insertExercisesFromApi()
                    if (existingUser?.gender.isNullOrEmpty() ||
                        existingUser?.height == null ||
                        existingUser.weight == null
                    ) {
                        navController.navigate("user_data")
                    } else {
                        navController.navigate("home")
                    }
                } else {
                    Toast.makeText(
                        context, "Error al conectar con el servidor", Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                isLoading = false
                Toast.makeText(
                    context, "Error de conexión", Toast.LENGTH_LONG
                ).show()
                Log.e("API", "Error al hacer petición a la API", e)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido a FitTrack",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            TextField(
                value = mail,
                onValueChange = { mail = it },
                label = { Text("Correo electrónico") },
                enabled = !isLoading,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                enabled = !isLoading,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            if (showConfirmPassword) {
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Contraseña") },
                    enabled = !isLoading,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon =
                            if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (mail.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        auth.signInWithEmailAndPassword(mail, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null && user.isEmailVerified) {
                                        processUserLogin(user)
                                    } else {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Por favor verifica tu correo electrónico antes de iniciar sesión.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        auth.signOut()
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "Error de autenticacion",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.e("Login", "Error en inicio de sesión", task.exception)
                                }
                            }
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Iniciar Sesión")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (!showConfirmPassword) {
                        if (mail.isNotEmpty() && password.isNotEmpty()) {
                            showConfirmPassword = true
                        } else {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        if (password != confirmPassword) {
                            Toast.makeText(
                                context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        isLoading = true
                        auth.createUserWithEmailAndPassword(mail, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.sendEmailVerification()
                                        ?.addOnCompleteListener { verifyTask ->
                                            isLoading = false
                                            if (verifyTask.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Cuenta creada. Verifica tu correo electrónico antes de iniciar sesión.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                showConfirmPassword = false
                                                confirmPassword = ""
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Error al enviar verificación",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    auth.signOut()
                                } else {
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "Error: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.e("Register", "Error al crear la cuenta", task.exception)
                                }
                            }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (showConfirmPassword) "Confirmar Registro" else "Crear Nueva Cuenta")
            }
        }
    }
}