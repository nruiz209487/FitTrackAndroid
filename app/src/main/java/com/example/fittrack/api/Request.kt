package com.example.fittrack.api

class Request {
    data class UserByEmailResponse(
        val token: String,
        val user: UserDetails
    )

    data class UserDetails(
        val user_id: Int,
        val email: String,
        val name: String,
        val streak_days: Int?,
        val profile_image: String?
    )
    data class UserRegistrationRequest(
        val email: String,
        val password: String,
        val password_confirmation: String,
        val name: String?
    )

    data class RegisterUserResponse(
        val success: Boolean,
        val message: String,
        val data: UserData
    )

    data class UserData(
        val user_id: Int,
        val email: String,
        val name: String,
        val token: String
    )


}