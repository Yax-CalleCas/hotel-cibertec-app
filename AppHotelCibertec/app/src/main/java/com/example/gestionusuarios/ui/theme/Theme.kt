package com.example.gestionusuarios.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0E284E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    secondary = Color(0xFF4796C1),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB3E5FC),
    surface = Color(0xFFF8F9FA),
    background = Color(0xFFF1F3F5),
    error = Color(0xFFD32F2F),
    surfaceVariant = Color(0xFFE8EAF6)
)

@Composable
fun GestionUsuariosTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
