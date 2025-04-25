package com.example.fittrack.entity

data class SocialPostEntity(
    val id: Int = 0,
    val userName: String,
    val userAvatarUrl: String,
    val postText: String,
    val postImageUrl: String? = null,
    val timestamp: Long
)
