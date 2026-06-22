package com.example.gestionusuarios.data.remote.model

data class Persona(
    val idPersona: Int? = null,
    val tipoDocumento: String,
    val documento: String,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val fotoUrl: String? = null,
    val clave: String? = null,
    val idTipoPersona: Int,
    val estado: Boolean? = true,
    val fechaCreacion: String? = null
)