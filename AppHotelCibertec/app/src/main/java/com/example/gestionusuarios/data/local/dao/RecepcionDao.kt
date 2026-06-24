package com.example.gestionusuarios.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.gestionusuarios.data.local.entity.RecepcionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecepcionDao {

    // Estandarizado: 1 = Activo/Ocupado, 0 = Finalizado
    @Query("SELECT * FROM recepciones WHERE estado = 1")
    fun obtenerRecepciones(): Flow<List<RecepcionEntity>>

    @Query("SELECT * FROM recepciones WHERE idHabitacion = :idHabitacion AND estado = 1 LIMIT 1")
    fun obtenerRecepcionActivaPorHabitacion(idHabitacion: Int): Flow<RecepcionEntity?>

    // Actualiza o inserta sin borrar el resto de la tabla
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLista(recepciones: List<RecepcionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(recepcion: RecepcionEntity)

    @Query("UPDATE recepciones SET estado = 0 WHERE idRecepcion = :idRecepcion")
    suspend fun marcarComoFinalizada(idRecepcion: Int)

    @Query("DELETE FROM recepciones")
    suspend fun eliminarTodo()

    @Query("SELECT * FROM recepciones WHERE idRecepcion = :idRecepcion LIMIT 1")
    suspend fun obtenerPorId(idRecepcion: Int): RecepcionEntity?

    @Query("SELECT COUNT(*) FROM recepciones WHERE estado = 1")
    suspend fun contarRecepcionesActivas(): Int

    /**
     * Sincronización Segura:
     * En lugar de eliminar todo, actualizamos masivamente.
     * Si necesitas borrar solo lo que no vino del servidor,
     * eso requiere una lógica más compleja (DiffUtil de BD).
     */
    @Transaction
    suspend fun guardarSincronizacionCompleta(recepciones: List<RecepcionEntity>) {
        insertarLista(recepciones)
    }

    @Query("SELECT * FROM recepciones WHERE idCliente = :idCliente ORDER BY fechaEntrada DESC")
    fun obtenerMisReservas(idCliente: Int): Flow<List<RecepcionEntity>>
}