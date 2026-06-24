package com.example.gestionusuarios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionusuarios.data.local.entity.VentaEntity
import com.example.gestionusuarios.data.remote.model.VentaDto
import com.example.gestionusuarios.data.repository.VentaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VentaViewModel(
    private val repository: VentaRepository
) : ViewModel() {

    // Fuente única de verdad (Room)
    val ventasLocales: StateFlow<List<VentaEntity>> = repository.obtenerVentasLocales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _ventasRemotas = MutableStateFlow<List<VentaDto>>(emptyList())
    val ventasRemotas: StateFlow<List<VentaDto>> = _ventasRemotas.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private var lastIdRecepcion: Int? = null

    // NUEVO: Método para sincronizar la capa local desde el repositorio
    fun sincronizarConServidor(idRecepcion: Int) {
        viewModelScope.launch {
            _loading.value = true
            repository.sincronizarVentasConServidor(idRecepcion)
            _loading.value = false
        }
    }

    fun registrarVenta(ventaDto: VentaDto, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val success = repository.registrarVenta(ventaDto)
                if (success) {
                    _mensaje.value = "Venta registrada correctamente"
                    onResult(true)
                } else {
                    _mensaje.value = "Error al registrar la venta en el servidor."
                    onResult(false)
                }
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.localizedMessage}"
                onResult(false)
            } finally {
                _loading.value = false
            }
        }
    }

    fun cargarVentasPorRecepcion(idRecepcion: Int) {
        lastIdRecepcion = idRecepcion
        viewModelScope.launch {
            _loading.value = true
            try {
                // Mantenemos tu método original
                _ventasRemotas.value = repository.listarPorRecepcion(idRecepcion)
                // Sincronizamos internamente con Room para mantener la persistencia
                repository.sincronizarVentasConServidor(idRecepcion)
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar consumos: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    val serviciosAplanados: StateFlow<List<ItemServicio>> = _ventasRemotas
        .map { ventas ->
            ventas.flatMap { venta ->
                (venta.detalles ?: emptyList()).map { detalle ->
                    ItemServicio(
                        nombreProducto = detalle.nombreProducto ?: "Sin nombre",
                        cantidad = detalle.cantidad ?: 0,
                        precioUnitario = detalle.precioUnitario ?: 0.0,
                        estadoVenta = venta.estado ?: "PENDIENTE",
                        subTotal = detalle.subTotal ?: 0.0
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    data class ItemServicio(
        val nombreProducto: String,
        val cantidad: Int,
        val precioUnitario: Double,
        val estadoVenta: String,
        val subTotal: Double
    )

    val consumosPendientes: StateFlow<List<VentaEntity>> = ventasLocales
        .map { list -> list.filter { it.estado == "PENDIENTE" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sumaConsumosPendientes: StateFlow<Double> = consumosPendientes
        .map { list -> list.sumOf { it.total } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun limpiarMensaje() { _mensaje.value = null }
}