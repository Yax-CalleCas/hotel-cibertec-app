package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gestionusuarios.data.local.entity.HabitacionEntity
import com.example.gestionusuarios.ui.viewmodel.EstadoHabitacionViewModel
import com.example.gestionusuarios.ui.viewmodel.HabitacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitacionListScreen(
    viewModel: HabitacionViewModel,
    navController: NavController,
    estadosViewModel: EstadoHabitacionViewModel,
    onNavigateToForm: (Int?) -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val habitaciones by viewModel.habitaciones.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val listaEstados by estadosViewModel.estados.collectAsStateWithLifecycle()

    var habitacionABorrar by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habitaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToForm(null) }) {
                Icon(Icons.Default.Add, "Nueva")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.sincronizar() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(habitaciones, key = { it.idHabitacion }) { habitacion ->
                    val estado = listaEstados.find { it.idEstadoHabitacion == habitacion.idEstadoHabitacion }?.descripcion ?: "N/A"

                    HabitacionItem(
                        habitacion = habitacion,
                        estadoDescripcion = estado,
                        onEdit = { onNavigateToForm(habitacion.idHabitacion) },
                        onDelete = { habitacionABorrar = habitacion.idHabitacion },
                        onClick = { onNavigateToDetail(habitacion.idHabitacion) }
                    )
                }
            }
        }
    }

    habitacionABorrar?.let { id ->
        AlertDialog(
            onDismissRequest = { habitacionABorrar = null },
            title = { Text("Eliminar") },
            text = { Text("¿Deseas eliminar esta habitación?") },
            confirmButton = {
                TextButton(onClick = { viewModel.eliminar(id) { habitacionABorrar = null } }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { habitacionABorrar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun HabitacionItem(
    habitacion: HabitacionEntity,
    estadoDescripcion: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val colorEstado = when (estadoDescripcion.lowercase()) {
        "disponible" -> MaterialTheme.colorScheme.tertiary
        "ocupado", "ocupada" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = habitacion.urlsImagenes?.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Habitación ${habitacion.numero}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("S/. ${"%.2f".format(habitacion.precio)}", color = MaterialTheme.colorScheme.primary)
                SuggestionChip(
                    onClick = {},
                    label = { Text(estadoDescripcion.uppercase(), style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = colorEstado.copy(alpha = 0.1f))
                )
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}