package com.example.gestionusuarios.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.gestionusuarios.data.local.entity.PersonaEntity

@Dao
interface AuthDao {
    @Query("SELECT * FROM persona WHERE correo = :correo LIMIT 1")
    suspend fun getPersonaPorCorreo(correo: String): PersonaEntity?
}