package com.example.gestionusuarios.data.local.mapper

import com.example.gestionusuarios.data.local.entity.HabitacionEntity
import com.example.gestionusuarios.data.remote.model.HabitacionDto

fun HabitacionDto.toEntity(): HabitacionEntity {
    val id = this.idHabitacion ?: throw IllegalArgumentException("HabitacionDto con ID nulo")

    return HabitacionEntity(
        idHabitacion = id,
        numero = this.numero?.trim()?.takeIf { it.isNotEmpty() } ?: "S/N",
        detalle = this.detalle?.trim() ?: "",
        precio = this.precio ?: 0.0,
        idEstadoHabitacion = this.idEstadoHabitacion ?: 0,
        idPiso = this.idPiso ?: 1,
        idCategoria = this.idCategoria ?: 1,
        // CAMPOS NUEVOS NECESARIOS PARA EL FILTRADO:
        estado = this.estado ?: true,
        descripcionEstado = this.descripcionEstado ?: "DESCONOCIDO",
        urlsImagenes = this.urlsImagenes?.filterNotNull() ?: emptyList()
    )
}