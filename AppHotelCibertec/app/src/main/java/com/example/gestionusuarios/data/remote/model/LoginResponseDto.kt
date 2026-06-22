package com.example.gestionusuarios.data.remote.model

data class LoginResponseDto(
    val idPersona: Int,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val tipoPersona: String,
    val token: String
)