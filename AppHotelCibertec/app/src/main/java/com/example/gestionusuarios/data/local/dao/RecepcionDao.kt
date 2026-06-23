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
    // En RecepcionDao
    @Query("SELECT * FROM recepciones WHERE estado = 'ACTIVO' OR estado = 'OCUPADO'")
    fun obtenerRecepciones(): Flow<List<RecepcionEntity>>
    @Query("SELECT * FROM recepciones WHERE idHabitacion = :idHabitacion AND estado = 1 LIMIT 1")
    fun obtenerRecepcionActivaPorHabitacion(idHabitacion: Int): Flow<RecepcionEntity?>

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

    @Transaction
    suspend fun guardarSincronizacionCompleta(recepciones: List<RecepcionEntity>) {
        // Es vital eliminar lo anterior si el servidor es la fuente de verdad absoluta
        eliminarTodo()
        insertarLista(recepciones)
    }
}