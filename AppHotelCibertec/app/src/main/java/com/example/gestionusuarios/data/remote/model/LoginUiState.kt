package com.example.gestionusuarios.data.remote.model

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val data: LoginResponseDto) : LoginUiState()
}