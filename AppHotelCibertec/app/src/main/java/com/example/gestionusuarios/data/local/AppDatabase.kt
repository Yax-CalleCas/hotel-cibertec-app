package com.example.gestionusuarios.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gestionusuarios.data.local.converters.Converters
import com.example.gestionusuarios.data.local.dao.*
import com.example.gestionusuarios.data.local.entity.*

@Database(
    entities = [
        PersonaEntity::class,
        UsuarioEntity::class,
        ProductoEntity::class,
        HabitacionEntity::class,
        EstadoHabitacionEntity::class,
        CategoriaEntity::class,
        PisoEntity::class,
        VentaEntity::class,
        RecepcionEntity::class,
        DetalleVentaEntity::class,
    ],
    version = 42,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun personaDao(): PersonaDao
    abstract fun authDao(): AuthDao
    abstract fun productoDao(): ProductoDao
    abstract fun habitacionDao(): HabitacionDao
    abstract fun estadoHabitacionDao(): EstadoHabitacionDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun pisoDao(): PisoDao
    abstract fun recepcionDao(): RecepcionDao
    abstract fun ventaDao(): VentaDao
    abstract fun detalleVentaDao(): DetalleVentaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestion_hoteles_db" // Nombre de base de datos más descriptivo
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}