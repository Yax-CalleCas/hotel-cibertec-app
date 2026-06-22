package com.example.gestionusuarios.data.repository

import com.example.gestionusuarios.data.local.dao.CategoriaDao
import com.example.gestionusuarios.data.local.entity.CategoriaEntity
import com.example.gestionusuarios.data.remote.api.CategoriaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoriaRepository(
    private val service: CategoriaService,
    private val dao: CategoriaDao
) {
    // Retorna un Flow de la base de datos local
    fun getCategoriasLocales() = dao.getCategorias()

    // Método principal de sincronización
    suspend fun sincronizar() = withContext(Dispatchers.IO) {
        try {
            val response = service.listarTodos()
            if (response.success && !response.data.isNullOrEmpty()) {
                val entities = response.data.map {
                    CategoriaEntity(it.idCategoria, it.descripcion, it.estado)
                }
                // Limpiamos y guardamos (o insertamos/actualizamos)
                dao.insertAll(entities)
            }
        } catch (e: Exception) {
            // Loguea el error o maneja la excepción según tu política de logs
            e.printStackTrace()
        }
    }

    // Delegamos el TODO a la función real sincronizar()
    suspend fun sincronizarCategorias() {
        sincronizar()
    }
}