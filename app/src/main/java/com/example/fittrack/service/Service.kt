package com.example.fittrack.service

import android.util.Log
import com.example.fittrack.MainActivity
import com.example.fittrack.api.ApiClient
import com.example.fittrack.api.Request
import com.example.fittrack.api.Request.UserRegistrationRequest
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.entity.TargetLocationEntity
import com.example.fittrack.entity.UserEntity

object Service {
    // Implementación de la función insertRoutineToApi
    suspend fun insertRoutineToApi(testRoutine: RoutineEntity, idUser: Int) {
        try {
            val insertedRoutine = ApiClient.insertRoutine(testRoutine, idUser)
            println("Rutina insertada exitosamente: ${insertedRoutine.name}")
            // Aquí puedes manejar la respuesta exitosa
        } catch (e: Exception) {
            println("Error al insertar rutina: ${e.message}")
            // Aquí puedes manejar el error
            throw e
        }
    }
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
                        email = userEntity.email,
                        password = userEntity.password,
                        password_confirmation = userEntity.password,
                        name = userEntity.name
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
    suspend fun insertTargetLocationsToApi(
        targetLocationEntity: TargetLocationEntity,
        idUser: Int
    ) {
        try {
            val testLocation = Request.TargetLocationRequest(
                name = targetLocationEntity.name,
                position = "${targetLocationEntity.position.latitude},${targetLocationEntity.position.longitude}",
                radiusMeters = targetLocationEntity.radiusMeters
            )

            val response = ApiClient.insertTargetLocation(idUser, testLocation)

        } catch (e: Exception) {
            Log.e("API", "Excepción: ${e.localizedMessage}")
        }
    }



    suspend fun login(userEntity: UserEntity) {
        val dao = MainActivity.database.trackFitDao()

        try {

                // Llamar al endpoint /api/user/{email}
                val userResponse = ApiClient.getUser(userEntity.email)

                if (userResponse != null) {
                    Log.d("GET_USER_TEST", "Usuario obtenido con éxito!")
                    Log.d("GET_USER_TEST", "User ID: ${userResponse.user.user_id}")
                    Log.d("GET_USER_TEST", "Email: ${userResponse.user.email}")
                    Log.d("GET_USER_TEST", "Name: ${userResponse.user.name}")
                    Log.d("GET_USER_TEST", "Streak: ${userResponse.user.streak_days}")

                } else {
                    Log.e("GET_USER_TEST", "No se encontró el usuario con email $userEntity")
                }
            } catch (e: Exception) {
            Log.e("REGISTRATION_TEST", "Error al registrar usuario: ${e.message}")
        }
        }


        suspend fun updateUserApi(userEntity: UserEntity) {
            val dao = MainActivity.database.trackFitDao()
            dao.updateUser(userEntity)
        }

    suspend  fun insertNoteToApi(testNote: NoteEntity, idUser: Int) {
        try {
            val insertedRoutine = ApiClient.insertNote(testNote, idUser)
            println("Nota insertada exitosamente: ${insertedRoutine}")
            // Aquí puedes manejar la respuesta exitosa
        } catch (e: Exception) {
            println("Error al insertar rutina: ${e.message}")
            // Aquí puedes manejar el error
            throw e
        }
    }

    suspend fun insertExerciseLogToApi(testExerciseLog: ExerciseLogEntity, idUser: Int) {
        try {
            val insertedRoutine = ApiClient.insertExerciseLog(testExerciseLog, idUser)
            println("Nota insertada exitosamente: ${insertedRoutine}")
            // Aquí puedes manejar la respuesta exitosa
        } catch (e: Exception) {
            println("Error al insertar rutina: ${e.message}")
            // Aquí puedes manejar el error
            throw e
        }
    }

    suspend  fun deleteRoutine(routineId: Int, idUser: Int) {
        try {
            val insertedRoutine = ApiClient.deleteRoutine(routineId, idUser)
            println("Nota insertada exitosamente: ${insertedRoutine}")
            // Aquí puedes manejar la respuesta exitosa
        } catch (e: Exception) {
            println("Error al insertar rutina: ${e.message}")
            // Aquí puedes manejar el error
            throw e
        }
    }

    suspend fun deleteNote(noteId: Int, idUser: Int) {
        try {
            val insertedRoutine = ApiClient.deleteNote(noteId, idUser)
            println("Nota insertada exitosamente: ${insertedRoutine}")
            // Aquí puedes manejar la respuesta exitosa
        } catch (e: Exception) {
            println("Error al insertar rutina: ${e.message}")
            // Aquí puedes manejar el error
            throw e
        }
    }

    suspend   fun deletetExerciseLog(ExerciseLogId: Int, idUser: Int) {
        try {
            val insertedRoutine = ApiClient.deleteExeciseLog(ExerciseLogId, idUser)
            println("Nota insertada exitosamente: ${insertedRoutine}")
            // Aquí puedes manejar la respuesta exitosa
        } catch (e: Exception) {
            println("Error al insertar rutina: ${e.message}")
            // Aquí puedes manejar el error
            throw e
        }
    }



}