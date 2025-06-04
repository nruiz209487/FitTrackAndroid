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

        suspend fun insertUserFromApi() {
            val dao = MainActivity.database.trackFitDao()
            TokenManager.userId?.let { userId ->
                val apiUser = ApiClient.getUser(userId)
                apiUser?.let {
                    dao.insertUser(it)
                }
            }
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


        suspend fun updateUserApi(userEntity: UserEntity) {
            val dao = MainActivity.database.trackFitDao()
            dao.updateUser(userEntity)
        }
    }