package com.example.gestionusuarios.ui.screens

import com.example.gestionusuarios.data.remote.model.TipoPersonaDto

data class RegistroUiState(
    val nombre: String = "",
    val apellido: String = "",
    val documento: String = "",
    val correo: String = "",
    val clave: String = "",
    val fotoUrl: String = "", // Campo para la URL de la foto
    val selectedTipo: TipoPersonaDto? = null,
    val isPasswordVisible: Boolean = false
)

