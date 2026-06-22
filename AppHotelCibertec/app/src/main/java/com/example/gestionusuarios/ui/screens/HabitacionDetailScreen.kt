package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.gestionusuarios.ui.viewmodel.EstadoHabitacionViewModel
import com.example.gestionusuarios.ui.viewmodel.HabitacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitacionDetailScreen(
    habitacionId: Int,
    viewModel: HabitacionViewModel,
    estadosViewModel: EstadoHabitacionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit
) {
    val habitaciones by viewModel.habitaciones.collectAsStateWithLifecycle()
    val listaEstados by estadosViewModel.estados.collectAsStateWithLifecycle()
    val categoriasMap by viewModel.categoriasMap.collectAsStateWithLifecycle()

    val habitacion = remember(habitacionId, habitaciones) { habitaciones.find { it.idHabitacion == habitacionId } }

    // Estados derivados para evitar cálculos en recomposiciones
    val estadoDesc by remember(habitacion, listaEstados) {
        derivedStateOf { listaEstados.find { it.idEstadoHabitacion == habitacion?.idEstadoHabitacion }?.descripcion ?: "N/A" }
    }
    val categoriaDesc by remember(habitacion, categoriasMap) {
        derivedStateOf { habitacion?.let { categoriasMap[it.idCategoria] } ?: "No asignada" }
    }

    var showDialog by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToEdit(habitacionId) }) {
                Icon(Icons.Default.Edit, "Editar")
            }
        }
    ) { padding ->
        if (habitacion == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())) {
                // Galería de miniaturas
                if (!habitacion.urlsImagenes.isNullOrEmpty()) {
                    LazyRow(contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(habitacion.urlsImagenes!!) { index, url ->
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                modifier = Modifier.size(280.dp, 180.dp).clip(RoundedCornerShape(24.dp)).clickable {
                                    currentIndex = index
                                    showDialog = true
                                },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Info
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Habitación ${habitacion.numero}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                    Text("S/ ${"%.2f".format(habitacion.precio)} / noche", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)

                    Spacer(Modifier.height(24.dp))

                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            DetailItem("Estado", estadoDesc)
                            DetailItem("Categoría", categoriaDesc)
                            DetailItem("Piso", "Nivel ${habitacion.idPiso}")
                        }
                    }
                }
            }
        }
    }

    // ... código anterior ...

    // Visor de imagen integrado
    if (showDialog && habitacion != null && !habitacion.urlsImagenes.isNullOrEmpty()) {
        ZoomableImageDialog(
            images = habitacion.urlsImagenes!!,
            initialIndex = currentIndex,
            onDismiss = { showDialog = false }
        )
    }
} // Fin de HabitacionDetailScreen

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
    }
}
