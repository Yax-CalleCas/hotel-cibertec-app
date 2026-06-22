package com.example.gestionusuarios.data.local.dao

import androidx.room.*
import com.example.gestionusuarios.data.local.entity.PisoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PisoDao {
    @Query("SELECT * FROM pisos")
    fun obtenerPisos(): Flow<List<PisoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(pisos: List<PisoEntity>)

    @Query("DELETE FROM pisos")
    suspend fun eliminarTodo()
}