package com.example.fittrack.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String? = null,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "streak_days")
    var streakDays: Int? = null,
    @ColumnInfo(name = "profile_image")
    val profileImage: String? = null,

)