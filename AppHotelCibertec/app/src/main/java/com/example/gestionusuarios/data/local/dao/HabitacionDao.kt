package com.example.gestionusuarios.data.local.dao

import androidx.room.*
import com.example.gestionusuarios.data.local.entity.HabitacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitacionDao {

    @Query("SELECT * FROM habitacion")
    fun listarTodos(): Flow<List<HabitacionEntity>>

    @Query("SELECT * FROM habitacion WHERE idHabitacion = :id")
    fun buscarPorId(id: Int): Flow<HabitacionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(habitacion: HabitacionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLista(habitaciones: List<HabitacionEntity>)

    @Query("UPDATE habitacion SET idEstadoHabitacion = :nuevoIdEstado WHERE idHabitacion = :id")
    suspend fun actualizarEstadoHabitacion(id: Int?, nuevoIdEstado: Int)


    @Query("UPDATE habitacion SET idEstadoHabitacion = :nuevoEstado WHERE idHabitacion IN (:ids)")
    suspend fun actualizarEstadosEnLote(ids: List<Int>, nuevoEstado: Int)

    @Query("DELETE FROM habitacion WHERE idHabitacion = :id")
    suspend fun eliminarPorId(id: Int)

    @Query("DELETE FROM habitacion")
    suspend fun borrarTodo()

    @Transaction
    suspend fun syncHabitaciones(nuevasHabitaciones: List<HabitacionEntity>) {
        borrarTodo()
        insertarLista(nuevasHabitaciones)
    }
}