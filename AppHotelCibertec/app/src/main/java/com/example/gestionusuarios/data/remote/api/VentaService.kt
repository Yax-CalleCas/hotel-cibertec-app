package com.example.gestionusuarios.data.remote.api

import com.example.gestionusuarios.data.remote.model.ApiResponse
import com.example.gestionusuarios.data.remote.model.VentaDto
import retrofit2.Response
import retrofit2.http.*

interface VentaService {

    @GET("api/venta")
    suspend fun listarVentas(): Response<ApiResponse<List<VentaDto>>>

    @GET("api/venta/buscar/{id}")
    suspend fun obtenerVenta(@Path("id") id: Int): Response<ApiResponse<VentaDto>>

    @GET("api/venta/recepcion/{idRecepcion}")
    suspend fun listarPorRecepcion(
        @Path("idRecepcion") idRecepcion: Int
    ): Response<ApiResponse<List<VentaDto>>>

    @POST("api/venta")
    suspend fun registrarVenta(@Body venta: VentaDto): Response<ApiResponse<VentaDto>>

    @PUT("api/venta/{id}")
    suspend fun actualizarVenta(
        @Path("id") id: Int,
        @Body venta: VentaDto
    ): Response<ApiResponse<VentaDto>>

    @DELETE("api/venta/{id}")
    suspend fun eliminarVenta(@Path("id") id: Int): Response<ApiResponse<Void>>
}