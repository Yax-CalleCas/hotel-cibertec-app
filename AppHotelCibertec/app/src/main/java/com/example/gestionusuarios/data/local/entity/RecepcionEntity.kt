package com.example.gestionusuarios.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "recepciones")
data class RecepcionEntity(
    @PrimaryKey val idRecepcion: Int,
    val idCliente: Int?,
    val idHabitacion: Int?,
    val numero: String?,
    val categoriaNombre: String?,
    val pisoNombre: String?,
    val detalleHabitacion: String?,
    val precioHabitacion: Double?,
    val estadoHabitacion: String?,
    val tipoDocumento: String?,
    val documento: String?,
    val nombre: String?,
    val apellido: String?,
    val correo: String?,
    val precioInicial: Double?,
    val adelanto: Double?,
    val precioRestante: Double?,
    val totalPagado: Double?,
    val costoPenalidad: Double?,
    val fechaEntrada: String?,
    val fechaSalida: String?,
    val fechaSalidaConfirmacion: String?,
    val observacion: String?,
    val estado: Boolean?
)