package com.example.gestionusuarios.data.repository

import android.util.Log
import com.example.gestionusuarios.data.local.dao.HabitacionDao
import com.example.gestionusuarios.data.remote.api.HabitacionService
import com.example.gestionusuarios.data.remote.model.HabitacionDto
import com.example.gestionusuarios.data.local.entity.HabitacionEntity
import com.example.gestionusuarios.data.remote.model.ApiResponse
import com.example.gestionusuarios.data.local.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class HabitacionRepository(
    private val habitacionService: HabitacionService,
    private val habitacionDao: HabitacionDao
) {
    private val TAG = "HabitacionRepository"
    fun getHabitacionesLocales(): Flow<List<HabitacionEntity>> = habitacionDao.listarTodos()

    suspend fun sincronizarHabitaciones(): Boolean {
        return try {
            val response = habitacionService.listarTodos()
            if (response.success && response.data != null) {
                val entities = response.data.map { it.toEntity() }
                habitacionDao.syncHabitaciones(entities)
                true
            } else false
        } catch (e: Exception) {
            Log.e(TAG, "Sincronización fallida", e)
            false
        }
    }

    suspend fun registrar(dto: HabitacionDto): Boolean = ejecutarYPersistir {
        habitacionService.registrar(dto)
    }

    suspend fun actualizar(id: Int, dto: HabitacionDto): Boolean = ejecutarYPersistir {
        habitacionService.actualizar(id, dto)
    }

    suspend fun eliminar(id: Int): Boolean {
        return try {
            val response = habitacionService.eliminar(id)
            if (response.success) {
                habitacionDao.eliminarPorId(id)
                true
            } else false
        } catch (e: Exception) {
            Log.e(TAG, "Fallo en eliminación remota", e)
            false
        }
    }

    fun getHabitacionById(id: Int): Flow<HabitacionEntity?> = habitacionDao.buscarPorId(id)

    private suspend fun ejecutarYPersistir(call: suspend () -> ApiResponse<HabitacionDto>): Boolean {
        return try {
            val response = call()
            if (response.success && response.data != null) {
                habitacionDao.insertar(response.data.toEntity())
                true
            } else false
        } catch (e: Exception) {
            Log.e(TAG, "Error en persistencia local", e)
            false
        }
    }
}