package com.example.gestionusuarios.data.remote.model
data class CategoriaDto(
    val idCategoria: Int,
    val descripcion: String,
    val estado: Boolean,
    val fechaCreacion: String?
)