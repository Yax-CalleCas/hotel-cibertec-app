package com.example.gestionusuarios.data.repository

import android.util.Log
import com.example.gestionusuarios.data.local.dao.HabitacionDao
import com.example.gestionusuarios.data.local.dao.RecepcionDao
import com.example.gestionusuarios.data.local.entity.RecepcionEntity
import com.example.gestionusuarios.data.remote.api.RecepcionService
import com.example.gestionusuarios.data.remote.model.RecepcionDto
import com.example.gestionusuarios.data.local.mapper.toEntity
import com.example.gestionusuarios.data.remote.model.Persona
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class RecepcionRepository(
    private val recepcionService: RecepcionService,
    private val recepcionDao: RecepcionDao,
    private val personRepository: PersonRepository,
    private val habitacionDao: HabitacionDao,
) {
    private val ESTADO_OCUPADO = 2
    private val ESTADO_DISPONIBLE = 1

    fun obtenerRecepcionActiva(idHabitacion: Int): Flow<RecepcionEntity?> {
        return recepcionDao.obtenerRecepcionActivaPorHabitacion(idHabitacion)
    }

    /**
     * Sincronización completa: Obtiene del servidor y actualiza Room.
     * Ideal para llamar al iniciar la app o mediante pull-to-refresh.
     */
    suspend fun refrescarRecepciones() {
        try {
            val response = recepcionService.listarRecepciones()
            if (response.isSuccessful && response.body()?.data != null) {
                val listaRemota = response.body()?.data?.map { it.toEntity() } ?: emptyList()
                recepcionDao.insertarLista(listaRemota)
            }
        } catch (e: Exception) {
            Log.e("REPO", "Error en refrescarRecepciones: ${e.message}")
        }
    }

    suspend fun registrarRecepcion(dto: RecepcionDto): Boolean {
        return try {
            val response = recepcionService.registrarRecepcion(dto)
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                recepcionDao.insertar(data.toEntity())
                habitacionDao.actualizarEstadoHabitacion(data.idHabitacion, ESTADO_OCUPADO)
                return true
            }
            false
        } catch (e: Exception) {
            Log.e("REPO_ERROR", "Error registrando: ${e.message}")
            false
        }
    }

    /**
     * Sincroniza todas las recepciones actuales desde el servidor.
     * Este es el método que llamarás desde el coroutineScope en HabitacionViewModel.
     */
    suspend fun sincronizarRecepciones() {
        try {
            val response = recepcionService.listarRecepciones()
            if (response.isSuccessful && response.body()?.data != null) {

                // Filtramos manteniendo solo donde estado sea true
                // Esto asegura que habitaciones con estado null o false (finalizadas) se ignoren
                val listaFiltrada = response.body()!!.data!!
                    .filter { it.estado == true }
                    .map { it.toEntity() }

                // Guardamos exclusivamente las recepciones activas en la BD
                recepcionDao.guardarSincronizacionCompleta(listaFiltrada)
            }
        } catch (e: Exception) {
            Log.e("REPO", "Error en sincronización: ${e.message}")
        }
    }
    suspend fun sincronizarRecepcionActiva(idHabitacion: Int) {
        try {
            val response = recepcionService.obtenerRecepcionActiva(idHabitacion)
            if (response.isSuccessful && response.body()?.data != null) {
                recepcionDao.insertar(response.body()!!.data!!.toEntity())
            }
        } catch (e: Exception) {
            Log.e("REPO", "Fallo sincronización activa: ${e.message}")
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
                recepcionDao.marcarComoFinalizada(idRecepcion)
                habitacionDao.actualizarEstadoHabitacion(idHabitacion, ESTADO_DISPONIBLE)
                return true
            }
            false
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error en registrarSalida: ${e.message}")
            false
        }
    }



    /**
     * Obtiene las reservas del cliente como un flujo reactivo (SSOT).
     */
    fun obtenerMisReservas(idCliente: Int): Flow<List<RecepcionEntity>> {
        return recepcionDao.obtenerMisReservas(idCliente)
    }

    // Dentro de RecepcionRepository
    suspend fun obtenerDatosCliente(idCliente: Int): Persona? {
        return try {
            val response = personRepository.buscarPersona(idCliente)
            if (response.success && response.data != null) {
                response.data
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("REPO", "Error buscando persona: ${e.message}")
            null
        }
    }



    // 3. Sincronizar (Actualizar la lista completa desde el servidor)
    suspend fun sincronizarMisReservas(idCliente: Int) {
        withContext(Dispatchers.IO) {
            try {
                val response = recepcionService.listarRecepciones()
                if (response.isSuccessful && response.body()?.data != null) {
                    val lista = response.body()!!.data!!.map { it.toEntity() }
                    recepcionDao.guardarSincronizacionCompleta(lista)
                }
            } catch (e: Exception) {
                // Manejo de error silencioso o log
            }
        }
    }
}