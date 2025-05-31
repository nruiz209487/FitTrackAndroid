package com.example.fittrack.api

import com.example.fittrack.entity.ExerciseEntity
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.entity.UserEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("/api/user/{id}")
    suspend fun getUser(@Path("id") userId: Int): Response<UserEntity>

    @GET("/api/exercises")
    suspend fun getExercises(): Response<List<ExerciseEntity>>

    @GET("/api/routines/user/{id}")
    suspend fun getRoutines(@Path("id") userId: Int): Response<List<RoutineEntity>>

    @GET("/api/notes/user/{id}")
    suspend fun getNotes(@Path("id") userId: Int): Response<List<NoteEntity>>

    @GET("/api/logs/user/{id}")
    suspend fun getExerciseLogs(@Path("id") userId: Int): Response<List<ExerciseLogEntity>>
}
