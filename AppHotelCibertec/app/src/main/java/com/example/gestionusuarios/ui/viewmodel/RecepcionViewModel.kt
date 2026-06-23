package com.example.gestionusuarios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionusuarios.data.local.entity.RecepcionEntity
import com.example.gestionusuarios.data.remote.model.RecepcionDto
import com.example.gestionusuarios.data.repository.RecepcionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecepcionViewModel(
    private val repository: RecepcionRepository
) : ViewModel() {

    private val _recepcion = MutableStateFlow<RecepcionEntity?>(null)
    val recepcion: StateFlow<RecepcionEntity?> = _recepcion.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private var observacionJob: Job? = null

    fun buscarRecepcionActiva(idHabitacion: Int) {
        observacionJob?.cancel()

        observacionJob = viewModelScope.launch {
            // El loading solo es necesario para la carga inicial,
            // no para las actualizaciones reactivas posteriores.
            _loading.value = true

            // Sincronización en background
            launch { repository.sincronizarRecepcionActiva(idHabitacion) }

            // Observación de la SSOT (Room)
            repository.obtenerRecepcionActiva(idHabitacion)
                .collect { entity ->
                    _recepcion.value = entity
                    _loading.value = false
                }
        }
    }

    fun registrarRecepcion(dto: RecepcionDto, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            val success = repository.registrarRecepcion(dto)
            _mensaje.value = if (success) "Reserva registrada con éxito." else "Error al registrar la reserva."
            onResult(success)
            _loading.value = false
        }
    }

    fun registrarSalida(idRecepcion: Int, idHabitacion: Int, penalidad: Double, total: Double, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            val success = repository.registrarSalida(idRecepcion, idHabitacion, penalidad, total)
            _mensaje.value = if (success) "Salida registrada." else "Error al procesar la salida."
            onResult(success)
            _loading.value = false
        }
    }

    fun limpiarMensaje() { _mensaje.value = null }
}