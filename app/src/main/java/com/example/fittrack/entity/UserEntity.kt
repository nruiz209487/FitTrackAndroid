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
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val name: String? = null,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "streakDays")
    var streakDays: Int? = null,
    @ColumnInfo(name = "profileImage")
    val profileImage: String? = null,
    var lastStreakDay: String? = null,
    var password: String,
    @ColumnInfo(name = "gender")
    var gender: String? = null,
    @ColumnInfo(name = "height")
    var height: Double? = null,
    @ColumnInfo(name = "weight")
    var weight: Double? = null
)