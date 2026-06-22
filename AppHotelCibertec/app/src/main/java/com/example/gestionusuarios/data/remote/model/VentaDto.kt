package com.example.gestionusuarios.data.remote.model


data class VentaDto(
val idVenta: Int?,
  val idRecepcion: Int?,
   val total: Double?,
   val estado: String?,
   val detalles: List<DetalleVentaDto>? 
)