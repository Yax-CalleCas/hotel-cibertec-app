package com.example.gestionusuarios.data.local.dao

import androidx.room.*
import com.example.gestionusuarios.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // 1. Obtener lista reactiva para la UI (usando Flow para actualizaciones en tiempo real)
    @Query("SELECT * FROM producto WHERE estado = 1")
    fun obtenerProductosActivos(): Flow<List<ProductoEntity>>

    // 2. Obtener un producto único por ID (útil para el ProductoDetalleScreen)
    @Query("SELECT * FROM producto WHERE idProducto = :id LIMIT 1")
    suspend fun obtenerProductoPorId(id: Int): ProductoEntity?

    // 3. Insertar lista de productos (para la sincronización inicial desde API)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)

    @Query("DELETE FROM producto")
    suspend fun limpiarTabla()

    @Transaction
    suspend fun refreshData(productos: List<ProductoEntity>) {
        limpiarTabla()
        insertarProductos(productos)
    }

    // 4. Insertar o actualizar un único producto (para el formulario)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: ProductoEntity)

    // 5. Actualizar un producto existente
    @Update
    suspend fun actualizar(producto: ProductoEntity)

    // 6. Eliminar un producto (o marcar como inactivo)
    @Delete
    suspend fun eliminar(producto: ProductoEntity)

    // 7. Eliminar por ID (útil si no tienes el objeto completo, solo el ID)
    @Query("DELETE FROM producto WHERE idProducto = :id")
    suspend fun eliminarPorId(id: Int)
}