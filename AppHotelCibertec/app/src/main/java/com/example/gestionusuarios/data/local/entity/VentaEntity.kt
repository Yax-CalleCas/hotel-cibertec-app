package com.example.gestionusuarios.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "ventas")
data class VentaEntity(
    @PrimaryKey val idVenta: Int,
    val idRecepcion: Int,
    val total: Double,
    val estado: String


)

