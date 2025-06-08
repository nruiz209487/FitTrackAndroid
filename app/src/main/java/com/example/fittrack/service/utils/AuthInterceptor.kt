package com.example.fittrack.service.utils

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

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