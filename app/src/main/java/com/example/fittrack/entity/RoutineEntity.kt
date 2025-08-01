package com.example.fittrack.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Entidad Routina
 */
@Entity(tableName = "routine_table")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val imageUri: String = "",
    val exerciseIds: String = "",
    val userId: Int = 0
)
