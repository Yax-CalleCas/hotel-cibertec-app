
package com.example.gestionusuarios.data.local.mapper

import com.example.gestionusuarios.data.local.entity.PersonaEntity
import com.example.gestionusuarios.data.remote.model.Persona

fun PersonaEntity.toDomain(): Persona {
    return Persona(
        idPersona = this.idPersona,
        tipoDocumento = this.tipoDocumento,
        documento = this.documento,
        nombre = this.nombre,
        apellido = this.apellido,
        correo = this.correo,
        fotoUrl = this.fotoUrl,
        clave = this.clave,
        idTipoPersona = this.idTipoPersona,
        estado = this.estado,
        fechaCreacion = this.fechaCreacion
    )

}


fun Persona.toEntity(): PersonaEntity {
    return PersonaEntity(
        idPersona = this.idPersona ?: 0,
        tipoDocumento = this.tipoDocumento,
        documento = this.documento,
        nombre = this.nombre,
        apellido = this.apellido,
        correo = this.correo,
        fotoUrl = this.fotoUrl,
        clave = this.clave,
        idTipoPersona = this.idTipoPersona,
        estado = this.estado ?: true,
        fechaCreacion = this.fechaCreacion
    )
}

object PersonaValidator {
    fun esValido(p: Persona): Pair<Boolean, String?> {
        return when {
            p.nombre.isBlank() -> false to "Nombre requerido"
            p.documento.length < 8 -> false to "Documento inválido"
            !p.correo.contains("@") -> false to "Correo inválido"
            else -> true to null
        }
    }
}