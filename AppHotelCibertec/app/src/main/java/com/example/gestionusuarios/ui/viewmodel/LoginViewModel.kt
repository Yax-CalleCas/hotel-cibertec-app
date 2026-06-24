package com.example.gestionusuarios.ui.viewmodel

import android.util.Log
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

    private val TAG = "DEBUG_VM_COMPLETO"

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
                    sessionManager.saveToken(response.token)
                    sessionManager.saveUserData(response.nombre, response.apellido)

                    // GUARDADO DEL ID PERSONA
                    response.idPersona?.let { id ->
                        Log.d(TAG, "Guardando ID persona: $id")
                        sessionManager.saveIdPersona(id)
                    }

                    // GUARDADO DEL ROL
                    response.idTipoPersona?.let { rol ->
                        Log.d(TAG, "Guardando ROL en SessionManager: $rol")
                        sessionManager.saveUserRole(rol)
                    } ?: Log.e(TAG, "¡ERROR! idTipoPersona es NULL.")

                    _uiState.value = LoginUiState.Success(response)
                    sendEvent(UiEvent.ShowSnackbar("Bienvenido, ${response.nombre}"))
                }.onFailure { error ->
                    _uiState.value = LoginUiState.Idle
                    sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al iniciar sesión"))
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Idle
                sendEvent(UiEvent.ShowSnackbar("Error: ${e.message}"))
            }
        }
    }

    fun validarUsuarioEnBackend(token: String, nombre: String, apellido: String, email: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val result = withContext(ioDispatcher) {
                    repository.loginSocial(token, nombre, apellido, email)
                }

                result.onSuccess { response ->
                    sessionManager.saveToken(response.token)
                    sessionManager.saveUserData(response.nombre, response.apellido)

                    // GUARDADO DEL ID PERSONA EN LOGIN SOCIAL
                    response.idPersona?.let { id ->
                        Log.d(TAG, "Guardando ID persona social: $id")
                        sessionManager.saveIdPersona(id)
                    }

                    // GUARDADO DEL ROL
                    response.idTipoPersona?.let { rol ->
                        Log.d(TAG, "Guardando ROL social: $rol")
                        sessionManager.saveUserRole(rol)
                    } ?: Log.e(TAG, "¡ERROR! idTipoPersona es NULL en login social.")

                    _uiState.value = LoginUiState.Success(response)
                }.onFailure { error ->
                    _uiState.value = LoginUiState.Idle
                    sendEvent(UiEvent.ShowSnackbar("Error en login social"))
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Idle
            }
        }
    }

    private fun sendEvent(event: UiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }
}