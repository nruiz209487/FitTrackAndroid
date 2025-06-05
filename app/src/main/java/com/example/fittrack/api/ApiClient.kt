package com.example.fittrack.api

import android.util.Log
import com.example.fittrack.api.Request.RegisterUserResponse
import com.example.fittrack.api.Request.UserRegistrationRequest
import com.example.fittrack.entity.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient  {
    private const val BASE_URL = "http://10.0.2.2:8000"

    private val retrofitService: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }
    suspend fun registerUser(user: UserRegistrationRequest): RegisterUserResponse {
        val response = retrofitService.registerUser(user)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Registration failed: ${response.errorBody()?.string()}")
        }
    }
    suspend fun insertTargetLocation(userId: Int, testLocation: Request.TargetLocationRequest): Any {
        val response = retrofitService.insertTargetLocation(userId,testLocation)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Registration failed: ${response.errorBody()?.string()}")
        }
    }

    // Nueva función para insertar rutina
    suspend fun insertRoutine(routine: RoutineEntity, userId: Int): RoutineEntity {
        val response = retrofitService.insertRoutine(userId, routine)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Insert routine failed: ${response.errorBody()?.string()}")
        }
    }

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

    suspend fun insertExerciseLog(testExerciseLog: ExerciseLogEntity, userId: Int): Any {
        val response = retrofitService.insertExerciseLog(userId, testExerciseLog)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Insert routine failed: ${response.errorBody()?.string()}")
        }
    }

    suspend   fun insertNote(testNote: NoteEntity, userId: Int): Any {
        val response = retrofitService.insertNote(userId, testNote)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Insert routine failed: ${response.errorBody()?.string()}")
        }
    }
    suspend fun getUser(userEmail: String): Request.UserByEmailResponse? {
        return try {
            val response = retrofitService.getUserByEmail(userEmail)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("API_CLIENT", "Error al obtener usuario: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("API_CLIENT", "Excepción al obtener usuario: ${e.message}")
            null
        }
    }
    private fun getRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }
     suspend fun getExercises(): List<ExerciseEntity> {
        val response = retrofitService.getExercises()
        return response.body().orEmpty()
   }
     suspend fun getTargetLocations(): List<TargetLocationEntity> {
        val response = retrofitService.getTargetLocations()
        return response.body().orEmpty()
    }
     suspend fun getRoutines(userId: Int): List<RoutineEntity> {
        val response = retrofitService.getRoutines(userId)
        return response.body().orEmpty()
    }
     suspend fun getNotes(userId: Int): List<NoteEntity> {
        val response = retrofitService.getNotes(userId)
        return response.body().orEmpty()
    }
     suspend fun getExerciseLogs(userId: Int): List<ExerciseLogEntity> {
        val response = retrofitService.getExerciseLogs(userId)
        return response.body().orEmpty()
    }




}
