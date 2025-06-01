package com.example.fittrack.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val header: String,
    val text: String,
    val timestamp: String,
    val user_id: String = ""
)
