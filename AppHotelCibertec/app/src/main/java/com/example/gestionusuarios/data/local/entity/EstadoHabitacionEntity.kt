package com.example.gestionusuarios.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey




@Entity(tableName = "estado_habitacion")
data class EstadoHabitacionEntity(
    @PrimaryKey val idEstadoHabitacion: Int,
    val descripcion: String,
    val estado: Boolean
)
