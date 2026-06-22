package com.example.gestionusuarios.data.remote.api

import com.example.gestionusuarios.data.remote.model.ApiResponse
import com.example.gestionusuarios.data.remote.model.HabitacionDto

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HabitacionService {
    @GET("api/habitacion/listar")
    suspend fun listarTodos(): ApiResponse<List<HabitacionDto>>

    @GET("api/habitacion/buscar/{id}")
    suspend fun buscarPorId(@Path("id") id: Int): ApiResponse<HabitacionDto>

    @POST("api/habitacion/registrar")
    suspend fun registrar(@Body dto: HabitacionDto): ApiResponse<HabitacionDto>

    @PUT("api/habitacion/actualizar/{id}")
    suspend fun actualizar(@Path("id") id: Int, @Body dto: HabitacionDto): ApiResponse<HabitacionDto>

    @DELETE("api/habitacion/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Int): ApiResponse<Void>
}