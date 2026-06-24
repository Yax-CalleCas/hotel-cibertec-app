package com.example.gestionusuarios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionusuarios.data.local.entity.HabitacionEntity
import com.example.gestionusuarios.data.remote.model.HabitacionDto
import com.example.gestionusuarios.data.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class HabitacionViewModel(
    private val repository: HabitacionRepository,
    private val estadoRepository: EstadoHabitacionRepository,
    private val categoriaRepository: CategoriaRepository,
    private val recepcionRepository: RecepcionRepository
) : ViewModel() {
    // NUEVO: Estado para la habitación que se está editando
    private val _habitacionEdit = MutableStateFlow<HabitacionEntity?>(null)
    val habitacionEdit: StateFlow<HabitacionEntity?> = _habitacionEdit.asStateFlow()
    val categoriasMap: StateFlow<Map<Int, String>> = categoriaRepository.getCategoriasLocales()
        .map { lista -> lista.associate { it.idCategoria to it.descripcion } }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val habitaciones: StateFlow<List<HabitacionEntity>> = repository.getHabitacionesLocales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val estados = estadoRepository.getEstadosLocales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Optimización: Solo sincronizamos al iniciar si es estrictamente necesario
        sincronizar(esInicial = true)
    }


    fun clearError() {
        _errorMessage.value = null
    }

    fun sincronizar(esInicial: Boolean = false) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                coroutineScope {
                    val s1 = async { repository.sincronizarHabitaciones() }
                    val s2 = async { estadoRepository.sincronizarEstados() }
                    val s3 = async { categoriaRepository.sincronizarCategorias() }
                    val s4 =
                        async { recepcionRepository.sincronizarRecepciones() } // <-- Sincroniza esto
                    awaitAll(s1, s2, s3, s4)
                }
            } catch (e: Exception) {
                if (!esInicial) _errorMessage.value = "Error al sincronizar datos."
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Método corregido para cargar la habitación
    fun cargarHabitacion(id: Int) {
        viewModelScope.launch {
            repository.getHabitacionById(id).collect { habitacion ->
                _habitacionEdit.value = habitacion
            }
        }
    }

    // Asegúrate de limpiar el estado cuando salgas del formulario
    fun limpiarEdicion() {
        _habitacionEdit.value = null
    }

    fun getHabitacionById(id: Int) = repository.getHabitacionById(id)

    fun registrar(dto: HabitacionDto, onResult: (Boolean) -> Unit) =
        ejecutarAccion({ repository.registrar(dto) }, onResult)

    fun actualizar(id: Int, dto: HabitacionDto, onResult: (Boolean) -> Unit) =
        ejecutarAccion({ repository.actualizar(id, dto) }, onResult)

    fun eliminar(id: Int, onResult: (Boolean) -> Unit) =
        ejecutarAccion({ repository.eliminar(id) }, onResult)

    private fun ejecutarAccion(action: suspend () -> Boolean, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = action()
                if (!success) _errorMessage.value = "La operación no pudo completarse."
                onResult(success)
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado."
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    //
// En tu HabitacionViewModel.kt

    // Filtramos las habitaciones que están disponibles (ejemplo: estado = 1 o disponible)
// Ajusta '1' al ID que represente "DISPONIBLE" en tu base de datos
    // Asumiendo que tu HabitacionEntity tiene un campo 'idEstadoHabitacion' o 'descripcionEstado'
    val habitacionesDisponibles: StateFlow<List<HabitacionEntity>> = habitaciones
        .map { lista ->
            // Filtramos donde la descripción sea "DISPONIBLE"
            // (o usa el ID si sabes cuál es el ID de disponible, ej: it.idEstadoHabitacion == 1)
            lista.filter {
                it.descripcionEstado?.equals("DISPONIBLE", ignoreCase = true) == true
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}