package com.example.gestionusuarios.data.remote.model

data class PisoDto(
    val idPiso: Int,
    val descripcion: String,
    val estado: Boolean,
    val fechaCreacion: String?
)