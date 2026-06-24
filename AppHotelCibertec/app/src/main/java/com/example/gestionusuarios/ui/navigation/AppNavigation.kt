package com.example.gestionusuarios.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestionusuarios.data.local.SessionManager
import com.example.gestionusuarios.ui.screens.*
import com.example.gestionusuarios.ui.viewmodel.*


@Composable
fun AppNavigation(sessionManager: SessionManager) {
    val navController = rememberNavController()
    val factory = LocalViewModelFactory.current


    fun getDestinationForRole(rol: Int): String {
        android.util.Log.d("DEBUG_NAVEGACION", "Calculando destino para rol: $rol")
        return when (rol) {
            1, 2 -> Routes.Home.route
            3    -> Routes.CatalogoCliente.route
            else -> Routes.Login.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (!sessionManager.isLoggedIn()) Routes.Login.route
        else getDestinationForRole(sessionManager.getUserRole())
    ) {

        composable(Routes.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(factory = factory)
            val personaViewModel: PersonaViewModel = viewModel(factory = factory)

            LoginScreen(
                viewModel = loginViewModel,
                personaViewModel = personaViewModel,
                onLoginSuccess = {
                    // Obtenemos el rol que YA se guardó en el ViewModel
                    val rolGuardado = sessionManager.getUserRole()
                    val destination = getDestinationForRole(rolGuardado)

                    navController.navigate(destination) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegisterCliente = { navController.navigate(Routes.RegistrarCliente.route) }
            )
        }
//ruta para el login facebook o id cliente
        composable(Routes.RegistrarCliente.route) {
            val personaViewModel: PersonaViewModel = viewModel(factory = factory)
            RegistroClienteScreen(
                viewModel = personaViewModel,
                onBack = { navController.popBackStack() })
        }

        composable(Routes.Home.route) {
            MainScreen(onLogout = { performLogout(navController, sessionManager) })
        }

        composable(Routes.CatalogoCliente.route) {
            CatalogoClienteScreen(
                onLogout = { performLogout(navController, sessionManager) },
                onNavigateToReserva = { habitacion ->
                    // Usamos las propiedades del objeto habitacion para crear la ruta
                    navController.navigate(
                        Routes.DetalleReserva.createRoute(
                            id = habitacion.idHabitacion,
                            numero = habitacion.numero,
                            precio = habitacion.precio ?: 0.0
                        )
                    )
                }
            )
        }

        composable(
            route = Routes.DetalleReserva.route,
            arguments = listOf(
                navArgument("habitacionId") { type = NavType.IntType },
                navArgument("numeroHabitacion") { type = NavType.StringType },
                navArgument("precioPorNoche") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("habitacionId") ?: 0
            // Decodificamos el número de habitación
            val numero = backStackEntry.arguments?.getString("numeroHabitacion")?.let {
                java.net.URLDecoder.decode(it, "UTF-8")
            } ?: ""
            val precio = backStackEntry.arguments?.getFloat("precioPorNoche")?.toDouble() ?: 0.0

            DetalleReservaScreen(
                habitacionId = id,
                numeroHabitacion = numero,
                precioPorNoche = precio,
                onReservaExitosa = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Función auxiliar para centralizar la lógica de salida y evitar repetición.
 */
private fun performLogout(
    navController: androidx.navigation.NavController,
    sessionManager: SessionManager
) {
    sessionManager.clearSession()
    com.facebook.login.LoginManager.getInstance().logOut()
    navController.navigate(Routes.Login.route) {
        popUpTo(0) { inclusive = true }
    }
}
