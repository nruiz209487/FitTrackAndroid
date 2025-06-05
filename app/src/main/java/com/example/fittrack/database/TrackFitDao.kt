package com.example.trackfit.database

import androidx.room.*
import com.example.fittrack.entity.ExerciseEntity
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.entity.TargetLocationEntity
import com.example.fittrack.entity.UserEntity

@Dao
interface TrackFitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    @Insert
    suspend fun insertExerciseLog(exerciseLog: ExerciseLogEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercies(routine: List<ExerciseEntity>)
    @Insert
    suspend fun insertExercise(routine: ExerciseEntity)
    @Delete
    suspend fun deleteExercise(routine: ExerciseEntity)
    @Query("SELECT * FROM exercise_table ")
    suspend fun getExercises(): List<ExerciseEntity>
    @Query("SELECT * FROM exercise_table WHERE id IN (:ids)")
    suspend fun getExercisesByIds(ids: List<Int>): List<ExerciseEntity>
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserEntity?
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?
    @Update
    suspend fun updateUser(user: UserEntity)
    @Delete
    suspend fun deleteUser(user: UserEntity)
    @Query("SELECT * FROM target_location_table ")
    suspend fun getTargetLocations(): List<TargetLocationEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTargetLocations(targetLocations: List<TargetLocationEntity>)
}