package com.example.gestionusuarios.data.remote.api

import com.example.gestionusuarios.data.remote.model.*
import retrofit2.http.*

interface LoginService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): ApiResponse<LoginResponseDto>

    @POST("api/auth/social")
    suspend fun loginSocial(@Body request: SocialLoginRequestDto): ApiResponse<LoginResponseDto>
}