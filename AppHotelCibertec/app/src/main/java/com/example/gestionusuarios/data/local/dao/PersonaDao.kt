package com.example.gestionusuarios.data.local.dao

import androidx.room.*
import com.example.gestionusuarios.data.local.entity.PersonaEntity

@Dao
interface PersonaDao {
    @Query("SELECT * FROM persona")
    suspend fun getAll(): List<PersonaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(personas: List<PersonaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(persona: PersonaEntity): Long

    @Update
    suspend fun update(persona: PersonaEntity)

    @Query("DELETE FROM persona WHERE idPersona = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM persona")
    suspend fun deleteAll()
}