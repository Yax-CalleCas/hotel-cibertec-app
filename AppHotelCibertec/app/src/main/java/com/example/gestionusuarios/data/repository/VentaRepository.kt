package com.example.gestionusuarios.data.repository

import android.util.Log
import com.example.gestionusuarios.data.local.dao.VentaDao
import com.example.gestionusuarios.data.local.entity.DetalleVentaEntity
import com.example.gestionusuarios.data.local.entity.VentaEntity
import com.example.gestionusuarios.data.remote.api.VentaService
import com.example.gestionusuarios.data.remote.model.VentaDto
import com.example.gestionusuarios.data.local.UiState.VentaConDetalles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VentaRepository(
    private val ventaService: VentaService,
    private val ventaDao: VentaDao
) {

    // --- MÉTODOS REQUERIDOS POR EL VIEWMODEL ---

    // Este es el que usa ventasLocales en tu ViewModel
    fun obtenerVentasLocales(): Flow<List<VentaEntity>> = ventaDao.obtenerVentas()

    // Este es el que usa cargarVentasPorRecepcion en tu ViewModel
    suspend fun listarPorRecepcion(idRecepcion: Int): List<VentaDto> = withContext(Dispatchers.IO) {
        try {
            val response = ventaService.listarPorRecepcion(idRecepcion)
            response.body()?.data ?: emptyList()
        } catch (e: Exception) {
            Log.e("VentaRepository", "Error en listarPorRecepcion: ${e.message}")
            emptyList()
        }
    }

    // --- LÓGICA DE SINCRONIZACIÓN OFFLINE-FIRST ---

    fun obtenerVentasPorRecepcion(idRecepcion: Int): Flow<List<VentaConDetalles>> {
        return ventaDao.obtenerVentasCompletasPorRecepcion(idRecepcion)
    }

    suspend fun sincronizarVentasConServidor(idRecepcion: Int) = withContext(Dispatchers.IO) {
        try {
            val response = ventaService.listarPorRecepcion(idRecepcion)
            if (response.isSuccessful && response.body()?.data != null) {
                val listaDtos = response.body()!!.data!!

                listaDtos.forEach { dto ->
                    val idVenta = dto.idVenta ?: 0
                    val ventaEntity = VentaEntity(
                        idVenta = idVenta,
                        idRecepcion = dto.idRecepcion ?: 0,
                        total = dto.total ?: 0.0,
                        estado = dto.estado ?: "PENDIENTE"
                    )
                    val detalles = dto.detalles?.map { d ->
                        DetalleVentaEntity(
                            idVenta = idVenta,
                            idProducto = d.idProducto,
                            nombreProducto = d.nombreProducto,
                            cantidad = d.cantidad ?: 0,
                            precioUnitario = d.precioUnitario ?: 0.0,
                            subTotal = d.subTotal ?: 0.0
                        )
                    } ?: emptyList()

                    ventaDao.guardarVentaConDetalles(ventaEntity, detalles)
                }
            }
        } catch (e: Exception) {
            Log.e("VentaRepository", "Fallo sincronización: ${e.message}")
        }
    }

    suspend fun registrarVenta(ventaDto: VentaDto): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = ventaService.registrarVenta(ventaDto)

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: return@withContext false
                val idVenta = data.idVenta ?: 0

                // 1. Crear entidad Venta
                val ventaEntity = VentaEntity(
                    idVenta = idVenta,
                    idRecepcion = data.idRecepcion ?: 0,
                    total = data.total ?: 0.0,
                    estado = data.estado ?: "PENDIENTE"
                )

                // 2. Mapear detalles asegurando el idVenta correcto
                val detallesEntities = data.detalles?.map { dto ->
                    DetalleVentaEntity(
                        idVenta = idVenta, // Este es el ID clave que debe coincidir
                        idProducto = dto.idProducto,
                        nombreProducto = dto.nombreProducto ?: "Sin nombre",
                        cantidad = dto.cantidad ?: 0,
                        precioUnitario = dto.precioUnitario ?: 0.0,
                        subTotal = dto.subTotal ?: 0.0
                    )
                } ?: emptyList()

                // 3. Ejecutar la transacción atómica definida en tu DAO
                ventaDao.guardarVentaConDetalles(ventaEntity, detallesEntities)

                return@withContext true
            } else {
                Log.e("VentaRepository", "API Error: ${response.code()}")
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e("VentaRepository", "Error crítico en registrarVenta", e)
            return@withContext false
        }
    }

    suspend fun eliminarVentaLocal(venta: VentaEntity) = ventaDao.eliminar(venta)
}