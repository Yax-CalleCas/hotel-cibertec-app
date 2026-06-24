package com.example.gestionusuarios.data.repository

import com.example.gestionusuarios.data.local.dao.PersonaDao
import com.example.gestionusuarios.data.local.entity.PersonaEntity
import com.example.gestionusuarios.data.local.mapper.toEntity // Importación del mapper
import com.example.gestionusuarios.data.remote.api.PersonaService
import com.example.gestionusuarios.data.remote.model.ApiResponse
import com.example.gestionusuarios.data.remote.model.Persona
import kotlinx.coroutines.flow.Flow

class PersonRepository(
    private val apiService: PersonaService,
    private val personaDao: PersonaDao
) {


    suspend fun listarPersonas() = apiService.listarPersonas().also { response ->
        if (response.success && response.data != null) {
            // Uso de  mapper aquí, manteniendo el repositorio limpio
            val entities = response.data.map { it.toEntity() }
            personaDao.insertAll(entities)
        }
    }


    suspend fun refrescarPersonas() {
        val response = apiService.listarPersonas()
        if (response.success && response.data != null) {
            val entities = response.data.map { it.toEntity() }
            personaDao.insertAll(entities)
        }
    }
    suspend fun getPersonasLocal() = personaDao.getAll()

    suspend fun buscarPersona(id: Int) = apiService.buscarPersona(id)

    suspend fun registrarPersona(persona: Persona): ApiResponse<Persona> {
        val response = apiService.registrarPersona(persona)
        if (response.success && response.data != null) {
            personaDao.insert(response.data.toEntity()) // Persistencia local post-API
        }
        return response
    }
    suspend fun actualizarPersona(id: Int, persona: Persona) = apiService.actualizarPersona(id, persona)

    suspend fun eliminarPersona(id: Int) = apiService.eliminarPersona(id).also { response ->
        if (response.success) {
            personaDao.deleteById(id)
        }
    }

    suspend fun listarTiposPersona() = apiService.listarTiposPersona()
}