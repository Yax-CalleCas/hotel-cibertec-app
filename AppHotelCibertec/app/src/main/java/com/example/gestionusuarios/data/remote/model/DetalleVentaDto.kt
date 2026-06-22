package com.example.gestionusuarios.data.remote.model

import com.google.gson.annotations.SerializedName


data class DetalleVentaDto(
    val idDetalleVenta: Int?,
    val idProducto: Int?,
    val nombreProducto: String?,
    val cantidad: Int?,
    val precioUnitario: Double?,
    val subTotal: Double?
)