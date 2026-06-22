package com.example.gestionusuarios.data.remote.api

import com.example.gestionusuarios.data.remote.model.*
import retrofit2.http.*

interface PersonaService {
    @GET("api/persona/listar")
    suspend fun listarPersonas(): ApiResponse<List<Persona>>

    @GET("api/tipopersona/listar")
    suspend fun listarTiposPersona(): ApiResponse<List<TipoPersonaDto>>

    @GET("api/persona/buscar/{id}")
    suspend fun buscarPersona(@Path("id") id: Int): ApiResponse<Persona>

    @POST("api/persona/registrar")
    suspend fun registrarPersona(@Body persona: Persona): ApiResponse<Persona>

    @PUT("api/persona/actualizar/{id}")
    suspend fun actualizarPersona(@Path("id") id: Int, @Body persona: Persona): ApiResponse<Persona>

    @DELETE("api/persona/eliminar/{id}")
    suspend fun eliminarPersona(@Path("id") id: Int): ApiResponse<Void>
}