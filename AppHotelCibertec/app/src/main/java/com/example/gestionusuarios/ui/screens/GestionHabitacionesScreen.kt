package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestionusuarios.ui.viewmodel.HabitacionViewModel
import com.example.gestionusuarios.ui.viewmodel.PersonaViewModel
import com.example.gestionusuarios.ui.viewmodel.RecepcionViewModel
import kotlinx.coroutines.launch

private val DisponibleColor = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionHabitacionesScreen(
    habitacionViewModel: HabitacionViewModel,
    recepcionViewModel: RecepcionViewModel,
    personaViewModel: PersonaViewModel,
    onHabitacionClick: (Int) -> Unit
) {
    val habitaciones by habitacionViewModel.habitaciones.collectAsStateWithLifecycle()
    val isLoading by habitacionViewModel.isLoading.collectAsStateWithLifecycle()
    val disponibles = habitaciones.filter { it.idEstadoHabitacion == 1 }

    LaunchedEffect(Unit) {
        habitacionViewModel.sincronizar()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Alquiler", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { habitacionViewModel.sincronizar() },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    DashboardDisponible(totalDisponibles = disponibles.size)
                }

                item {
                    Text(
                        text = "Habitaciones Disponibles",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(disponibles) { habitacion ->
                    HabitacionDisponibleCard(
                        numero = habitacion.numero,
                        detalle = habitacion.detalle ?: "Sin descripción",
                        precio = habitacion.precio ?: 0.0,
                        onClick = { habitacion.idHabitacion?.let(onHabitacionClick) }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardDisponible(totalDisponibles: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DisponibleColor.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = DisponibleColor.copy(alpha = 0.2f)) {
                Icon(
                    imageVector = Icons.Default.Hotel,
                    contentDescription = null,
                    tint = DisponibleColor,
                    modifier = Modifier.padding(12.dp).size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "$totalDisponibles Disponibles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Habitaciones listas para alquilar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun HabitacionDisponibleCard(numero: String, detalle: String, precio: Double, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Habitación $numero", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Badge(containerColor = DisponibleColor.copy(alpha = 0.1f), contentColor = DisponibleColor) {
                    Text("Disponible", modifier = Modifier.padding(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Precio por noche", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("S/ %.2f".format(precio), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Button(onClick = onClick, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Bed, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Alquilar")
                }
            }
        }
    }
}