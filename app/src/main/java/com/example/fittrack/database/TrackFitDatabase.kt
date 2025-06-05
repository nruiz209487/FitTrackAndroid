package com.example.fittrack.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fittrack.entity.ExerciseEntity
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.entity.TargetLocationEntity
import com.example.fittrack.entity.UserEntity
import com.example.fittrack.type_converters.LatLngConverter
import com.example.trackfit.database.TrackFitDao

@Database(
    entities = [ExerciseLogEntity::class , NoteEntity::class , RoutineEntity::class , ExerciseEntity::class , UserEntity::class, TargetLocationEntity::class],
    version = 10,
    exportSchema = true
)
@TypeConverters(LatLngConverter::class)
abstract class TrackFitDatabase : RoomDatabase() {
    abstract fun trackFitDao(): TrackFitDao
}

