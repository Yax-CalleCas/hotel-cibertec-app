package com.example.gestionusuarios.data.remote.model

data class Producto(
    val idProducto: Int?,
    val nombre: String,
    val detalle: String?,
    val precio: Double,
    val cantidad: Int,
    val estado: Boolean,
    val imagenUrl: String?
)
