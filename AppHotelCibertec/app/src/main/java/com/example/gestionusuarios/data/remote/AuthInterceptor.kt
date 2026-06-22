package com.example.gestionusuarios.data.remote

import com.example.gestionusuarios.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Evitamos peticiones innecesarias si es una ruta pública
        val path = originalRequest.url.encodedPath
        val isAuthRoute = path.contains("/auth/", ignoreCase = true)

        val token = sessionManager.getToken()

        // Creamos una copia del request
        val requestBuilder = originalRequest.newBuilder()

        if (!isAuthRoute && !token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        // Manejo centralizado de sesión expirada
        if (response.code == 401) {
            sessionManager.clearSession()
        }

        return response
    }
}