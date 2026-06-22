package com.example.gestionusuarios.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persona")
data class PersonaEntity(
    @PrimaryKey(autoGenerate = true)
    val idPersona: Int? = null,
    val tipoDocumento: String,
    val documento: String,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val fotoUrl: String?,
    val clave: String?,
    val idTipoPersona: Int,
    val estado: Boolean,
    val fechaCreacion: String?
)
