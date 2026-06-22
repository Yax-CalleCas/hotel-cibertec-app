package com.example.gestionusuarios.data.repository

import com.example.gestionusuarios.data.local.dao.ProductoDao
import com.example.gestionusuarios.data.local.entity.ProductoEntity
import com.example.gestionusuarios.data.remote.api.ProductoService
import com.example.gestionusuarios.data.remote.model.Producto

class ProductoRepository(
    private val api: ProductoService,
    private val dao: ProductoDao
) {
    // La UI observa este Flow, que es la única fuente de verdad (Room)
    fun getProductos() = dao.obtenerProductosActivos()

    // Sincronización completa: Limpia y actualiza todo desde el servidor
    suspend fun sincronizar(): Boolean {
        return try {
            val response = api.listar()
            if (response.success && response.data != null) {
                val entities = response.data.map { it.toEntity() }
                dao.refreshData(entities)
                true
            } else false
        } catch (e: Exception) { false }
    }

    // CRUD: Registrar producto
    suspend fun registrar(producto: Producto): Boolean {
        return try {
            val response = api.registrar(producto)
            if (response.success) {
                // Sincronizamos todo para asegurar que el ID generado por el servidor sea consistente
                sincronizar()
                true
            } else false
        } catch (e: Exception) { false }
    }

    // CRUD: Actualizar producto
    suspend fun actualizar(id: Int, producto: Producto): Boolean {
        return try {
            val response = api.actualizar(id, producto)
            if (response.success) {
                sincronizar()
                true
            } else false
        } catch (e: Exception) { false }
    }

    // CRUD: Eliminar producto
    suspend fun eliminar(id: Int): Boolean {
        return try {
            val response = api.eliminar(id)
            if (response.success) {
                dao.eliminarPorId(id)
                true
            } else false
        } catch (e: Exception) { false }
    }

    // Mapper privado para mantener la consistencia interna
    private fun Producto.toEntity() = ProductoEntity(
        idProducto = idProducto ?: 0,
        nombre = nombre,
        detalle = detalle,
        precio = precio,
        cantidad = cantidad,
        estado = estado,
        imagenUrl = imagenUrl
    )
}