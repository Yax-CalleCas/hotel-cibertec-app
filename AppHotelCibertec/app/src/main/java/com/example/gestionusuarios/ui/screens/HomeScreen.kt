package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestionusuarios.data.remote.model.DashboardItem
import com.example.gestionusuarios.ui.navigation.Routes
import com.example.gestionusuarios.ui.viewmodel.PersonaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: PersonaViewModel, onLogout: () -> Unit) {

    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val userName by viewModel.nombreUsuario.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.primaryContainer
    val gradient = Brush.verticalGradient(listOf(primaryColor, secondaryColor))

    val onRefresh: () -> Unit = {
        scope.launch { isRefreshing = true; kotlinx.coroutines.delay(1500); isRefreshing = false }
    }

    val menuItems = listOf(
        DashboardItem("Usuarios", Routes.GestionPersonas.route, Icons.Default.People),
        DashboardItem("G.Productos", Routes.ProductoList.route, Icons.Default.ShoppingCart),
        DashboardItem("G.Habitaciones", Routes.HabitacionList.route, Icons.Default.Bed),
        DashboardItem("Alquiler", Routes.GestionHabitaciones.route, Icons.Default.SettingsSystemDaydream),
        DashboardItem("Recepcion", Routes.RecepcionHabitacionesOcupadas.route, Icons.Default.MeetingRoom),
        DashboardItem("Salidas", Routes.GestionSalidasLista.route, Icons.Default.ExitToApp),
        DashboardItem("Salir", "logout_action", Icons.Default.Logout)
    )

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = onRefresh) {
            Column(modifier = Modifier.fillMaxSize()) {

                Surface(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(gradient),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                            Text(
                                "Bienvenido,",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                           //lleva a mostrar el nombre usuario apellido
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            // Badge sutil
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "SISTEMA DE GESTIÓN",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }

                // --- GRID DE TARJETAS ---
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                ) {
                    items(menuItems) { item ->
                        DashboardCard(item) {
                            if (item.route == "logout_action") onLogout()
                            else navController.navigate(item.route) { launchSingleTop = true }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(item: DashboardItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            // Un truco profesional: usa surfaceContainer si el fondo es background
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        // Borde ultra delgado con baja opacidad para un look premium
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Contenedor del icono con un ligero gradiente de transparencia
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

