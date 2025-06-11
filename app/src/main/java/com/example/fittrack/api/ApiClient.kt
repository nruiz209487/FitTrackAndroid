package com.example.fittrack.api

import android.util.Log
import com.example.fittrack.api.Request.RegisterUserResponse
import com.example.fittrack.api.Request.UserRegistrationRequest
import com.example.fittrack.entity.*
import com.example.fittrack.service.utils.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    /**
     * Constante con la URL base de la API.
     */
    private const val BASE_URL = "http://10.0.2.2:8000"
    // David si no puedes levantar el servidor en laravel puedes probar con el servicio despleglado pero tiene un limite de consultas asi que no es seguro que funcione "https://fitrackapi-production.up.railway.app"

    /**
     * Actualiza los datos de un usuario en el servidor.
     * @param userId ID del usuario a actualizar
     * @param user Objeto con los datos actualizados
     */
    suspend fun updateUser(
        userId: Int,
        user: Request.UserUpdateRequest
    ): Request.UserUpdateResponse {
        val response = retrofitService.updateUser(userId, user)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Update failed: ${response.errorBody()?.string()}")
        }
    }

    /**
     * Cliente HTTP configurado con interceptores:
     * - AuthInterceptor: agrega token de autenticación a cada solicitud.
     * - HttpLoggingInterceptor: muestra logs de las peticiones para depuración.
     */
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .readTimeout(2000, TimeUnit.SECONDS)
        .build()

    /**
     * Instancia del servicio Retrofit, creada de forma perezosa (lazy).
     */
    private val retrofitService: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }

    /**
     * Configura y retorna una instancia de Retrofit con la URL base y cliente HTTP.
     */
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    /**
     * Obtiene los datos de un usuario por correo y contraseña.
     * @return Objeto con los datos del usuario si la respuesta es exitosa, null si falla.
     */
    suspend fun getUser(userEmail: String, password: String): Request.UserByEmailSuccessResponse? {
        return try {
            val loginRequest = Request.LoginRequest(userEmail, password)
            val response = retrofitService.getUserByEmail(userEmail, loginRequest)

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("API_CLIENT", "Error al obtener usuario: ${response.code()} - ${response.message()}")
                val errorBody = response.errorBody()?.string()
                Log.e("API_CLIENT", "Error body: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("API_CLIENT", "Excepción al obtener usuario: ${e.message}")
            null
        }
    }

    /**
     * Registra un nuevo usuario en la API.
     */
    suspend fun registerUser(user: UserRegistrationRequest): RegisterUserResponse {
        val response = retrofitService.registerUser(user)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Registration failed: ${response.errorBody()?.string()}")
        }
    }

    /**
     * Inserta una nueva localización objetivo para el usuario.
     */
    suspend fun insertTargetLocation(
        userId: Int,
        testLocation: Request.TargetLocationRequest
    ): Any {
        val response = retrofitService.insertTargetLocation(userId, testLocation)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Registration failed: ${response.errorBody()?.string()}")
        }
    }

    /**
     * Inserta una nueva rutina para el usuario.
     */
    suspend fun insertRoutine(routine: RoutineEntity, userId: Int): RoutineEntity {
        val response = retrofitService.insertRoutine(userId, routine)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Insert routine failed: ${response.errorBody()?.string()}")
        }
    }

    /**
     * Elimina una rutina del usuario.
     */
    suspend fun deleteRoutine(routineId: Int, userId: Int): Any? {
        val response = retrofitService.deleteRoutine(userId, routineId)
        return if (response.isSuccessful) {
            println("Rutina eliminada exitosamente")
            response.body()
        } else {
            println("Error al eliminar rutina: ${response.errorBody()?.string()}")
            null
        }
    }

    /**
     * Elimina una nota del usuario.
     */
    suspend fun deleteNote(noteId: Int, userId: Int): Any? {
        val response = retrofitService.deleteNote(userId, noteId)
        return if (response.isSuccessful) {
            println("Nota eliminada exitosamente")
            response.body()
        } else {
            println("Error al eliminar rutina: ${response.errorBody()?.string()}")
            null
        }
    }

    /**
     * Elimina un registro de ejercicio del usuario.
     */
    suspend fun deleteExeciseLog(exerciseLogId: Int, idUser: Int): Any? {
        val response = retrofitService.deleteExeciseLog(idUser, exerciseLogId)
        return if (response.isSuccessful) {
            println("Nota eliminada exitosamente")
            response.body()
        } else {
            println("Error al eliminar rutina: ${response.errorBody()?.string()}")
            null
        }
    }

    /**
     * Inserta un registro de ejercicio para el usuario.
     */
    suspend fun insertExerciseLog(testExerciseLog: ExerciseLogEntity, userId: Int): Any {
        val response = retrofitService.insertExerciseLog(userId, testExerciseLog)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Insert routine failed: ${response.errorBody()?.string()}")
        }
    }

    /**
     * Inserta una nota para el usuario.
     */
    suspend fun insertNote(testNote: NoteEntity, userId: Int): Any {
        val response = retrofitService.insertNote(userId, testNote)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Insert routine failed: ${response.errorBody()?.string()}")
        }
    }

    /**
     * Obtiene la lista de ejercicios disponibles.
     */
    suspend fun getExercises(): List<ExerciseEntity> {
        val response = retrofitService.getExercises()
        return response.body().orEmpty()
    }

    /**
     * Obtiene las localizaciones objetivo del usuario.
     */
    suspend fun getTargetLocations(userId: Int): List<TargetLocationEntity> {
        val response = retrofitService.getTargetLocations(userId)
        return response.body().orEmpty()
    }

    /**
     * Obtiene las rutinas asociadas al usuario.
     */
    suspend fun getRoutines(userId: Int): List<RoutineEntity> {
        val response = retrofitService.getRoutines(userId)
        return response.body().orEmpty()
    }

    /**
     * Obtiene las notas del usuario.
     */
    suspend fun getNotes(userId: Int): List<NoteEntity> {
        val response = retrofitService.getNotes(userId)
        return response.body().orEmpty()
    }

    /**
     * Obtiene los registros de ejercicios del usuario.
     */
    suspend fun getExerciseLogs(userId: Int): List<ExerciseLogEntity> {
        val response = retrofitService.getExerciseLogs(userId)
        return response.body().orEmpty()
    }

    /**
     * Elimina una localización objetivo del usuario.
     */
    suspend fun deleteTargetLocation(id: Int, userId: Int): Unit? {
        val response = retrofitService.deleteTargetLocation(userId, id)
        return if (response.isSuccessful) {
            println("Nota eliminada exitosamente")
            response.body()
        } else {
            println("Error al eliminar rutina: ${response.errorBody()?.string()}")
            null
        }
    }
}
