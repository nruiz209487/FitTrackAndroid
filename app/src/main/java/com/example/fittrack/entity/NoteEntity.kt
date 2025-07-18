package com.example.fittrack.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Entidad nota
 */
@Entity(tableName = "note_table")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val header: String,
    val text: String,
    val timestamp: String,
    val userId: Int = 0
)
