package com.example.gestionusuarios.data.repository

import com.example.gestionusuarios.data.local.dao.EstadoHabitacionDao
import com.example.gestionusuarios.data.remote.api.EstadoHabitacionService
import com.example.gestionusuarios.data.remote.model.EstadoHabitacionDto
import com.example.gestionusuarios.data.local.entity.EstadoHabitacionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class EstadoHabitacionRepository(
    private val estadoHabitacionService: EstadoHabitacionService,
    private val estadoHabitacionDao: EstadoHabitacionDao
) {

    fun getEstadosLocales(): Flow<List<EstadoHabitacionEntity>> = estadoHabitacionDao.listarTodos()

    suspend fun sincronizarEstados(): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = estadoHabitacionService.listarTodos()
            if (response.success && response.data != null) {
                val entities = response.data.map { toEntity(it) }
                estadoHabitacionDao.refreshData(entities)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun toEntity(dto: EstadoHabitacionDto) = EstadoHabitacionEntity(
        idEstadoHabitacion = dto.idEstadoHabitacion ?: 0,
        descripcion = dto.descripcion ?: "Sin descripción",
        estado = dto.estado ?: true
    )
}