package com.example.fittrack.database

import androidx.room.*
import com.example.fittrack.entity.ExerciseEntity
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.entity.TargetLocationEntity
import com.example.fittrack.entity.UserEntity

/**
 * interface para manejar el CRUD de la base de datos local
 */
@Dao
interface TrackFitDao {

    // User
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    // Exercise Logs
    @Insert
    suspend fun insertExerciseLog(exerciseLog: ExerciseLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLogs(exerciseLogs: List<ExerciseLogEntity>)

    @Query("SELECT * FROM exercise_log_table WHERE exerciseId = :exerciseId")
    suspend fun getExerciseLogsById(exerciseId: Int): List<ExerciseLogEntity>

    @Delete
    suspend fun deleteExerciseLogs(exerciseLogs: ExerciseLogEntity)

    @Query("DELETE FROM exercise_log_table")
    suspend fun deleteAllExerciseLogs()

    // Notes
    @Insert
    suspend fun insertNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM note_table")
    suspend fun getNotes(): List<NoteEntity>

    @Query("DELETE FROM note_table")
    suspend fun deleteAllNotes()

    // Routines
    @Insert
    suspend fun insertRoutine(routine: RoutineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<RoutineEntity>)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Query("SELECT * FROM routine_table")
    suspend fun getRoutines(): List<RoutineEntity>

    @Query("DELETE FROM routine_table")
    suspend fun deleteAllRoutines()

    // Exercises
    @Insert
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Query("SELECT * FROM exercise_table")
    suspend fun getExercises(): List<ExerciseEntity>

    @Query("SELECT * FROM exercise_table WHERE id IN (:ids)")
    suspend fun getExercisesByIds(ids: List<Int>): List<ExerciseEntity>

    @Query("SELECT * FROM exercise_table WHERE id = :id LIMIT 1")
    suspend fun getExerciseById(id: Int): ExerciseEntity?


    // Target Locations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTargetLocation(targetLocation: TargetLocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTargetLocations(targetLocations: List<TargetLocationEntity>)

    @Delete
    suspend fun deleteTargetLocation(targetLocation: TargetLocationEntity)

    @Query("SELECT * FROM target_location_table")
    suspend fun getTargetLocations(): List<TargetLocationEntity>

    @Query("DELETE FROM target_location_table")
    suspend fun deleteAllTargetLocations()

    //Borra los registros de la base de datos al cerrar sesion
    @Transaction
    suspend fun clearAllData() {
        deleteAllUsers()
        deleteAllExerciseLogs()
        deleteAllNotes()
        deleteAllRoutines()
        deleteAllTargetLocations()
    }
}