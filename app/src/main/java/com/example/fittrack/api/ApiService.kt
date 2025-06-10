package com.example.fittrack.api

import com.example.fittrack.api.Request.RegisterUserResponse
import com.example.fittrack.api.Request.UserRegistrationRequest
import com.example.fittrack.entity.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @DELETE("/api/routines/{user_id}/{routine_id}")
    suspend fun deleteRoutine(
        @Path("user_id") userId: Int,
        @Path("routine_id") routineId: Int
    ): Response<Unit>

    @POST("/api/target-locations/{user_id}")
    suspend fun insertTargetLocation(
        @Path("user_id") userId: Int,
        @Body testLocation: Request.TargetLocationRequest
    ):Response<Unit>

    @DELETE("/api/target-locations/{user_id}/{id}")
    suspend fun deleteTargetLocation(
        @Path("user_id") userId: Int,
        @Path("id") noteId: Int
    ):Response<Unit>



    @DELETE("/api/notes/{user_id}/{id}")
    suspend fun deleteNote(
        @Path("user_id") userId: Int,
        @Path("id") noteId: Int
    ): Response<Unit>

    @DELETE("/api/exercise-log/{user_id}/{exercise_Id}")
    suspend fun deleteExeciseLog(
        @Path("user_id") userId: Int,
        @Path("exercise_Id") routineId: Int
    ): Response<Unit>

    @POST("/api/routines/{user_id}")
    suspend fun insertRoutine(
        @Path("user_id") userId: Int,
        @Body routine: RoutineEntity
    ): Response<RoutineEntity>

    @POST("/api/note/{user_id}")
    suspend fun insertNote(
        @Path("user_id") userId: Int,
        @Body note: NoteEntity
    ): Response<NoteEntity>

    @POST("/api/exercise-log/{user_id}")
    suspend fun insertExerciseLog(
        @Path("user_id") userId: Int,
        @Body log: ExerciseLogEntity
    ): Response<ExerciseLogEntity>

    @POST("/api/user/register")
    suspend fun registerUser(@Body user: UserRegistrationRequest): Response<RegisterUserResponse>

    @POST("/api/user/{email}")
    suspend fun getUserByEmail(
        @Path("email") email: String,
        @Body loginRequest: Request.LoginRequest
    ): Response<Request.UserByEmailSuccessResponse>

    @GET("/api/exercises")
    suspend fun getExercises(): Response<List<ExerciseEntity>>

    @PUT("/api/user/update/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body user: Request.UserUpdateRequest
    ): Response<Request.UserUpdateResponse>

    @GET("/api/target-locations/{user_id}")
    suspend fun getTargetLocations(@Path("user_id") userId: Int): Response<List<TargetLocationEntity>>

    @GET("/api/routines/{user_id}")
    suspend fun getRoutines(@Path("user_id") userId: Int): Response<List<RoutineEntity>>

    @GET("/api/notes/{user_id}")
    suspend fun getNotes(@Path("user_id") userId: Int): Response<List<NoteEntity>>

    @GET("/api/exercise-log/{user_id}")
    suspend fun getExerciseLogs(@Path("user_id") userId: Int): Response<List<ExerciseLogEntity>>
}
