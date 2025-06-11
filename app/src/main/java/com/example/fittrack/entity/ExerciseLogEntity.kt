package com.example.fittrack.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Entidad registro ejercicio
 */
@Entity(tableName = "exercise_log_table")
data class ExerciseLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val exerciseId: Int,
    val date: String,
    val weight: Float,
    val reps: Int,
    val userId: Int = 0
)
