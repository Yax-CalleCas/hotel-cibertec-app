package com.example.gestionusuarios.ui.viewmodel

import LoginResponseDto
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionusuarios.data.local.SessionManager
import com.example.gestionusuarios.data.local.mapper.toDomain
import com.example.gestionusuarios.data.repository.PersonRepository
import com.example.gestionusuarios.data.remote.model.Persona
import com.example.gestionusuarios.data.remote.model.TipoPersonaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class PersonaViewModel(
    private val repository: PersonRepository,
    private val sessionManager: SessionManager
) : ViewModel() {



    private val _personas = MutableStateFlow<List<Persona>>(emptyList())
    val personas = _personas.asStateFlow()

    private val _clientes = MutableStateFlow<List<Persona>>(emptyList())
    val clientes = _clientes.asStateFlow()

    private val _tiposPersona = MutableStateFlow<List<TipoPersonaDto>>(emptyList())
    val tiposPersona = _tiposPersona.asStateFlow()

    private val _nombreUsuario = MutableStateFlow("Usuario")
    val nombreUsuario = _nombreUsuario.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _personaSeleccionada = MutableStateFlow<Persona?>(null)
    val personaSeleccionada = _personaSeleccionada.asStateFlow()





    fun cargarTiposPersona() {
        viewModelScope.launch {
            try {
                val response = repository.listarTiposPersona()
                if (response.success) {
                    _tiposPersona.value = response.data ?: emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar tipos"
            }
        }
    }

    fun buscarPersona(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.buscarPersona(id)

                if (response.success) {
                    _personaSeleccionada.value = response.data
                } else {
                    _errorMessage.value = response.message
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error al buscar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun actualizarPersona(id: Int, persona: Persona, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.actualizarPersona(id, persona)
                if (response.success) {
                    cargarPersonas() // Refrescar lista
                    onResult(true)
                } else {
                    _errorMessage.value = response.message
                    onResult(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun actualizarListas(listaCompleta: List<Persona>) {
        _personas.value = listaCompleta
        _clientes.value = listaCompleta.filter { it.idTipoPersona == 3 }
    }

    fun cargarPersonas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.listarPersonas()
                if (response.success && response.data != null) {
                    actualizarListas(response.data)
                } else {
                    loadFromLocal()
                    _errorMessage.value = response.message ?: "Error al conectar"
                }
            } catch (e: Exception) {
                loadFromLocal()
                _errorMessage.value = "Modo Offline"
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun setUsuarioLogueado(data: LoginResponseDto) {
        val nombreCompleto = "${data.nombre} ${data.apellido}".trim()
        _nombreUsuario.value = nombreCompleto.ifBlank { "Usuario" }
    }

    // Dentro de PersonaViewModel
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    private suspend fun loadFromLocal() {
        val localData = repository.getPersonasLocal().map { it.toDomain() }
        actualizarListas(localData)
    }

    fun limpiarSeleccion() {
        _personaSeleccionada.value = null
    }
    fun registrarPersona(nuevaPersona: Persona, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.registrarPersona(nuevaPersona)
                if (response.success) {
                    cargarPersonas()
                    onResult(true)
                } else {
                    _errorMessage.value = response.message
                    onResult(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al registrar"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Centralizamos la gestión de carga y errores
    private fun performAction(
        action: suspend () -> Boolean,
        onSuccess: () -> Unit = { cargarPersonas() }
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (action()) onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error en la operación"
            } finally {
                _isLoading.value = false
            }
        }
    }

    init {
        cargarUsuarioDesdeSesion() // <--- LLAMA A ESTO AQUÍ
        cargarPersonas()
    }

    private fun cargarUsuarioDesdeSesion() {
        val nombre = sessionManager.getNombre() // Debes crear este método en SessionManager
        if (!nombre.isNullOrBlank()) {
            _nombreUsuario.value = nombre
        }
    }
    // Ejemplo de cómo quedaría eliminarPersona ahora:
    fun eliminarPersona(id: Int, onResult: (Boolean) -> Unit) {
        performAction(
            action = {
                val res = repository.eliminarPersona(id)
                if(res.success) { onResult(true); true } else { onResult(false); false }
            }
        )
    }


}