package com.example.gestionusuarios.data.remote.model


data class SalidaRequest(
    val idRecepcion: Int,
    val idHabitacion: Int,
    val costoPenalidad: Double,
    val totalPagado: Double
)