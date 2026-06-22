package com.example.gestionusuarios.data.local.mapper

import com.example.gestionusuarios.data.local.entity.ProductoEntity
import com.example.gestionusuarios.data.remote.model.Producto

// Convierte de API (Producto) a Base de Datos (ProductoEntity)
fun Producto.toEntity(): ProductoEntity {
    return ProductoEntity(
        idProducto = this.idProducto ?: 0,
        nombre = this.nombre,
        detalle = this.detalle,
        precio = this.precio,
        cantidad = this.cantidad,
        estado = this.estado,
        imagenUrl = this.imagenUrl
    )
}

// Convierte de Base de Datos (ProductoEntity) a UI/Modelo (Producto)
fun ProductoEntity.toProducto(): Producto {
    return Producto(
        idProducto = this.idProducto,
        nombre = this.nombre,
        detalle = this.detalle,
        precio = this.precio,
        cantidad = this.cantidad,
        estado = this.estado,
        imagenUrl = this.imagenUrl
    )
}