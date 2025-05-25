package com.example.fittrack.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.entity.RoutineEntity
import com.example.trackfit.database.TrackFitDao

@Database(
    entities = [ExerciseLogEntity::class , NoteEntity::class , RoutineEntity::class],
    version = 8,
    exportSchema = true
)
abstract class TrackFitDatabase : RoomDatabase() {
    abstract fun trackFitDao(): TrackFitDao
}

