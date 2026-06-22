package com.example.gestionusuarios.data.remote.model


data class TipoPersonaDto(
    val idTipoPersona: Int,
    val descripcion: String,
    val estado: Boolean,
    val fechaCreacion: String?
)