package com.example.gestionusuarios.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val idPersona: Int,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val usuario: String,
    val tipoPersona: String,
    val contrasena: String
)
