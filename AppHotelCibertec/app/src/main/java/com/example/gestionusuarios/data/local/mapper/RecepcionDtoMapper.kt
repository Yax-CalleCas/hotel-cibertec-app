package com.example.gestionusuarios.data.local.mapper

import com.example.gestionusuarios.data.local.entity.RecepcionEntity
import com.example.gestionusuarios.data.remote.model.RecepcionDto

fun RecepcionDto.toEntity() = RecepcionEntity(
    idRecepcion = this.idRecepcion ?: 0,
    idCliente = this.idCliente,
    idHabitacion = this.idHabitacion,
    numero = this.numero,
    categoriaNombre = this.categoriaNombre,
    pisoNombre = this.pisoNombre,
    detalleHabitacion = this.detalleHabitacion,
    precioHabitacion = this.precioHabitacion,
    estadoHabitacion = this.estadoHabitacion,
    tipoDocumento = this.tipoDocumento,
    documento = this.documento,
    nombre = this.nombre,
    apellido = this.apellido,
    correo = this.correo,
    precioInicial = this.precioInicial,
    adelanto = this.adelanto,
    precioRestante = this.precioRestante,
    totalPagado = this.totalPagado,
    costoPenalidad = this.costoPenalidad,
    fechaEntrada = this.fechaEntrada,
    fechaSalida = this.fechaSalida,
    fechaSalidaConfirmacion = this.fechaSalidaConfirmacion,
    observacion = this.observacion,
    estado = this.estado ?: true
)