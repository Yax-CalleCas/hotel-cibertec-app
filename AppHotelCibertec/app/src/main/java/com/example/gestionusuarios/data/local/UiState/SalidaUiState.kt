package com.example.gestionusuarios.data.local.UiState

import com.example.gestionusuarios.data.remote.model.RecepcionDto
import com.example.gestionusuarios.data.remote.model.VentaDto
import java.math.BigDecimal

data class SalidaUiState(
    val recepcion: RecepcionDto? = null,
    val ventas: List<VentaDto> = emptyList(),
    val totalProductos: BigDecimal = BigDecimal.ZERO,
    val isLoading: Boolean = false
)