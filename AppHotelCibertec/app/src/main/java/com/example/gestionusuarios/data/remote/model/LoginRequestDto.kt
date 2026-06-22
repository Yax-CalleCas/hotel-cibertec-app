package com.example.gestionusuarios.data.remote.model

data class LoginRequestDto(
    val correo: String,
    val clave: String
)