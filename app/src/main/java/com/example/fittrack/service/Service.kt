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
import com.example.fittrack.service.utils.TokenManager

object Service {
    /**
     *  Intenta loguear, y si falla, registra al usuario
     */
    suspend fun registerOrLogin(userEntity: UserEntity): Boolean {
        return try {
            val loginSuccess = login(userEntity)

            if (loginSuccess) {
                Log.d("SERVICE", "Login exitoso para usuario existente")
                true
            } else {
                Log.d("SERVICE", "Usuario no existe, procediendo a registrar...")
                insertUserToApi(userEntity)
            }
        } catch (e: Exception) {
            Log.e("SERVICE", "Error en registerOrLogin: ${e.message}")
            false
        }
    }

    /**
     *  Función para hacer login del usuario
     */
    private suspend fun login(userEntity: UserEntity): Boolean {
        return try {
            val userResponse = ApiClient.getUser(userEntity.email, userEntity.password)

            if (userResponse != null) {
                // Guarda el token
                userResponse.token?.let {
                    userResponse.user?.let { it1 ->
                        TokenManager.saveUserSession(
                            it,
                            it1.id
                        )
                    }
                }

                val dao = MainActivity.database.trackFitDao()
                val existingUser = userResponse.user?.let { dao.getUserById(it.id) }
                // Si el usuario no existe localmente, lo inserta
                if (existingUser == null) {
                    val userFromApi = userResponse.user?.let {
                        UserEntity(
                            id = it.id,
                            name = userResponse.user.name,
                            email = userResponse.user.email,
                            streakDays = userResponse.user.streakDays ?: 1,
                            profileImage = userResponse.user.profileImage,
                            lastStreakDay = userResponse.user.lastStreakDay,
                            password = userEntity.password,
                            gender = userResponse.user.gender,
                            height = userResponse.user.height,
                            weight = userResponse.user.weight
                        )
                    }
                    if (userFromApi != null) {
                        dao.insertUser(userFromApi)
                    }
                    Log.d(
                        "LOGIN",
                        "Usuario sincronizado localmente con ID: ${userResponse.user?.id}"
                    )
                }

                Log.d("LOGIN", "Login exitoso - Token guardado, User ID: ${userResponse.user?.id}")
                true
            } else {
                Log.e("LOGIN", "Usuario no encontrado o credenciales incorrectas")
                false
            }
        } catch (e: Exception) {
            Log.e("LOGIN", "Error en login: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Actualiza un usuario
     */
    suspend fun updateUserApi(userEntity: UserEntity) {
        try {
            val userUpdateRequest = Request.UserUpdateRequest(
                email = userEntity.email,
                password = userEntity.password,
                password_confirmation = userEntity.password,
                name = userEntity.name,
                gender = userEntity.gender,
                height = userEntity.height,
                weight = userEntity.weight,
                streakDays = userEntity.streakDays,
                lastStreakDay = userEntity.lastStreakDay,
                profileImage = userEntity.profileImage
            )

            val response = ApiClient.updateUser(userEntity.id, userUpdateRequest)

            if (response.success) {
                val dao = MainActivity.database.trackFitDao()
                dao.updateUser(userEntity)
            } else {
                throw Exception("API returned success=false: ${response.message}")
            }

        } catch (e: Exception) {
            throw Exception("Error updating user: ${e.message}")
        }
    }

    /**
     *  Función para hacer registro del usuario
     */
    private suspend fun insertUserToApi(userEntity: UserEntity): Boolean {
        try {
            val testUser = UserRegistrationRequest(
                email = userEntity.email,
                password = userEntity.password,
                password_confirmation = userEntity.password,
                name = userEntity.name,
                gender = userEntity.gender,
                height = userEntity.height,
                weight = userEntity.weight,
            )

            val response = ApiClient.registerUser(testUser)

            if (response.success) {
                Log.d(
                    "REGISTRATION_TEST",
                    "Usuario registrado con éxito en API! ID: ${response.data.id}"
                )
                Log.d("REGISTRATION_TEST", "Token: ${response.data.token}")
                val dao = MainActivity.database.trackFitDao()
                val userWithApiId = userEntity.copy(
                    id = response.data.id
                )

                dao.insertUser(userWithApiId)
                Log.d(
                    "REGISTRATION_TEST",
                    "Usuario insertado localmente con ID de API: ${response.data.id}"
                )
                TokenManager.saveUserSession(response.data.token, response.data.id)

                return true
            } else {
                Log.e("REGISTRATION_TEST", "Error en registro API: ${response.message}")
                return false
            }
        } catch (e: Exception) {
            Log.e("REGISTRATION_TEST", "Error al registrar usuario: ${e.message}")
            return false
        }
    }

    /**
     *  Función  que se encarga de registar un TargetLocation en api y local
     */
    suspend fun insertTargetLocationsToApi(targetLocationEntity: TargetLocationEntity): Boolean {
        val userId = TokenManager.userId
        if (userId == null) {
            Log.e("SERVICE", "No hay userId disponible")
            return false
        }

        return try {
            val testLocation = Request.TargetLocationRequest(
                name = targetLocationEntity.name,
                position = "${targetLocationEntity.position.latitude},${targetLocationEntity.position.longitude}",
                radiusMeters = targetLocationEntity.radiusMeters
            )

            val response = ApiClient.insertTargetLocation(userId, testLocation)
            Log.d("SERVICE", "Ubicación objetivo insertada en API exitosamente: $response")

            val dao = MainActivity.database.trackFitDao()
            dao.insertTargetLocation(targetLocationEntity)
            Log.d("SERVICE", "Ubicación objetivo insertada localmente")

            true
        } catch (e: Exception) {
            Log.e("SERVICE", "Error al insertar ubicación objetivo: ${e.localizedMessage}")
            false
        }
    }

    /**
     *  Función  que se encarga de registar un Notes en api y local
     */
    suspend fun insertNoteToApi(testNote: NoteEntity): Boolean {
        val userId = TokenManager.userId
        if (userId == null) {
            Log.e("SERVICE", "No hay userId disponible")
            return false
        }

        return try {
            ApiClient.insertNote(testNote, userId)
            Log.d("SERVICE", "Nota insertada en API exitosamente")

            val dao = MainActivity.database.trackFitDao()
            dao.insertNote(testNote)
            Log.d("SERVICE", "Nota insertada localmente")

            true
        } catch (e: Exception) {
            Log.e("SERVICE", "Error al insertar nota: ${e.message}")
            false
        }
    }

    /**
     *  Función  que se encarga de registar un Routine en api y local
     */
    suspend fun insertRoutineToApi(testRoutine: RoutineEntity): Boolean {
        val userId = TokenManager.userId
        if (userId == null) {
            Log.e("SERVICE", "No hay userId disponible")
            return false
        }

        return try {
            val insertedRoutine = ApiClient.insertRoutine(testRoutine, userId)
            Log.d("SERVICE", "Rutina insertada en API exitosamente: ${insertedRoutine.name}")

            val dao = MainActivity.database.trackFitDao()
            dao.insertRoutine(testRoutine)
            Log.d("SERVICE", "Rutina insertada localmente")

            true
        } catch (e: Exception) {
            Log.e("SERVICE", "Error al insertar rutina: ${e.message}")
            false
        }
    }

    /**
     *  Función  que se encarga de registar un ExerciseLog en api y local
     */
    suspend fun insertExerciseLogToApi(testExerciseLog: ExerciseLogEntity): Boolean {
        val userId = TokenManager.userId
        if (userId == null) {
            Log.e("SERVICE", "No hay userId disponible")
            return false
        }

        return try {
            ApiClient.insertExerciseLog(testExerciseLog, userId)
            Log.d("SERVICE", "Log insertado en API exitosamente")

            val dao = MainActivity.database.trackFitDao()
            dao.insertExerciseLog(testExerciseLog)
            Log.d("SERVICE", "Log insertado localmente")

            true
        } catch (e: Exception) {
            Log.e("SERVICE", "Error al insertar log: ${e.message}")
            false
        }
    }

    /**
     *  Función  que se encarga de borrar un TargetLocation en api y local
     */
    suspend fun deleteTargetLocationEntity(targetLocation: TargetLocationEntity): Boolean {
        val userId = TokenManager.userId
        if (userId == null) {
            Log.e("SERVICE", "No hay userId disponible")
            return false
        }

        return try {
            ApiClient.deleteTargetLocation(targetLocation.id, userId)
            Log.d("SERVICE", "Ubicación objetivo eliminada de API exitosamente")

            val dao = MainActivity.database.trackFitDao()
            dao.deleteTargetLocation(targetLocation)
            Log.d("SERVICE", "Ubicación objetivo eliminada localmente")

            true
        } catch (e: Exception) {
            Log.e("SERVICE", "Error al eliminar ubicación objetivo: ${e.message}")
            false
        }
    }

    /**
     *  Función  que se encarga de borrar un Routine en api y local
     */
    suspend fun deleteRoutine(routine: RoutineEntity): Boolean {
        val userId = TokenManager.userId
        if (userId == null) {
            Log.e("SERVICE", "No hay userId disponible")
            return false
        }

        return try {
            ApiClient.deleteRoutine(routine.id, userId)
            Log.d("SERVICE", "Rutina eliminada de API exitosamente")

            val dao = MainActivity.database.trackFitDao()
            dao.deleteRoutine(routine)
            Log.d("SERVICE", "Rutina eliminada localmente")

            true
        } catch (e: Exception) {
            Log.e("SERVICE", "Error al eliminar rutina: ${e.message}")
            false
        }
    }

    /**
     *  Función  que se encarga de borrar un Note en api y local
     */
    suspend fun deleteNote(note: NoteEntity): Boolean {
        val userId = TokenManager.userId
        if (userId == null) {
            Log.e("SERVICE", "No hay userId disponible")
            return false
        }

        return try {
            ApiClient.deleteNote(note.id, userId)
            Log.d("SERVICE", "Nota eliminada de API exitosamente")

            val dao = MainActivity.database.trackFitDao()
            dao.deleteNote(note)
            Log.d("SERVICE", "Nota eliminada localmente")

            true
        } catch (e: Exception) {
            Log.e("SERVICE", "Error al eliminar nota: ${e.message}")
            false
        }
    }

    /**
     *  Función  que se encarga de borrar un ExerciseLog en api y local
     */
    suspend fun deleteExerciseLog(exerciseLog: ExerciseLogEntity): Boolean {
        val userId = TokenManager.userId
        if (userId == null) {
            Log.e("SERVICE", "No hay userId disponible")
            return false
        }

        return try {
            ApiClient.deleteExeciseLog(exerciseLog.id, userId)
            Log.d("SERVICE", "Log eliminado de API exitosamente")

            val dao = MainActivity.database.trackFitDao()
            dao.deleteExerciseLogs(exerciseLog)
            Log.d("SERVICE", "Log eliminado localmente")

            true
        } catch (e: Exception) {
            Log.e("SERVICE", "Error al eliminar log: ${e.message}")
            false
        }
    }

    /**
     *  Función  que se encarga de insertar  ExerciseLogs de la api a local
     */
    suspend fun insertLogsFromApi() {
        val dao = MainActivity.database.trackFitDao()
        TokenManager.userId?.let { userId ->
            val apiLogs = ApiClient.getExerciseLogs(userId)
            dao.insertExerciseLogs(apiLogs)
        }
    }

    /**
     *  Función  que se encarga de insertar  Notes de la api a local
     */
    suspend fun insertNotesFromApi() {
        val dao = MainActivity.database.trackFitDao()
        TokenManager.userId?.let { userId ->
            val apiNotes = ApiClient.getNotes(userId)
            dao.insertNotes(apiNotes)
        }
    }

    /**
     *  Función  que se encarga de insertar  Routines de la api a local
     */
    suspend fun insertRoutinesFromApi() {
        val dao = MainActivity.database.trackFitDao()
        TokenManager.userId?.let { userId ->
            val apiRoutines = ApiClient.getRoutines(userId)
            dao.insertRoutines(apiRoutines)
        }
    }

    /**
     *  Función  que se encarga de insertar  TargetLocations de la api a local
     */
    suspend fun insertTargetLocationsFromApi() {
        val dao = MainActivity.database.trackFitDao()
        TokenManager.userId?.let { userId ->
            val apiTargetLocations = ApiClient.getTargetLocations(userId)
            dao.insertTargetLocations(apiTargetLocations)
        }
    }

    /**
     *  Función  que se encarga de insertar  Exercises de la api a local
     */
    suspend fun insertExercisesFromApi() {
        val dao = MainActivity.database.trackFitDao()
        val apiExercises = ApiClient.getExercises()
        dao.insertExercises(apiExercises)
    }
}