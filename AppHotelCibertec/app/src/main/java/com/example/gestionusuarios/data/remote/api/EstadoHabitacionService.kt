package com.example.gestionusuarios.data.remote.api

import com.example.gestionusuarios.data.remote.model.ApiResponse
import com.example.gestionusuarios.data.remote.model.EstadoHabitacionDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path



interface EstadoHabitacionService {
    @GET("api/estadohabitacion/listar")
    suspend fun listarTodos(): ApiResponse<List<EstadoHabitacionDto>>

    @GET("api/estadohabitacion/buscar/{id}")
    suspend fun buscarPorId(@Path("id") id: Int): ApiResponse<EstadoHabitacionDto>

    @POST("api/estadohabitacion/registrar")
    suspend fun registrar(@Body dto: EstadoHabitacionDto): ApiResponse<EstadoHabitacionDto>

    @PUT("api/estadohabitacion/actualizar/{id}")
    suspend fun actualizar(@Path("id") id: Int, @Body dto: EstadoHabitacionDto): ApiResponse<EstadoHabitacionDto>

    @DELETE("api/estadohabitacion/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Int): ApiResponse<Void>
}
