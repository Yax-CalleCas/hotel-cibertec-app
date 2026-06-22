package com.example.gestionusuarios.data.repository

import android.util.Log
import com.example.gestionusuarios.data.local.SessionManager
import com.example.gestionusuarios.data.remote.api.LoginService
import com.example.gestionusuarios.data.remote.model.LoginRequestDto
import com.example.gestionusuarios.data.remote.model.LoginResponseDto
import com.example.gestionusuarios.data.remote.model.SocialLoginRequestDto

class LoginRepository(
    private val loginService: LoginService,
    private val sessionManager: SessionManager
) {


    // Método corregido para enviar todos los datos al nuevo endpoint /social
    suspend fun loginSocial(
        token: String,
        nombre: String,
        apellido: String,
        correo: String
    ): Result<LoginResponseDto> {

        return try {
            Log.d("SOCIAL_LOGIN", "Enviando datos sociales para: $correo")

            val response = loginService.loginSocial(
                SocialLoginRequestDto(
                    token = token,
                    provider = "FACEBOOK",
                    nombre = nombre,
                    apellido = apellido,
                    correo = correo
                )
            )

            if (response.success && response.data != null) {
                sessionManager.saveToken(response.data.token)
                Log.d("SOCIAL_LOGIN", "Token social guardado correctamente")
                Result.success(response.data)
            } else {
                Result.failure(
                    Exception(response.message.ifBlank { "Error en autenticación social" })
                )
            }
        } catch (e: Exception) {
            Log.e("SOCIAL_LOGIN_ERROR", "Error de conexión en login social", e)
            Result.failure(Exception(e.message ?: "Error de conexión con el servidor"))
        }
    }


    suspend fun login(correo: String, clave: String): Result<LoginResponseDto> {
        if (correo.isBlank() || clave.isBlank()) {
            return Result.failure(Exception("Ingrese correo y contraseña"))
        }

        return try {
            val response = loginService.login(LoginRequestDto(correo.trim(), clave))

            if (response.success && response.data != null) {
                sessionManager.saveToken(response.data.token)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message.ifBlank { "Credenciales incorrectas" }))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error de conexión"))
        }
    }
}