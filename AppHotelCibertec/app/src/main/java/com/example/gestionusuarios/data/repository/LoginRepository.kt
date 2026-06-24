package com.example.gestionusuarios.data.repository

import LoginResponseDto
import com.example.gestionusuarios.data.remote.api.LoginService
import com.example.gestionusuarios.data.remote.model.LoginRequestDto
import com.example.gestionusuarios.data.remote.model.SocialLoginRequestDto
import com.example.gestionusuarios.data.remote.model.ApiResponse

class LoginRepository(
    private val loginService: LoginService
) {

    suspend fun loginSocial(token: String, nombre: String, apellido: String, correo: String): Result<LoginResponseDto> {
        return try {
            val response = loginService.loginSocial(
                SocialLoginRequestDto(token, "FACEBOOK", nombre, apellido, correo)
            )

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error en autenticación social"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(correo: String, clave: String): Result<LoginResponseDto> {
        return try {
            val response = loginService.login(LoginRequestDto(correo.trim(), clave))

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}