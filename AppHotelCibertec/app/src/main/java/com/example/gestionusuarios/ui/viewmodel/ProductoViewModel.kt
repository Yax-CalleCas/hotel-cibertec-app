package com.example.gestionusuarios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionusuarios.data.local.entity.ProductoEntity
import com.example.gestionusuarios.data.remote.model.Producto
import com.example.gestionusuarios.data.repository.ProductoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductoViewModel(private val repository: ProductoRepository) : ViewModel() {

    // 1. Fuente única de verdad: Observa Room. Al cambiar Room, la UI se actualiza sola.
    val productos: StateFlow<List<ProductoEntity>> = repository.getProductos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        refrescarDesdeApi()
    }

    fun refrescarDesdeApi() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.sincronizar() // Este método debe hacer el fetch de la API e insertarlo en Room
            _isLoading.value = false
        }
    }

    fun registrar(producto: Producto, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.registrar(producto)
            if (success) {
                repository.sincronizar() // Sincroniza local con remoto tras el éxito
                onResult(true)
            } else {
                _error.value = "Error al registrar producto"
                onResult(false)
            }
            _isLoading.value = false
        }
    }

    fun actualizar(id: Int, producto: Producto, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.actualizar(id, producto)
            if (success) {
                repository.sincronizar() // Sincroniza tras actualizar
                onResult(true)
            } else {
                _error.value = "Error al actualizar producto"
                onResult(false)
            }
            _isLoading.value = false
        }
    }

    fun eliminar(id: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.eliminar(id) // Llama al DELETE de la API
                repository.sincronizar() // Forzamos actualización de Room para que desaparezca de la UI
                onResult(true)
            } catch (e: Exception) {
                _error.value = "Error al eliminar: ${e.message}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getProductoById(id: Int): ProductoEntity? {
        return productos.value.find { it.idProducto == id }
    }
}