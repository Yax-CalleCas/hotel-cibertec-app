package com.example.gestionusuarios.data.local.mapper

import com.example.gestionusuarios.data.local.entity.DetalleVentaEntity
import com.example.gestionusuarios.data.local.entity.RecepcionEntity
import com.example.gestionusuarios.data.local.entity.VentaEntity
import com.example.gestionusuarios.data.remote.model.DetalleVentaDto
import com.example.gestionusuarios.data.remote.model.RecepcionDto
import com.example.gestionusuarios.data.remote.model.VentaDto

fun RecepcionDto.toEntity() = RecepcionEntity(
    idRecepcion = this.idRecepcion ?: 0,
    idCliente = this.idCliente,
    idHabitacion = this.idHabitacion,
    numero = this.numero,
    categoriaNombre = this.categoriaNombre,
    pisoNombre = this.pisoNombre,
    detalleHabitacion = this.detalleHabitacion,
    precioHabitacion = this.precioHabitacion ?: 0.0,
    estadoHabitacion = this.estadoHabitacion,
    tipoDocumento = this.tipoDocumento,
    documento = this.documento,
    nombre = this.nombre,
    apellido = this.apellido,
    correo = this.correo,
    precioInicial = this.precioInicial ?: 0.0,
    adelanto = this.adelanto ?: 0.0,
    precioRestante = this.precioRestante ?: 0.0,
    totalPagado = this.totalPagado ?: 0.0,
    costoPenalidad = this.costoPenalidad ?: 0.0,
    fechaEntrada = this.fechaEntrada,
    fechaSalida = this.fechaSalida,
    fechaSalidaConfirmacion = this.fechaSalidaConfirmacion,
    observacion = this.observacion,
    estado = this.estado ?: false
)

fun VentaDto.toEntity() = VentaEntity(
    idVenta = this.idVenta ?: 0,
    idRecepcion = this.idRecepcion ?: 0,
    total = this.total ?: 0.0,
    estado = this.estado ?: ""
)

fun DetalleVentaDto.toEntity(ventaId: Int) = DetalleVentaEntity(
    idVenta = ventaId,
    idProducto = this.idProducto,
    nombreProducto = this.nombreProducto,
    cantidad = this.cantidad ?: 0,
    precioUnitario = this.precioUnitario ?: 0.0,
    subTotal = this.subTotal ?: 0.0
)

fun crearDtoReserva(idCliente: Int, idHabitacion: Int): RecepcionDto {
    return RecepcionDto(
        idRecepcion = null,
        idCliente = idCliente,
        idHabitacion = idHabitacion,
        numero = null,
        categoriaNombre = null,
        pisoNombre = null,
        detalleHabitacion = null,
        precioHabitacion = null,
        estadoHabitacion = null,
        tipoDocumento = null,
        documento = null,
        nombre = null,
        apellido = null,
        correo = null,
        precioInicial = null,
        adelanto = null,
        precioRestante = null,
        totalPagado = null,
        costoPenalidad = null,
        fechaEntrada = null,
        fechaSalida = null,
        fechaSalidaConfirmacion = null,
        observacion = null,
        estado = false
    )
}