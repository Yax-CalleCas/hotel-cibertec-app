package com.example.gestionusuarios.data.remote.api

import com.example.gestionusuarios.data.remote.model.ApiResponse
import com.example.gestionusuarios.data.remote.model.PisoDto
import retrofit2.Response
import retrofit2.http.GET

interface PisoService {
    @GET("api/piso/listar")
    suspend fun listarTodos(): Response<ApiResponse<List<PisoDto>>>
}