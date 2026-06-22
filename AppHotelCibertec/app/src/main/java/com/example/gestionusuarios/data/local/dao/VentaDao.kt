package com.example.gestionusuarios.data.local.dao

import androidx.room.*
import com.example.gestionusuarios.data.local.UiState.VentaConDetalles
import com.example.gestionusuarios.data.local.entity.DetalleVentaEntity
import com.example.gestionusuarios.data.local.entity.VentaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {


    @Query("SELECT * FROM ventas ORDER BY idVenta DESC")
    fun obtenerVentas(): Flow<List<VentaEntity>>

    @Query("SELECT * FROM ventas WHERE idVenta = :id")
    suspend fun obtenerPorId(id: Int): VentaEntity?

    @Query("SELECT * FROM ventas WHERE idRecepcion = :idRecepcion ORDER BY idVenta DESC")
    fun obtenerVentasPorRecepcion(idRecepcion: Int): Flow<List<VentaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(venta: VentaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarDetalles(detalles: List<DetalleVentaEntity>)


    @Transaction
    suspend fun guardarVentaConDetalles(venta: VentaEntity, detalles: List<DetalleVentaEntity>) {
        // 1. Insertamos la venta con el ID real del servidor
        insertar(venta)

        // 2. Usamos directamente el idVenta de la entidad para los detalles
        val detallesConId = detalles.map { it.copy(idVenta = venta.idVenta) }
        insertarDetalles(detallesConId)
    }
    // --- Consultas Relacionales (Venta + Detalles) ---
    @Transaction
    @Query("SELECT * FROM ventas WHERE idVenta = :id")
    suspend fun obtenerVentaCompleta(id: Int): VentaConDetalles?

    @Transaction
    @Query("SELECT * FROM ventas WHERE idRecepcion = :idRecepcion")
    fun obtenerVentasCompletasPorRecepcion(idRecepcion: Int): Flow<List<VentaConDetalles>>

    // --- Operaciones de Borrado ---
    @Delete
    suspend fun eliminar(venta: VentaEntity)

    @Query("DELETE FROM ventas")
    suspend fun eliminarTodo()
}