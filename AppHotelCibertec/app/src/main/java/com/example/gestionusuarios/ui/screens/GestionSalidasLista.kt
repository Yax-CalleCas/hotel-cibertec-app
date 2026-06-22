package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestionusuarios.ui.viewmodel.HabitacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionSalidasLista(
    habitacionViewModel: HabitacionViewModel,
    onHabitacionClick: (Int) -> Unit
) {
    // Usamos collectAsStateWithLifecycle para mejor gestión de ciclo de vida
    val habitaciones by habitacionViewModel.habitaciones.collectAsStateWithLifecycle()
    val isLoading by habitacionViewModel.isLoading.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }

    val ocupadas = remember(habitaciones, searchQuery) {
        habitaciones.filter {
            // Ajusta el ID 2 según tu constante real de "Ocupada"
            it.idEstadoHabitacion == 2 &&
                    (searchQuery.isEmpty() || it.numero.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout de Habitaciones", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        // PullToRefreshBox integrado
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { habitacionViewModel.sincronizar() },
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar por número...") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp)
                )

                if (ocupadas.isEmpty() && !isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay habitaciones ocupadas actualmente.", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = ocupadas, key = { it.idHabitacion }) { hab ->
                            Card(
                                onClick = { onHabitacionClick(hab.idHabitacion) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                ListItem(
                                    headlineContent = { Text("Habitación ${hab.numero}", fontWeight = FontWeight.Bold) },
                                    supportingContent = { Text(hab.detalle ?: "Sin descripción adicional") },
                                    leadingContent = {
                                        Badge(containerColor = MaterialTheme.colorScheme.error) {
                                            Text("OCUPADA", fontWeight = FontWeight.Bold)
                                        }
                                    },
                                    trailingContent = { Icon(Icons.Default.ArrowForward, null) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}