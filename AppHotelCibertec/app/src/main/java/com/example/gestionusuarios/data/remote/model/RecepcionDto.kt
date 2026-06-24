package com.example.gestionusuarios.data.remote.model

import com.google.gson.annotations.SerializedName

data class RecepcionDto(
    @SerializedName("idRecepcion") val idRecepcion: Int? = null,
    @SerializedName("idCliente") val idCliente: Int? = null,
    @SerializedName("idHabitacion") val idHabitacion: Int? = null,
    @SerializedName("numero") val numero: String? = null,
    @SerializedName("categoriaNombre") val categoriaNombre: String? = null,
    @SerializedName("pisoNombre") val pisoNombre: String? = null,
    @SerializedName("detalleHabitacion") val detalleHabitacion: String? = null,
    @SerializedName("precioHabitacion") val precioHabitacion: Double? = null,
    @SerializedName("estadoHabitacion") val estadoHabitacion: String? = null,
    @SerializedName("tipoDocumento") val tipoDocumento: String? = null,
    @SerializedName("documento") val documento: String? = null,
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("apellido") val apellido: String? = null,
    @SerializedName("correo") val correo: String? = null,
    @SerializedName("precioInicial") val precioInicial: Double? = null,
    @SerializedName("adelanto") val adelanto: Double? = null,
    @SerializedName("precioRestante") val precioRestante: Double? = null,
    @SerializedName("totalPagado") val totalPagado: Double? = null,
    @SerializedName("costoPenalidad") val costoPenalidad: Double? = null,
    @SerializedName("fechaEntrada") val fechaEntrada: String? = null,
    @SerializedName("fechaSalida") val fechaSalida: String? = null,
    @SerializedName("fechaSalidaConfirmacion") val fechaSalidaConfirmacion: String? = null,
    @SerializedName("observacion") val observacion: String? = null,
    @SerializedName("estado") val estado: Boolean? = false
)