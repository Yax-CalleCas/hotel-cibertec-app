package com.example.gestionusuarios.data.remote.api

import com.example.gestionusuarios.data.remote.model.ApiResponse
import com.example.gestionusuarios.data.remote.model.Producto
import retrofit2.http.*

interface ProductoService {
    @GET("api/producto/listar")
    suspend fun listar(): ApiResponse<List<Producto>>

    @POST("api/producto/registrar")
    suspend fun registrar(@Body producto: Producto): ApiResponse<Producto>

    @DELETE("api/producto/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Int): ApiResponse<Void>

    @PUT("api/producto/actualizar/{id}")
    suspend fun actualizar(@Path("id") id: Int, @Body producto: Producto): ApiResponse<Producto>

}