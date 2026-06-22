package com.example.gestionusuarios.data.local.dao

import androidx.room.*
import com.example.gestionusuarios.data.local.entity.DetalleVentaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleVentaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(detalleVenta: DetalleVentaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(detalles: List<DetalleVentaEntity>)

    @Query("SELECT * FROM detalle_venta")
    fun getAllDetalles(): Flow<List<DetalleVentaEntity>>


     @Query("SELECT * FROM detalle_venta WHERE idVenta = :idVenta")
    fun getDetallesByVenta(idVenta: Int): Flow<List<DetalleVentaEntity>>

    @Update
    suspend fun update(detalleVenta: DetalleVentaEntity)

    @Delete
    suspend fun delete(detalleVenta: DetalleVentaEntity)

    @Query("DELETE FROM detalle_venta WHERE idVenta = :idVenta")
    suspend fun deleteByVenta(idVenta: Int)
}