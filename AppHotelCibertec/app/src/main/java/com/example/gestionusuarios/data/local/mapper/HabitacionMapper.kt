package com.example.gestionusuarios.data.local.mapper

import com.example.gestionusuarios.data.local.entity.HabitacionEntity
import com.example.gestionusuarios.data.remote.model.HabitacionDto

fun HabitacionDto.toEntity(): HabitacionEntity {
    // Si el ID es nulo, lanzamos una excepción controlada
    // que el repositorio capturará en su mapNotNull
    val id = this.idHabitacion ?: throw IllegalArgumentException("HabitacionDto con ID nulo")

    return HabitacionEntity(
        idHabitacion = id,
        numero = this.numero?.trim()?.takeIf { it.isNotEmpty() } ?: "S/N",
        detalle = this.detalle?.trim() ?: "",
        precio = this.precio ?: 0.0,
        idEstadoHabitacion = this.idEstadoHabitacion ?: 0,
        urlsImagenes = this.urlsImagenes?.filterNotNull() ?: emptyList(),
        idPiso = this.idPiso ?: 1,
        idCategoria = this.idCategoria ?: 1
    )
}