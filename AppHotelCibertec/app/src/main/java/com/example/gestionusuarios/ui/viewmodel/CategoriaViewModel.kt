package com.example.gestionusuarios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionusuarios.data.local.entity.CategoriaEntity
import com.example.gestionusuarios.data.repository.CategoriaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoriaViewModel(
    private val repository: CategoriaRepository
) : ViewModel() {

    // Exponemos las categorías como un StateFlow que se mantiene activo mientras el ViewModel viva
    val categorias: StateFlow<List<CategoriaEntity>> = repository.getCategoriasLocales()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        sincronizar()
    }

    class CategoriaViewModel(
        private val repository: CategoriaRepository
    ) : ViewModel() {

        // El repositorio ya expone un Flow, lo convertimos a StateFlow
        val categorias: StateFlow<List<CategoriaEntity>> = repository.getCategoriasLocales()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        init {
            sincronizar()
        }

        fun sincronizar() {
            viewModelScope.launch {
                try {
                    repository.sincronizar()
                } catch (e: Exception) {
                    // Manejo de errores
                }
            }
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            try {
                repository.sincronizar()
            } catch (e: Exception) {
                // Manejo de errores silencioso o log
            }
        }
    }
}