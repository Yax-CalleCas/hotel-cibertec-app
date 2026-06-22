package com.example.gestionusuarios.data.remote.model

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(true, "Operación realizada correctamente", data)
        }

        fun <T> success(message: String, data: T): ApiResponse<T> {
            return ApiResponse(true, message, data)
        }

        fun <T> error(message: String): ApiResponse<T> {
            return ApiResponse(false, message, null)
        }
    }
}