package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.ChevronRight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaHabitacionesOcupadasScreen(
    habitacionViewModel: HabitacionViewModel,
    onHabitacionClick: (Int) -> Unit
) {
    // Observamos los datos desde el ViewModel
    val habitaciones by habitacionViewModel.habitaciones.collectAsStateWithLifecycle()
    val isLoading by habitacionViewModel.isLoading.collectAsStateWithLifecycle()

    // Filtramos localmente para mantener la reactividad sin lógica de negocio en la UI
    val ocupadas = remember(habitaciones) { habitaciones.filter { it.idEstadoHabitacion == 2 } }

    // Sincronización inicial al cargar la pantalla
    LaunchedEffect(Unit) {
        habitacionViewModel.sincronizar()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habitaciones Ocupadas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { habitacionViewModel.sincronizar() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (ocupadas.isEmpty() && !isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No hay habitaciones ocupadas.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = ocupadas, key = { it.idHabitacion }) { hab ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onHabitacionClick(hab.idHabitacion) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bed, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Hab. ${hab.numero}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(8.dp))
                                Badge(containerColor = Color(0xFFD32F2F)) {
                                    Text("OCUPADA", color = Color.White, modifier = Modifier.padding(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}