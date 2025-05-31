package com.example.fittrack.api

import com.example.fittrack.entity.*

interface ApiRoutes {
    suspend fun getExercises(): List<ExerciseEntity>
    suspend fun getRoutines(userId: Int): List<RoutineEntity>
    suspend fun getUser(): UserEntity
    suspend fun getNotes(userId: Int): List<NoteEntity>
    suspend fun getExerciseLogs(userId: Int): List<ExerciseLogEntity>
}
