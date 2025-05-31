package com.example.fittrack.api

import com.example.fittrack.entity.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient : ApiRoutes {
    private const val BASE_URL = "https://f7d0-46-6-130-69.ngrok-free.app/"

    private val retrofitService: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
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

    override suspend fun getExercises(): List<ExerciseEntity> {
        val response = retrofitService.getExercises()
        return response.body().orEmpty()
    }

    override suspend fun getRoutines(userId: Int): List<RoutineEntity> {
        val response = retrofitService.getRoutines(userId)
        return response.body().orEmpty()
    }

    override suspend fun getUser(): UserEntity {
        return UserEntity(
            id = 1,
            name = "Lucas Trainer",
            email = "lucas@example.com",
            streakDays = 5,
            profileImage = "https://i.pravatar.cc/300?img=3"
        )
    }

    override suspend fun getNotes(userId: Int): List<NoteEntity> {
        val response = retrofitService.getNotes(userId)
        return response.body().orEmpty()
    }

    override suspend fun getExerciseLogs(userId: Int): List<ExerciseLogEntity> {
        val response = retrofitService.getExerciseLogs(userId)
        return response.body().orEmpty()
    }
}
