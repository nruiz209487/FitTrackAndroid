package com.example.trackfit.database

import androidx.room.*
import com.example.fittrack.entity.ExerciseEntity
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.RoutineEntity

@Dao
interface TrackFitDao {
    @Insert
    suspend fun insertExercise(exercise: ExerciseEntity)
    @Insert
    suspend fun insertExerciseLog(exerciseLog: ExerciseLogEntity)
    @Insert
    suspend fun insertExerciseLogs(exerciseLogs: List<ExerciseLogEntity>)
    @Query("SELECT * FROM exercise_log_table WHERE exerciseId = :exerciseId")
    suspend fun getExerciseLogsById(exerciseId: Int): List<ExerciseLogEntity>
}