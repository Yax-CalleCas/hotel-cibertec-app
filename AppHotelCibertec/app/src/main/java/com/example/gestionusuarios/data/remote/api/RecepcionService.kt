package com.example.gestionusuarios.data.remote.api

import com.example.gestionusuarios.data.remote.model.ApiResponse
import com.example.gestionusuarios.data.remote.model.RecepcionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RecepcionService {

    @GET("api/recepcion/habitacion-activa/{idHabitacion}")
    suspend fun obtenerRecepcionActiva(
        @Path("idHabitacion") idHabitacion: Int
    ): Response<ApiResponse<RecepcionDto>>

    @POST("api/recepcion/registrar")
    suspend fun registrarRecepcion(
        @Body recepcion: RecepcionDto
    ): Response<ApiResponse<RecepcionDto>>

    @POST("api/recepcion/registrar-salida")
    suspend fun registrarSalida(
        @Body payload: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse<String>>

    @GET("api/recepcion/listar")
    suspend fun listarRecepciones(): Response<ApiResponse<List<RecepcionDto>>>
}