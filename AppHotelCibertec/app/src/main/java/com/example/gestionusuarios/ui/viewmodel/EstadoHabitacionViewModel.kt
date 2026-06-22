package com.example.gestionusuarios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionusuarios.data.local.entity.EstadoHabitacionEntity
import com.example.gestionusuarios.data.repository.EstadoHabitacionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EstadoHabitacionViewModel(
    private val repository: EstadoHabitacionRepository
) : ViewModel() {

    // Uso de stateIn: Convierte el Flow de Room en StateFlow reactivo automáticamente
    val estados: StateFlow<List<EstadoHabitacionEntity>> = repository.getEstadosLocales()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        sincronizar()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun sincronizar() {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.sincronizarEstados()
            if (!success) {
                _errorMessage.value = "Error al sincronizar el catálogo de estados"
            }
            _isLoading.value = false
        }
    }
}