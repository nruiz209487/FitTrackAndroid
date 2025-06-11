package com.example.fittrack.service.utils

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Clase AuthInterceptor sirve para manejar el tema de tokens en las soclitudes ya que la api usa tokens
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        //comprobacion de que el usario este autorizado
        return if (TokenManager.token != null) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer ${TokenManager.token}")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}