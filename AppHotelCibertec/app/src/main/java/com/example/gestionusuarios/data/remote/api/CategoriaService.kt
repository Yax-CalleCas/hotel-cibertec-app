package com.example.gestionusuarios.data.remote.api

import com.example.gestionusuarios.data.remote.model.ApiResponse
import com.example.gestionusuarios.data.remote.model.CategoriaDto
import retrofit2.http.GET

interface CategoriaService {
    @GET("api/categoria/listar")
    suspend fun listarTodos(): ApiResponse<List<CategoriaDto>>
}