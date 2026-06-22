package com.example.gestionusuarios.data.repository

import com.example.gestionusuarios.data.local.dao.PisoDao
import com.example.gestionusuarios.data.local.entity.PisoEntity
import com.example.gestionusuarios.data.remote.api.PisoService
import com.example.gestionusuarios.data.remote.model.PisoDto
import kotlinx.coroutines.flow.Flow

class PisoRepository(
    private val pisoService: PisoService,
    private val pisoDao: PisoDao
) {

    // La UI observará este flujo para mostrar la lista de pisos
    val pisos: Flow<List<PisoEntity>> = pisoDao.obtenerPisos()

    // --- SINCRONIZACIÓN (API -> ROOM) ---
    /**
     * Descarga los pisos desde el servidor y actualiza la base de datos local.
     * Retorna true si la sincronización fue exitosa.
     */
    suspend fun sincronizarPisos(): Boolean {
        return try {
            val response = pisoService.listarTodos()
            if (response.isSuccessful && response.body()?.success == true) {
                val listaDto = response.body()?.data ?: emptyList()

                // Convertir DTOs a Entities
                val entities = listaDto.map { it.toEntity() }

                // Actualizar base de datos local
                pisoDao.eliminarTodo()
                pisoDao.insertarTodas(entities)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}

// --- EXTENSION PARA MAPEADOR ---
/**
 * Convierte el objeto de Red (DTO) al objeto de Base de Datos (Entity).
 */
fun PisoDto.toEntity() = PisoEntity(
    idPiso = this.idPiso,
    descripcion = this.descripcion,
    estado = this.estado,
    fechaCreacion = this.fechaCreacion
)