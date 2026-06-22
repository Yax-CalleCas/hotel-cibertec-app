package com.example.gestionusuarios.ui.screens


import GestionPersonaScreen
import ProductoDetalleScreen
import ProductoFormScreen
import ProductoListScreen
import RegistroPersonaScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestionusuarios.ui.navigation.LocalViewModelFactory
import com.example.gestionusuarios.ui.navigation.Routes
import com.example.gestionusuarios.ui.viewmodel.*
@Composable
fun MainScreen(onLogout: () -> Unit) {


    val personaViewModel: PersonaViewModel = viewModel(factory = LocalViewModelFactory.current)
    val navController = rememberNavController()

    val internalNavController = rememberNavController()
    val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val factory = LocalViewModelFactory.current

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    Triple("Home", Icons.Default.Home, Routes.Home.route),
                    Triple("Personas", Icons.Default.People, Routes.GestionPersonas.route),
                    Triple("Productos", Icons.Default.ShoppingCart, Routes.ProductoList.route),
                ).forEach { (label, icon, route) ->
                    NavigationBarItem(
                        icon = { Icon(icon, label) },
                        label = { Text(label) },
                        selected = currentRoute == route,
                        onClick = {
                            internalNavController.navigate(route) {
                                popUpTo(internalNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = internalNavController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.Home.route) {
                // AQUÍ ES DONDE PASAS EL CALLBACK AL HOMESCREEN
                HomeScreen(
                    navController = internalNavController,
                    viewModel = viewModel(factory = factory),
                    onLogout = onLogout
                )
            }


            // --- PERSONAS ---
            composable(Routes.GestionPersonas.route) {
                GestionPersonaScreen(
                    viewModel(factory = factory),
                    { internalNavController.navigate(Routes.NuevoUsuario.route) },
                    { id -> internalNavController.navigate(Routes.EditarUsuario.createRoute(id)) }
                )
            }
            composable(Routes.NuevoUsuario.route) {
                RegistroPersonaScreen(viewModel(factory = factory)) { internalNavController.popBackStack() }
            }
            composable(
                route = Routes.EditarUsuario.route,
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { backStackEntry ->
                EditarPersonaScreen(
                    backStackEntry.arguments?.getInt("userId") ?: 0,
                    viewModel(factory = factory)
                ) { internalNavController.popBackStack() }
            }
            //repotes mapeo



            composable(
                route = Routes.DetalleHabitacion.route,
                arguments = listOf(navArgument("idHabitacion") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("idHabitacion") ?: 0
                HabitacionDetailScreen(
                    habitacionId = id,
                    viewModel = viewModel(factory = factory),
                    estadosViewModel = viewModel(factory = factory),
                    onNavigateBack = { internalNavController.popBackStack() },
                    onNavigateToEdit = { idToEdit ->
                        internalNavController.navigate(Routes.HabitacionForm.createRoute(idToEdit))
                    }
                )
            }

            // En tu MainScreen, dentro del NavHost
            composable(
                route = "alquiler_habitacion/{idHabitacion}",
                arguments = listOf(navArgument("idHabitacion") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("idHabitacion") ?: 0

                // Obtenemos el viewModel
                val habitacionViewModel: HabitacionViewModel = viewModel(factory = factory)
                // Obtenemos la lista actual de habitaciones
                val habitaciones by habitacionViewModel.habitaciones.collectAsStateWithLifecycle()

                // Buscamos la entidad específica
                val habitacion = habitaciones.find { it.idHabitacion == id }

                if (habitacion != null) {
                    DetalleHabitacionAlquilerScreen(
                        idHabitacion = id,
                        habitacion = habitacion,
                        habitacionViewModel = habitacionViewModel,
                        personaViewModel = viewModel(factory = factory),
                        categoriaViewModel = viewModel(factory = factory),
                        pisoViewModel = viewModel(factory = factory),
                        recepcionViewModel = viewModel(factory = factory),
                        onVolver = { internalNavController.popBackStack() }
                    )
                } else {
                    //  Mostrar un indicador de carga mientras se sincroniza
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }



            composable(
                route = Routes.HabitacionForm.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = 0 })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id")
                HabitacionFormScreen(
                    id = if (id == 0) null else id,
                    viewModel = viewModel(factory = factory),
                    estadosViewModel = viewModel(factory = factory),
                    categoriaViewModel = viewModel(factory = factory),
                    pisoViewModel = viewModel(factory = factory),
                    onNavigateBack = { internalNavController.popBackStack() }
                )
            }

// Dentro de tu NavHost en AppNavigation.kt

            composable(Routes.GestionHabitaciones.route) {
                GestionHabitacionesScreen(
                    habitacionViewModel = viewModel(factory = factory),
                    recepcionViewModel = viewModel(factory = factory),
                    personaViewModel = viewModel(factory = factory),
                    onHabitacionClick = { idHabitacion ->
                        internalNavController.navigate(
                            Routes.DetalleHabitacionAlquilerScreen.createRoute(
                                idHabitacion
                            )
                        )
                    }
                )
            }

            composable(Routes.HabitacionList.route) {
                HabitacionListScreen(
                    viewModel(factory = factory),
                    internalNavController,
                    viewModel(factory = factory),
                    { id -> internalNavController.navigate(Routes.HabitacionForm.createRoute(id)) },
                    { id -> internalNavController.navigate(Routes.DetalleHabitacion.createRoute(id)) } // Corregido: antes tenías HabitacionDetail
                )
            }

            composable(Routes.RecepcionHabitacionesOcupadas.route) {
                ListaHabitacionesOcupadasScreen(viewModel(factory = factory)) { id ->
                    internalNavController.navigate(Routes.DetalleVentasHabitaciones.createRoute(id))
                }
            }

            composable(
                route = Routes.DetalleVentasHabitaciones.route,
                arguments = listOf(navArgument("idHabitacion") { type = NavType.IntType })
            ) { backStackEntry ->
                VentasProductosHabitaciones(
                    backStackEntry.arguments?.getInt("idHabitacion") ?: 0,
                    viewModel(factory = factory),
                    viewModel(factory = factory),
                    viewModel(factory = factory),
                ) { internalNavController.popBackStack() }
            }

            // --- SALIDAS Y PRODUCTOS ---
            composable(Routes.GestionSalidasLista.route) {
                GestionSalidasLista(viewModel(factory = factory)) { id ->
                    internalNavController.navigate(Routes.GestionSalidasDetalle.createRoute(id))
                }
            }
            composable(
                route = Routes.GestionSalidasDetalle.route,
                arguments = listOf(navArgument("idHabitacion") { type = NavType.IntType })
            ) { backStackEntry ->
                GestionSalidasDetalle(
                    viewModel(factory = factory), viewModel(factory = factory),
                    backStackEntry.arguments?.getInt("idHabitacion") ?: 0
                ) { internalNavController.popBackStack() }
            }

            composable(Routes.ProductoList.route) {
                ProductoListScreen(
                    viewModel(factory = factory),
                    { id -> internalNavController.navigate(Routes.ProductoForm.createRoute(id)) },
                    { id -> internalNavController.navigate(Routes.ProductoDetalle.createRoute(id)) }
                )
            }
            composable(
                route = Routes.ProductoDetalle.route,
                arguments = listOf(navArgument("productoId") { type = NavType.IntType })
            ) { backStackEntry ->
                ProductoDetalleScreen(
                    viewModel(factory = factory),
                    backStackEntry.arguments?.getInt("productoId") ?: 0,
                    { internalNavController.popBackStack() },
                    { id -> internalNavController.navigate(Routes.ProductoForm.createRoute(id)) }
                )
            }
            composable(
                route = Routes.ProductoForm.route,
                arguments = listOf(navArgument("productoId") {
                    type = NavType.IntType; defaultValue = 0
                })
            ) { backStackEntry ->
                ProductoFormScreen(
                    viewModel(factory = factory),
                    backStackEntry.arguments?.getInt("productoId")?.takeIf { it != 0 },
                    { internalNavController.popBackStack() }
                )
            }
        }
    }
}