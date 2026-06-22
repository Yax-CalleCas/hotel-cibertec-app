package com.example.gestionusuarios.data.remote.model

import com.example.gestionusuarios.data.local.converters.Converters
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class DashboardStatsDto(
    val habitacionesOcupadas: Long = 0,
    val habitacionesDisponibles: Long = 0,
    val productosBajoStock: Long = 0,
   val ingresosHoy: Double = 0.0
)