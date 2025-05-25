package com.example.trackfit.database

import androidx.room.*
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.entity.RoutineEntity

@Dao
interface TrackFitDao {
    @Insert
    suspend fun insertExerciseLog(exerciseLog: ExerciseLogEntity)
    @Insert
    suspend fun insertExerciseLogs(exerciseLogs: List<ExerciseLogEntity>)
    @Query("SELECT * FROM exercise_log_table WHERE exerciseId = :exerciseId")
    suspend fun getExerciseLogsById(exerciseId: Int): List<ExerciseLogEntity>
    @Delete
    suspend fun deleteExerciseLogs(exerciseLogs: ExerciseLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)
    @Insert
    suspend fun insertNote(note: NoteEntity)
    @Delete
    suspend fun deleteNote(note: NoteEntity)
    @Query("SELECT * FROM note_table ")
    suspend fun getNotes(): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routine: List<RoutineEntity>)
    @Insert
    suspend fun insertRoutine(routine: RoutineEntity)
    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)
    @Query("SELECT * FROM routine_table ")
    suspend fun getRoutines(): List<RoutineEntity>


}