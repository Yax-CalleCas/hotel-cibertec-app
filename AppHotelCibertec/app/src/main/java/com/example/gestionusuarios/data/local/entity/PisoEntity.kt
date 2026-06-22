package com.example.gestionusuarios.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pisos")
data class PisoEntity(
    @PrimaryKey val idPiso: Int,
    val descripcion: String,
    val estado: Boolean,
    val fechaCreacion: String?
)