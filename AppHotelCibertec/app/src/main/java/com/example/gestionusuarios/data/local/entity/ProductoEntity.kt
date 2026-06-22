package com.example.gestionusuarios.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producto")
data class ProductoEntity(
    @PrimaryKey val idProducto: Int,
    val nombre: String,
    val detalle: String?,
    val precio: Double,
    val cantidad: Int,
    val estado: Boolean,
    val imagenUrl: String?
)