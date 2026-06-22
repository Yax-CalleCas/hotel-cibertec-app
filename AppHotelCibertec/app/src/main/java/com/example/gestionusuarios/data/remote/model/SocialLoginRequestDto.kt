package com.example.gestionusuarios.data.remote.model

data class SocialLoginRequestDto(
    val token: String,       // El token  de Facebook/Google
    val provider: String,    // "FACEBOOK" o "GOOGLE"
    val nombre: String,      // Necesario para el registro automático
    val apellido: String,    // Necesario para el registro automático
    val correo: String       // El email obtenido de la Graph API
)