package com.example.fittrack.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fittrack.entity.ExerciseEntity
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.entity.UserEntity
import com.example.trackfit.database.TrackFitDao

@Database(
    entities = [ExerciseLogEntity::class , NoteEntity::class , RoutineEntity::class , ExerciseEntity::class , UserEntity::class],
    version = 11,
    exportSchema = true
)
abstract class TrackFitDatabase : RoomDatabase() {
    abstract fun trackFitDao(): TrackFitDao
}

