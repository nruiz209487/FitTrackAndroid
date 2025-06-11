
package com.example.fittrack.service.utils

/**
 * La fucnion de TokenManager es majear el tema de autenticacion de usario cada vez que se habre la app se crea un nuevo token para ello
 * el usario inicia sesion en el service por user y contrasenya lo y el tokenmanager usa los datos retornados eso me evita confilctos con la api ya que siempre se usa el id y token en la api
 */
object TokenManager {
    var token: String? = null
    var userId: Int? = null

    fun saveUserSession(token: String, userId: Int) {
        TokenManager.token = token
        TokenManager.userId = userId
    }
}