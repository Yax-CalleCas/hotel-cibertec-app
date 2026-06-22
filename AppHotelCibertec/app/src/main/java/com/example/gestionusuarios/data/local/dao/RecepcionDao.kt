package com.example.gestionusuarios.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gestionusuarios.data.local.entity.RecepcionEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface RecepcionDao {

    @Query("SELECT * FROM recepciones")
    fun obtenerRecepciones(): Flow<List<RecepcionEntity>>

    @Query("SELECT * FROM recepciones WHERE idHabitacion = :idHabitacion AND estado = 1 LIMIT 1")
    fun obtenerRecepcionActivaPorHabitacion(idHabitacion: Int): Flow<RecepcionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(recepcion: RecepcionEntity)

    @Query("UPDATE recepciones SET estado = 0 WHERE idRecepcion = :idRecepcion")
    suspend fun marcarComoFinalizada(idRecepcion: Int)

    @Query("DELETE FROM recepciones")
    suspend fun eliminarTodo()
}