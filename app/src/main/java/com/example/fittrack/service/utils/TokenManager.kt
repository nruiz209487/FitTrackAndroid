
package com.example.fittrack.service.utils


object TokenManager {
    var token: String? = null
    var userId: Int? = null

    fun saveUserSession(token: String, userId: Int) {
        TokenManager.token = token
        TokenManager.userId = userId
    }
}