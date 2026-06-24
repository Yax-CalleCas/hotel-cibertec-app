package com.example.gestionusuarios.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.gestionusuarios.data.local.converters.Converters // IMPORTANTE: Importa tu clase


@Entity(
    tableName = "habitacion",
    indices = [Index(value = ["idEstadoHabitacion"])],
    foreignKeys = [
        ForeignKey(
            entity = EstadoHabitacionEntity::class,
            parentColumns = ["idEstadoHabitacion"],
            childColumns = ["idEstadoHabitacion"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(Converters::class)
data class HabitacionEntity(
    @PrimaryKey val idHabitacion: Int,
    val numero: String,
    val detalle: String?,
    val precio: Double,
    val idEstadoHabitacion: Int,        
    val descripcionEstado: String?,
    val urlsImagenes: List<String>?,
    val idPiso: Int,
    val idCategoria: Int,
    val estado: Boolean = true
)