package com.example.gestionusuarios.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "detalle_venta",
    foreignKeys = [
        ForeignKey(
            entity = VentaEntity::class,
            parentColumns = ["idVenta"],
            childColumns = ["idVenta"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("idVenta")]
)
data class DetalleVentaEntity(
    @PrimaryKey(autoGenerate = true) val idLocalDetalle: Int = 0,
    val idVenta: Int?,
    val idProducto: Int?,
    val nombreProducto: String?,
    val cantidad: Int,
    val precioUnitario: Double,
    val subTotal: Double
)