package com.example.fittrack.api

/**
 * Contenedor de clases de solicitud y respuesta utilizadas en la comunicación con la API.
 */
class Request {

    /**
     * Datos necesarios para realizar el login del usuario.
     */
    data class LoginRequest(
        val email: String,
        val password: String
    )

    /**
     * Solicitud para actualizar los datos de un usuario existente.
     */
    data class UserUpdateRequest(
        val email: String,
        val password: String,
        val password_confirmation: String,
        val name: String?,
        val gender: String?,
        val height: Double?,
        val weight: Double?,
        val streakDays: Int? = null,         // Número de días consecutivos activos
        val lastStreakDay: String? = null,   // Última fecha en la que se registró actividad
        val profileImage: String? = null     // Imagen de perfil codificada en Base64 u URL
    )

    /**
     * Respuesta enviada después de actualizar un usuario.
     */
    data class UserUpdateResponse(
        val success: Boolean,
        val message: String,
        val data: UserData?
    )

    /**
     * Detalles del usuario recuperados al hacer login.
     */
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

    /**
     * Respuesta exitosa al recuperar un usuario por su email (usado en login).
     */
    data class UserByEmailSuccessResponse(
        val success: Boolean? = null,
        val message: String? = null,
        val token: String? = null,          // Token JWT para autenticación
        val user: UserDetails? = null       // Datos del usuario autenticado
    )

    /**
     * Solicitud para registrar un nuevo usuario en la plataforma.
     */
    data class UserRegistrationRequest(
        val email: String,
        val password: String,
        val password_confirmation: String,
        val name: String?,
        val gender: String?,
        val height: Double?,
        val weight: Double?,
        val calveApp: String? = "+dfx8gyAR##d3'f9G8Gfj@fj3f57as63s/1?" // Token de autorización de app (¿clave secreta?)
    )

    /**
     * Respuesta tras el registro de un usuario nuevo.
     */
    data class RegisterUserResponse(
        val success: Boolean,
        val message: String,
        val data: UserData
    )

    /**
     * Datos básicos del usuario que se devuelven en distintas respuestas.
     */
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
        val token: String                 // Token JWT de autenticación
    )

    /**
     * Solicitud para insertar una nueva localización objetivo (geofence) para el usuario.
     */
    data class TargetLocationRequest(
        val name: String,                // Nombre identificativo de la zona
        val position: String,            // Coordenadas en formato lat,long
        val radiusMeters: Double         // Radio de la zona en metros
    )
}
