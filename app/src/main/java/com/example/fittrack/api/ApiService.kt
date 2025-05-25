package com.example.fittrack.api

import com.example.fittrack.entity.ExerciseEntity
import com.example.fittrack.entity.UserEntity
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/user/{id}")
    fun getUser(): Response<UserEntity>
    @GET("exercises")
    suspend fun getExercises(): Response<List<ExerciseEntity>>

}