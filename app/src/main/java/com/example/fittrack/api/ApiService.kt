package com.example.fittrack.api
import com.example.fittrack.api.Request.RegisterUserResponse
import com.example.fittrack.api.Request.UserRegistrationRequest
import com.example.fittrack.entity.*

import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    @GET("/api/user/token/{id}")
    suspend fun getUserToken(@Path("id") userId: Int): Response<UserEntity>

    @GET("/api/exercises")
    suspend fun getExercises(): Response<List<ExerciseEntity>>
    @POST("/api/user/register")
    suspend fun registerUser(@Body user: UserRegistrationRequest): Response<RegisterUserResponse>
    @GET("/api/targetlocations")
    suspend fun getTargetLocations(): Response<List<TargetLocationEntity>>

    @GET("/api/users/{id}/routines")
    suspend fun getRoutines(@Path("id") userId: Int): Response<List<RoutineEntity>>

    @POST("/api/users/{id}/routines")
    suspend fun insertRoutine(
        @Path("id") userId: Int,
        @Body routine: RoutineEntity
    ): Response<RoutineEntity>

    @DELETE("/api/users/{id}/routines/{routine_id}")
    suspend fun deleteRoutine(
        @Path("id") userId: Int,
        @Path("routine_id") routineId: Int
    ): Response<Void>

    @GET("/api/notes/user/{id}")
    suspend fun getNotes(@Path("id") userId: Int): Response<List<NoteEntity>>

    @POST("/api/logs/user/{id}/note")
    suspend fun insertNote(
        @Path("id") userId: Int,
        @Body note: NoteEntity
    ): Response<NoteEntity>

    @DELETE("/api/logs/user/{id}/note/{noteId}")
    suspend fun deleteNote(
        @Path("id") userId: Int,
        @Path("noteId") noteId: Int
    ): Response<Void>

    @GET("/api/logs/user/{id}")
    suspend fun getExerciseLogs(@Path("id") userId: Int): Response<List<ExerciseLogEntity>>

    @POST("/api/logs/user/{id}/insert")
    suspend fun insertExerciseLog(
        @Path("id") userId: Int,
        @Body log: ExerciseLogEntity
    ): Response<ExerciseLogEntity>

    @DELETE("/api/logs/user/{id}/exercise/{exerciseId}")
    suspend fun deleteExerciseLog(
        @Path("id") userId: Int,
        @Path("exerciseId") exerciseId: Int
    ): Response<Void>
}
