package com.example.gestionusuarios.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.gestionusuarios.ui.viewmodel.AppViewModelFactory

val LocalViewModelFactory = staticCompositionLocalOf<AppViewModelFactory> {
    error("ViewModelFactory no provista")
}