package com.example.fittrack.api

class Request {
    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class UserUpdateRequest(
        val email: String,
        val password: String,
        val password_confirmation: String,
        val name: String?,
        val gender: String?,
        val height: Double?,
        val weight: Double?,
        val streakDays: Int? = null,
        val lastStreakDay: String? = null,
        val profileImage: String? = null
    )

    data class UserUpdateResponse(
        val success: Boolean,
        val message: String,
        val data: UserData?
    )

    data class UserDetails(
        val id: Int,
        val email: String,
        val name: String,
        val streakDays: Int?,
        val profileImage: String?,
        var lastStreakDay: String,
        val gender: String?,
        val height: Double?,
        val weight: Double?
    )

    data class UserByEmailSuccessResponse(
        val success: Boolean? = null,
        val message: String? = null,
        val token: String? = null,
        val user: UserDetails? = null
    )

    data class UserRegistrationRequest(
        val email: String,
        val password: String,
        val password_confirmation: String,
        val name: String?,
        val gender: String?,
        val height: Double?,
        val weight: Double?,
        val calveApp: String? ="+dfx8gyAR##d3'f9G8Gfj@fj3f57as63s/1?"
    )

    data class RegisterUserResponse(
        val success: Boolean,
        val message: String,
        val data: UserData
    )

    data class UserData(
        val id: Int,
        val email: String,
        val name: String,
        val gender: String?,
        val height: Double?,
        val weight: Double?,
        val streakDays: Int?,
        val lastStreakDay: String?,
        val profileImage: String?,
        val token: String
    )

    data class TargetLocationRequest(
        val name: String,
        val position: String,
        val radiusMeters: Double
    )
}