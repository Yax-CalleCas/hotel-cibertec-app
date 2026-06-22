package com.example.gestionusuarios.data.repository

import android.util.Log
import com.example.gestionusuarios.data.local.dao.VentaDao
import com.example.gestionusuarios.data.local.entity.DetalleVentaEntity
import com.example.gestionusuarios.data.local.entity.VentaEntity
import com.example.gestionusuarios.data.remote.api.VentaService
import com.example.gestionusuarios.data.remote.model.VentaDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VentaRepository(
    private val ventaService: VentaService,
    private val ventaDao: VentaDao
) {

    suspend fun registrarVenta(ventaDto: VentaDto): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = ventaService.registrarVenta(ventaDto)

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                val idVentaGenerado = data?.idVenta ?: 0

                // Validación crítica: si el servidor devuelve ID 0, el registro local es inconsistente
                if (idVentaGenerado == 0) {
                    Log.e("VentaRepository", "El servidor devolvió un ID de venta inválido (0)")
                    return@withContext false
                }

                val ventaEntity = VentaEntity(
                    idVenta = idVentaGenerado,
                    idRecepcion = ventaDto.idRecepcion ?: 0,
                    total = ventaDto.total ?: 0.0,
                    estado = ventaDto.estado ?: "PENDIENTE"
                )

                val detallesEntities = ventaDto.detalles?.map { dto ->
                    DetalleVentaEntity(
                        idVenta = idVentaGenerado,
                        idProducto = dto.idProducto ?: 0,
                        nombreProducto = dto.nombreProducto ?: "Sin nombre",
                        cantidad = dto.cantidad ?: 0,
                        precioUnitario = dto.precioUnitario ?: 0.0,
                        subTotal = dto.subTotal ?: 0.0
                    )
                } ?: emptyList()

                // Intentamos persistir en Room
                ventaDao.guardarVentaConDetalles(ventaEntity, detallesEntities)
                true
            } else {
                // El servidor respondió, pero con éxito=false o error HTTP
                Log.e("VentaRepository", "Respuesta no exitosa: ${response.code()} - ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("VentaRepository", "Error crítico en registrarVenta: ${e.stackTraceToString()}")
            false
        }
    }

    // --- MÉTODOS DE LECTURA (Locales) ---

    fun obtenerVentasLocales(): Flow<List<VentaEntity>> = ventaDao.obtenerVentas()

    suspend fun listarPorRecepcion(idRecepcion: Int): List<VentaDto> = withContext(Dispatchers.IO) {
        try {
            val response = ventaService.listarPorRecepcion(idRecepcion)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                Log.e("VentaRepository", "Error listando: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("VentaRepository", "Error: ${e.message}")
            emptyList()
        }
    }

    suspend fun obtenerVentaLocal(id: Int): VentaEntity? = ventaDao.obtenerPorId(id)

    suspend fun eliminarVentaLocal(venta: VentaEntity) = ventaDao.eliminar(venta)


}