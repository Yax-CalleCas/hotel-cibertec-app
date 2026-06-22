package com.example.gestionusuarios.data.repository

import android.util.Log
import com.example.gestionusuarios.data.local.dao.HabitacionDao
import com.example.gestionusuarios.data.local.dao.RecepcionDao
import com.example.gestionusuarios.data.local.entity.RecepcionEntity
import com.example.gestionusuarios.data.remote.api.RecepcionService
import com.example.gestionusuarios.data.remote.model.RecepcionDto
import com.example.gestionusuarios.data.local.mapper.toEntity
import kotlinx.coroutines.flow.Flow

class RecepcionRepository(
    private val recepcionService: RecepcionService,
    private val recepcionDao: RecepcionDao,
    private val habitacionDao: HabitacionDao
) {
    private val ESTADO_OCUPADO = 2
    private val ESTADO_DISPONIBLE = 1

    val todasLasRecepciones: Flow<List<RecepcionEntity>> = recepcionDao.obtenerRecepciones()

    fun obtenerRecepcionActiva(idHabitacion: Int): Flow<RecepcionEntity?> {
        return recepcionDao.obtenerRecepcionActivaPorHabitacion(idHabitacion)
    }

    suspend fun sincronizarRecepcionActiva(idHabitacion: Int): RecepcionEntity? {
        return try {
            val response = recepcionService.obtenerRecepcionActiva(idHabitacion)
            response.body()?.data?.let { dto ->
                val entity = dto.toEntity()
                recepcionDao.insertar(entity)
                entity
            }
        } catch (e: Exception) {
            Log.e("REPO_ERROR", "Error sincronizando: ${e.message}")
            null
        }
    }

    suspend fun registrarRecepcion(dto: RecepcionDto): Boolean {
        return try {
            val response = recepcionService.registrarRecepcion(dto)
            val recepcionRegistrada = response.body()?.data

            if (response.isSuccessful && recepcionRegistrada != null) {
                recepcionDao.insertar(recepcionRegistrada.toEntity())
                habitacionDao.actualizarEstadoHabitacion(
                    recepcionRegistrada.idHabitacion,
                    ESTADO_OCUPADO
                )
                return true
            }
            false
        } catch (e: Exception) {
            Log.e("REPO_ERROR", "Error registrando recepción: ${e.message}")
            false
        }
    }


    suspend fun registrarSalida(
        idRecepcion: Int,
        idHabitacion: Int,
        penalidad: Double,
        total: Double
    ): Boolean {
        val payload = mapOf(
            "idRecepcion" to idRecepcion,
            "idHabitacion" to idHabitacion,
            "costoPenalidad" to penalidad,
            "totalPagado" to total
        )

        return try {
            val response = recepcionService.registrarSalida(payload)
            if (response.isSuccessful && response.body()?.success == true) {
                // 1. Finalizar en local
                recepcionDao.marcarComoFinalizada(idRecepcion)
                // 2. Liberar habitación a DISPONIBLE
                habitacionDao.actualizarEstadoHabitacion(idHabitacion, ESTADO_DISPONIBLE)
                true
            } else false
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error en registrarSalida: ${e.message}")
            false
        }
    }
}