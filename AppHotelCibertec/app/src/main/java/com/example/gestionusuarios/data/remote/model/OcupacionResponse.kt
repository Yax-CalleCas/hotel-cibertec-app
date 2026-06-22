package com.example.gestionusuarios.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class OcupacionResponse(
    val numeroHabitacion: String,
    val descripcionCategoria: String,
    val vecesAlquilada: Long
)
@Serializable
data class HabitacionesResponse(val data: List<OcupacionResponse>)