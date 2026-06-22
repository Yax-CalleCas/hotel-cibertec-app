package com.example.gestionusuarios.data.remote.model

data class EstadoHabitacionDto(
    val idEstadoHabitacion: Int,
    val descripcion: String,
    val estado: Boolean? = true,
    val fechaCreacion: String? = null
)