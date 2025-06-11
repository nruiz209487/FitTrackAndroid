package com.example.fittrack.api

import com.example.fittrack.api.Request.RegisterUserResponse
import com.example.fittrack.api.Request.UserRegistrationRequest
import com.example.fittrack.entity.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface que define los endpoints disponibles en la API de FitTrack.
 * Usada por Retrofit para generar automáticamente las implementaciones HTTP.
 */
interface ApiService {

    /**
     * Elimina una rutina del usuario.
     */
    @DELETE("/api/routines/{user_id}/{routine_id}")
    suspend fun deleteRoutine(
        @Path("user_id") userId: Int,
        @Path("routine_id") routineId: Int
    ): Response<Unit>

    /**
     * Inserta una nueva localización objetivo para el usuario.
     */
    @POST("/api/target-locations/{user_id}")
    suspend fun insertTargetLocation(
        @Path("user_id") userId: Int,
        @Body testLocation: Request.TargetLocationRequest
    ): Response<Unit>

    /**
     * Elimina una localización objetivo del usuario.
     */
    @DELETE("/api/target-locations/{user_id}/{id}")
    suspend fun deleteTargetLocation(
        @Path("user_id") userId: Int,
        @Path("id") noteId: Int
    ): Response<Unit>

    /**
     * Elimina una nota del usuario.
     */
    @DELETE("/api/notes/{user_id}/{id}")
    suspend fun deleteNote(
        @Path("user_id") userId: Int,
        @Path("id") noteId: Int
    ): Response<Unit>

    /**
     * Elimina un registro de ejercicio del usuario.
     */
    @DELETE("/api/exercise-log/{user_id}/{exercise_Id}")
    suspend fun deleteExeciseLog(
        @Path("user_id") userId: Int,
        @Path("exercise_Id") routineId: Int
    ): Response<Unit>

    /**
     * Inserta una nueva rutina para el usuario.
     */
    @POST("/api/routines/{user_id}")
    suspend fun insertRoutine(
        @Path("user_id") userId: Int,
        @Body routine: RoutineEntity
    ): Response<RoutineEntity>

    /**
     * Inserta una nueva nota para el usuario.
     */
    @POST("/api/note/{user_id}")
    suspend fun insertNote(
        @Path("user_id") userId: Int,
        @Body note: NoteEntity
    ): Response<NoteEntity>

    /**
     * Inserta un nuevo registro de ejercicio para el usuario.
     */
    @POST("/api/exercise-log/{user_id}")
    suspend fun insertExerciseLog(
        @Path("user_id") userId: Int,
        @Body log: ExerciseLogEntity
    ): Response<ExerciseLogEntity>

    /**
     * Registra un nuevo usuario en la plataforma.
     */
    @POST("/api/user/register")
    suspend fun registerUser(@Body user: UserRegistrationRequest): Response<RegisterUserResponse>

    /**
     * Obtiene un usuario por su correo electrónico, validando con los datos de inicio de sesión.
     */
    @POST("/api/user/{email}")
    suspend fun getUserByEmail(
        @Path("email") email: String,
        @Body loginRequest: Request.LoginRequest
    ): Response<Request.UserByEmailSuccessResponse>

    /**
     * Obtiene todos los ejercicios disponibles en la base de datos.
     */
    @GET("/api/exercises")
    suspend fun getExercises(): Response<List<ExerciseEntity>>

    /**
     * Actualiza los datos de un usuario por su ID.
     */
    @PUT("/api/user/update/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body user: Request.UserUpdateRequest
    ): Response<Request.UserUpdateResponse>

    /**
     * Obtiene todas las localizaciones objetivo asociadas al usuario.
     */
    @GET("/api/target-locations/{user_id}")
    suspend fun getTargetLocations(@Path("user_id") userId: Int): Response<List<TargetLocationEntity>>

    /**
     * Obtiene todas las rutinas del usuario.
     */
    @GET("/api/routines/{user_id}")
    suspend fun getRoutines(@Path("user_id") userId: Int): Response<List<RoutineEntity>>

    /**
     * Obtiene todas las notas del usuario.
     */
    @GET("/api/notes/{user_id}")
    suspend fun getNotes(@Path("user_id") userId: Int): Response<List<NoteEntity>>

    /**
     * Obtiene todos los registros de ejercicios del usuario.
     */
    @GET("/api/exercise-log/{user_id}")
    suspend fun getExerciseLogs(@Path("user_id") userId: Int): Response<List<ExerciseLogEntity>>
}
