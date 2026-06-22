package com.example.gestionusuarios.data.remote.model

data class ApiUser(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val usuario: String,
    val tipoPersona: String,
    val contrasena: String
)

