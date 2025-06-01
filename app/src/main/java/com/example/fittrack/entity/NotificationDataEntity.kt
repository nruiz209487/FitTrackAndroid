package com.example.fittrack.entity

import java.time.LocalDateTime

data class NotificationDataEntity(
    val id: Int,
    val title: String,
    val content: String,
    val dateTime: LocalDateTime
)