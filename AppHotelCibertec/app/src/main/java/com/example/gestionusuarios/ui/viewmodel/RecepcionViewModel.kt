package com.example.gestionusuarios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionusuarios.data.local.SessionManager
import com.example.gestionusuarios.data.local.entity.RecepcionEntity
import com.example.gestionusuarios.data.local.mapper.crearDtoReserva
import com.example.gestionusuarios.data.remote.model.Persona
import com.example.gestionusuarios.data.remote.model.RecepcionDto
import com.example.gestionusuarios.data.repository.RecepcionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecepcionViewModel(
    private val repository: RecepcionRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _clienteLogueado = MutableStateFlow<Persona?>(null)
    val clienteLogueado: StateFlow<Persona?> = _clienteLogueado.asStateFlow()
    private val _recepcion = MutableStateFlow<RecepcionEntity?>(null)
    val recepcion: StateFlow<RecepcionEntity?> = _recepcion.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private var observacionJob: Job? = null

    // metodo para la ventas desde catalogo
    private val _misReservas = MutableStateFlow<List<RecepcionEntity>>(emptyList())
    val misReservas: StateFlow<List<RecepcionEntity>> = _misReservas.asStateFlow()
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

    fun registrarSalida(
        idRecepcion: Int,
        idHabitacion: Int,
        penalidad: Double,
        total: Double,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true

            val success = repository.registrarSalida(idRecepcion, idHabitacion, penalidad, total)

            if (success) {
                _mensaje.value = "Salida registrada con éxito."
                // IMPORTANTE: Limpiamos la recepción activa localmente para que la UI se resetee
                _recepcion.value = null
            } else {
                _mensaje.value = "Error al procesar la salida."
            }

            onResult(success)
            _loading.value = false
        }
    }

    // En tu ViewModel
    fun cargarRecepcionParaSalida(idHabitacion: Int) {
        viewModelScope.launch {
            _loading.value = true
            // 1. Sincronizamos para traer los últimos datos del servidor
            repository.sincronizarRecepcionActiva(idHabitacion)

            // 2. Obtenemos el flujo actualizado de Room
            repository.obtenerRecepcionActiva(idHabitacion).collect { entity ->
                _recepcion.value = entity
                _loading.value = false
            }
        }
    }

    fun cargarMisReservas() {
        val idCliente = sessionManager.getIdPersona() // Asegúrate de tener este método
        viewModelScope.launch {
            // Sincronizamos primero con el servidor para tener lo último
            repository.sincronizarMisReservas(idCliente)

            // Observamos Room (esto se mantiene siempre actualizado)
            repository.obtenerMisReservas(idCliente).collect { lista ->
                _misReservas.value = lista
            }
        }
    }

    /**
     * Método para que el cliente haga una "Reserva" (que es una recepción en estado 0)
     */
    // Dentro de tu RecepcionViewModel
    fun solicitarReserva(idHabitacion: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true

            // Usamos el nuevo método del mapper
            val dto = crearDtoReserva(
                idCliente = sessionManager.getIdPersona() ?: 0,
                idHabitacion = idHabitacion
            )

            val success = repository.registrarRecepcion(dto)
            _mensaje.value = if (success) "Reserva solicitada." else "Error al solicitar."
            onResult(success)
            _loading.value = false
        }
    }

    /**
     * Nuevo método para reservar con selección de fechas.
     * Calcula noches y total antes de enviar al servidor.
     */
    fun solicitarReservaConFechas(
        idHabitacion: Int,
        fechaEntrada: String,
        fechaSalida: String,
        precioPorNoche: Double,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true

            // 1. Calcular noches (usando una función simple de diferencia de días)
            // Asegúrate de importar java.time.temporal.ChronoUnit o similar si usas LocalDate
            val noches = calcularDiferenciaDias(fechaEntrada, fechaSalida)
            val total = noches * precioPorNoche

            // 2. Crear DTO con los datos calculados
            val dto = RecepcionDto(
                idRecepcion = null,
                idCliente = sessionManager.getIdPersona() ?: 0,
                idHabitacion = idHabitacion,
                fechaEntrada = fechaEntrada,
                fechaSalida = fechaSalida,
                totalPagado = total,
                estado = false // Pendiente
            )

            // 3. Enviar al repositorio
            val success = repository.registrarRecepcion(dto)
            _mensaje.value = if (success) "Reserva solicitada. Total: S/.$total" else "Error al solicitar."
            onResult(success)
            _loading.value = false
        }
    }


    // Método auxiliar privado para calcular días
    private fun calcularDiferenciaDias(fechaInicio: String, fechaFin: String): Long {
        return try {
            val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE // "yyyy-MM-dd"
            val inicio = java.time.LocalDate.parse(fechaInicio, formatter)
            val fin = java.time.LocalDate.parse(fechaFin, formatter)
            java.time.temporal.ChronoUnit.DAYS.between(inicio, fin).coerceAtLeast(1)
        } catch (e: Exception) {
            1 // Por defecto, al menos 1 noche si falla el cálculo
        }
    }
    fun cargarDatosCliente() {
        val idCliente = sessionManager.getIdPersona()

        // Log para depuración
        android.util.Log.d("DEBUG_CLIENTE", "ID Obtenido del SessionManager: $idCliente")

        // Si el ID es nulo o 0, no tiene sentido consultar al repositorio
        if (idCliente == null || idCliente == 0) {
            android.util.Log.e("DEBUG_CLIENTE", "ID de cliente inválido: no se puede cargar la persona.")
            return
        }

        viewModelScope.launch {
            _loading.value = true
            try {
                // Buscamos en el repositorio
                val persona = repository.obtenerDatosCliente(idCliente)

                if (persona != null) {
                    android.util.Log.d("DEBUG_CLIENTE", "Persona obtenida exitosamente: ${persona.nombre}")
                    _clienteLogueado.value = persona
                } else {
                    android.util.Log.w("DEBUG_CLIENTE", "No se encontró el cliente con ID: $idCliente")
                }
            } catch (e: Exception) {
                android.util.Log.e("DEBUG_CLIENTE", "Error al cargar el cliente: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
    fun limpiarMensaje() { _mensaje.value = null }
}