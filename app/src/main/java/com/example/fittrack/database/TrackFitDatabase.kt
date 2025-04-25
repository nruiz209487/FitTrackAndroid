package com.example.fittrack.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fittrack.entity.ExerciseEntity
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.RoutineEntity
import com.example.trackfit.database.TrackFitDao

@Database(
    entities = [ExerciseEntity::class, RoutineEntity::class, ExerciseLogEntity::class],
    version = 3,
    exportSchema = true
)
abstract class TrackFitDatabase : RoomDatabase() {
    abstract fun trackFitDao(): TrackFitDao
}

