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

/**
 * Base de datos lcoal SQL lite android se usa para aliviar la carga del servicio web
 */
@Database(
    entities = [ExerciseLogEntity::class , NoteEntity::class , RoutineEntity::class , ExerciseEntity::class , UserEntity::class, TargetLocationEntity::class],
    version = 1,
    exportSchema = true
)
/**
 * Funcion que devuelve la db el type converter es para pasar de string a LatLng en TargetLocationEntity
 */
@TypeConverters(LatLngConverter::class)
abstract class TrackFitDatabase : RoomDatabase() {
    abstract fun trackFitDao(): TrackFitDao
}

