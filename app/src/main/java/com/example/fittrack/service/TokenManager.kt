
package com.example.fittrack.service

object TokenManager {
    var token: String? = null
    var userId: Int? = null

    fun saveUserSession(token: String, userId: Int) {
        this.token = token
        this.userId = userId
    }

    fun clearSession() {
        this.token = null
        this.userId = null
    }

    fun isLoggedIn(): Boolean {
        return token != null && userId != null
    }
}