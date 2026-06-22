package com.example.gestionusuarios.data.local.UiState

import androidx.room.Embedded
import androidx.room.Relation
import com.example.gestionusuarios.data.local.entity.DetalleVentaEntity
import com.example.gestionusuarios.data.local.entity.VentaEntity

data class VentaConDetalles(
    @Embedded val venta: VentaEntity,
    @Relation(
        parentColumn = "idVenta",
        entityColumn = "idVenta"
    )
    val detalles: List<DetalleVentaEntity>
)