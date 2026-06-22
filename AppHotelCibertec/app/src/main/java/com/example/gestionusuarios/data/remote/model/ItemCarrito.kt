package com.example.gestionusuarios.data.remote.model

import java.math.BigDecimal

data class ItemCarrito(
    val idProducto: Int,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val subTotal: Double
)