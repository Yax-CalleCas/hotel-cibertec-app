package com.example.gestionusuarios.ui.viewmodel

/**
 * Representa eventos de un solo uso que la UI debe observar.
 * Ejemplos: mostrar un Snackbar, navegar a otra pantalla, mostrar un diálogo.
 */
sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()

}