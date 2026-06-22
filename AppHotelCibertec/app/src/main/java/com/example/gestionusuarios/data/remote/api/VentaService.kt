package com.example.gestionusuarios.data.remote.api

import com.example.gestionusuarios.data.remote.model.ApiResponse
import com.example.gestionusuarios.data.remote.model.VentaDto
import retrofit2.Response
import retrofit2.http.*

interface VentaService {

    // Ruta: /api/venta
    @GET("api/venta")
    suspend fun listarVentas(): Response<ApiResponse<List<VentaDto>>>

    // Ruta: /api/venta/buscar/{id}
    @GET("api/venta/buscar/{id}")
    suspend fun obtenerVenta(@Path("id") id: Int): Response<ApiResponse<VentaDto>>

    // Ruta: /api/venta/recepcion/{idRecepcion}
    // ESTA ES LA QUE NECESITAS PARA TU PANTALLA DE SALIDA
    @GET("api/venta/recepcion/{idRecepcion}")
    suspend fun listarPorRecepcion(
        @Path("idRecepcion") idRecepcion: Int
    ): Response<ApiResponse<List<VentaDto>>>

    // Ruta: /api/venta
    @POST("api/venta")
    suspend fun registrarVenta(@Body venta: VentaDto): Response<ApiResponse<VentaDto>>

    // Ruta: /api/venta/{id}
    @PUT("api/venta/{id}")
    suspend fun actualizarVenta(
        @Path("id") id: Int,
        @Body venta: VentaDto
    ): Response<ApiResponse<VentaDto>>

    // Ruta: /api/venta/{id}
    @DELETE("api/venta/{id}")
    suspend fun eliminarVenta(@Path("id") id: Int): Response<ApiResponse<Void>>
}