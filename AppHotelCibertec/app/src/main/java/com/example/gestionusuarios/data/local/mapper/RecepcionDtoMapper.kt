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
    // AQUÍ ESTABA EL POSIBLE ERROR: Si el backend envía false,
    // estabas forzando 'true' siempre.
    estado = this.estado ?: false
)

// RecepcionMapper.kt (o el archivo donde manejes tus mappers)

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