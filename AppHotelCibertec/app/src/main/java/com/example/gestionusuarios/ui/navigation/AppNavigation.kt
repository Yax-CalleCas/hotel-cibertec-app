package com.example.gestionusuarios.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestionusuarios.data.local.SessionManager
import com.example.gestionusuarios.ui.screens.*
import com.example.gestionusuarios.ui.viewmodel.*

@Composable
fun AppNavigation(sessionManager: SessionManager) {
    val navController = rememberNavController()
    val factory = LocalViewModelFactory.current

    // Determinamos la ruta inicial basándonos en la persistencia del token
    val startRoute = if (sessionManager.isLoggedIn()) Routes.Home.route else Routes.Login.route

    NavHost(navController = navController, startDestination = startRoute) {

        // --- MÓDULO DE AUTENTICACIÓN ---
        composable(Routes.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(factory = factory)
            val personaViewModel: PersonaViewModel = viewModel(factory = factory)

            LoginScreen(
                viewModel = loginViewModel,
                personaViewModel = personaViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegisterCliente = {
                    navController.navigate(Routes.RegistrarCliente.route)
                }
            )
        }

        composable(Routes.RegistrarCliente.route) {
            val personaViewModel: PersonaViewModel = viewModel(factory = factory)
            RegistroClienteScreen(
                viewModel = personaViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- MÓDULO PRINCIPAL ---
        composable(Routes.Home.route) {
            MainScreen(
                onLogout = {
                    // 1. Limpiamos sesión local (EncryptedSharedPreferences)
                    sessionManager.clearSession()

                    // 2. Limpiamos sesión externa (Facebook SDK)
                    com.facebook.login.LoginManager.getInstance().logOut()

                    // 3. Navegamos al login y destruimos el historial (popUpTo 0)
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}