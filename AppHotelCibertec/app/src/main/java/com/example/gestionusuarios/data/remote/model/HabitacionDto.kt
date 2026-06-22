package com.example.gestionusuarios.data.remote.model

data class HabitacionDto(
    val idHabitacion: Int?,
    val numero: String?,
    val detalle: String?,
    val precio: Double?,
    val idEstadoHabitacion: Int?,
    val idPiso: Int?,
    val idCategoria: Int?,
    val estado: Boolean?,
    val descripcionEstado: String?,
    val urlsImagenes: List<String>?,
    val estadoHabitacion: EstadoHabitacionDto?
)