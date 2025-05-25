package com.example.fittrack.api

import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.entity.ExerciseEntity
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.entity.UserEntity

interface ApiRoutes {
    suspend fun getExercises(): List<ExerciseEntity>
    suspend fun getRoutines(): List<RoutineEntity>
    suspend fun getUser(): UserEntity
    suspend fun getExerciseLogs(): List<ExerciseLogEntity>
    suspend fun getNotes(): List<NoteEntity>

}
