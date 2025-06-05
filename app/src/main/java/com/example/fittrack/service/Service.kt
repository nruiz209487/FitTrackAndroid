package com.example.fittrack.service

import android.util.Log
import com.example.fittrack.MainActivity
import com.example.fittrack.api.ApiClient
import com.example.fittrack.api.Request.UserRegistrationRequest
import com.example.fittrack.entity.UserEntity

object Service {

        suspend fun insertLogsFromApi() {
            val dao = MainActivity.database.trackFitDao()
            TokenManager.userId?.let { userId ->
                val apiLogs = ApiClient.getExerciseLogs(userId)
                dao.insertExerciseLogs(apiLogs)
            }
        }

        suspend fun insertNotesFromApi() {
            val dao = MainActivity.database.trackFitDao()
            TokenManager.userId?.let { userId ->
                val apiNotes = ApiClient.getNotes(userId)
                dao.insertNotes(apiNotes)
            }
        }

        suspend fun insertRoutinesFromApi() {
            val dao = MainActivity.database.trackFitDao()
            TokenManager.userId?.let { userId ->
                val apiRoutines = ApiClient.getRoutines(userId)
                dao.insertRoutines(apiRoutines)
            }
        }

        suspend fun insertExercisesFromApi() {
            val dao = MainActivity.database.trackFitDao()
            val apiExercises = ApiClient.getExercises()
            dao.insertExercies(apiExercises)
        }

        suspend fun insertTargetLocationsFromApi() {
            val dao = MainActivity.database.trackFitDao()
            val apiTargetLocations = ApiClient.getTargetLocations()
            dao.insertTargetLocations(apiTargetLocations)
        }


        suspend fun insertUserToApi(userEntity: UserEntity) {
            val dao = MainActivity.database.trackFitDao()
                try {
                    val testUser = UserRegistrationRequest(
                        email = "testuser_${System.currentTimeMillis()}@example.com",
                        password = "password123",
                        password_confirmation = "password123",
                        name = "Test User"
                    )

                    val response = ApiClient.registerUser(testUser)
                    if (response.success) {
                        Log.d("REGISTRATION_TEST", "Usuario registrado con éxito! ID: ${response.data.user_id}")
                        Log.d("REGISTRATION_TEST", "Token: ${response.data.token}")
                    } else {
                        Log.e("REGISTRATION_TEST", "Error en registro: ${response.message}")
                    }
                } catch (e: Exception) {
                    Log.e("REGISTRATION_TEST", "Error al registrar usuario: ${e.message}")
                }
            }


    suspend fun login() {
        val dao = MainActivity.database.trackFitDao()

        try {
            // Crear un email falso único
            val fakeEmail = "testuser_${System.currentTimeMillis()}@example.com"

            // Crear el objeto de solicitud
            val testUser = UserRegistrationRequest(
                email = fakeEmail,
                password = "password123",
                password_confirmation = "password123",
                name = "Test User"
            )

            // Registrar el usuario
            val registerResponse = ApiClient.registerUser(testUser)

            if (registerResponse.success) {
                Log.d("REGISTRATION_TEST", "Usuario registrado con éxito! ID: ${registerResponse.data.user_id}")
                Log.d("REGISTRATION_TEST", "Token: ${registerResponse.data.token}")

                // Guardar el token para uso posterior
                val token = registerResponse.data.token

                // Llamar al endpoint /api/user/{email}
                val userResponse = ApiClient.getUser(fakeEmail)

                if (userResponse != null) {
                    Log.d("GET_USER_TEST", "Usuario obtenido con éxito!")
                    Log.d("GET_USER_TEST", "User ID: ${userResponse.user.user_id}")
                    Log.d("GET_USER_TEST", "Email: ${userResponse.user.email}")
                    Log.d("GET_USER_TEST", "Name: ${userResponse.user.name}")
                    Log.d("GET_USER_TEST", "Streak: ${userResponse.user.streak_days}")

                    // Aquí podrías guardar los datos del usuario en la base de datos local
                    // dao.insertUser(userResponse.user)

                } else {
                    Log.e("GET_USER_TEST", "No se encontró el usuario con email $fakeEmail")
                }
            } else {
                Log.e("REGISTRATION_TEST", "Error en registro: ${registerResponse.message}")
            }
        } catch (e: Exception) {
            Log.e("LOGIN_TEST", "Error en el proceso de login: ${e.message}")
            e.printStackTrace()
        }
    }

        suspend fun updateUserApi(userEntity: UserEntity) {
            val dao = MainActivity.database.trackFitDao()
            dao.updateUser(userEntity)
        }
    }