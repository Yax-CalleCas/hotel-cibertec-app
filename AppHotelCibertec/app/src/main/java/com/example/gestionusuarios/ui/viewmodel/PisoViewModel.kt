package com.example.gestionusuarios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionusuarios.data.local.entity.PisoEntity
import com.example.gestionusuarios.data.repository.PisoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PisoViewModel(
    private val repository: PisoRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // Observa los datos locales (Room) directamente
    val pisos: StateFlow<List<PisoEntity>> = repository.pisos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        sincronizarPisos()
    }

    fun sincronizarPisos() {
        viewModelScope.launch {
            _loading.value = true
            repository.sincronizarPisos()
            _loading.value = false
        }
    }
}