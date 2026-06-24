import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    val idPersona: Int,
    val nombre: String,
    val apellido: String,
    val correo: String,

    // Mapeamos el campo "tipoPersona" del JSON
    @SerializedName("tipoPersona")
    val tipoPersona: String?,

    val token: String
) {
    // Agregamos una propiedad calculada para obtener el ID numérico que tu App espera
    val idTipoPersona: Int?
        get() = when (tipoPersona) {
            "Administrador" -> 1
            "Empleado" -> 2
            "Cliente" -> 3
            else -> 0
        }
}