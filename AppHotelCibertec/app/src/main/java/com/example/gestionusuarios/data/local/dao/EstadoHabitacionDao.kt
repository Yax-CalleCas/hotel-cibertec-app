package com.example.gestionusuarios.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.gestionusuarios.data.local.entity.EstadoHabitacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstadoHabitacionDao {
    @Query("SELECT * FROM estado_habitacion")
    fun listarTodos(): Flow<List<EstadoHabitacionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLista(list: List<EstadoHabitacionEntity>)

    @Query("DELETE FROM estado_habitacion")
    suspend fun limpiarTabla()

    @Transaction
    suspend fun refreshData(list: List<EstadoHabitacionEntity>) {
        limpiarTabla()
        insertarLista(list)
    }
}