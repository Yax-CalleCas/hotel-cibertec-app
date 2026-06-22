package com.example.gestionusuarios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionusuarios.data.local.SessionManager
import com.example.gestionusuarios.data.repository.LoginRepository
import com.example.gestionusuarios.data.remote.model.LoginUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val sessionManager: SessionManager,
    private val repository: LoginRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    // Login tradicional
    fun iniciarSesion(correo: String, clave: String) {
        if (correo.isBlank() || clave.isBlank()) {
            sendEvent(UiEvent.ShowSnackbar("Por favor, complete todos los campos"))
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val result = withContext(ioDispatcher) {
                    repository.login(correo.trim(), clave.trim())
                }

                result.onSuccess { response ->
                    // PERSISTENCIA: Guardamos los datos antes de pasar a Success
                    sessionManager.saveToken(response.token)
                    sessionManager.saveUserData(response.nombre, response.apellido)

                    _uiState.value = LoginUiState.Success(response)
                    sendEvent(UiEvent.ShowSnackbar("Bienvenido, ${response.nombre}"))
                }.onFailure { error ->
                    _uiState.value = LoginUiState.Idle
                    sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al iniciar sesión"))
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Idle
                sendEvent(UiEvent.ShowSnackbar("Error de conexión: ${e.message}"))
            }
        }
    }

    // Login Social
    fun validarUsuarioEnBackend(token: String, nombre: String, apellido: String, email: String) {
        if (token.isBlank() || email.isBlank()) {
            sendEvent(UiEvent.ShowSnackbar("Datos incompletos de la red social"))
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val result = withContext(ioDispatcher) {
                    repository.loginSocial(token, nombre, apellido, email)
                }

                result.onSuccess { response ->
                    // PERSISTENCIA: Guardamos los datos para el login social
                    sessionManager.saveToken(response.token)
                    sessionManager.saveUserData(response.nombre, response.apellido)

                    _uiState.value = LoginUiState.Success(response)
                    sendEvent(UiEvent.ShowSnackbar("Bienvenido, ${response.nombre}"))
                }.onFailure { error ->
                    _uiState.value = LoginUiState.Idle
                    sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al sincronizar con tu servidor"))
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Idle
                sendEvent(UiEvent.ShowSnackbar("Error: ${e.message}"))
            }
        }
    }

    private fun sendEvent(event: UiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }
}