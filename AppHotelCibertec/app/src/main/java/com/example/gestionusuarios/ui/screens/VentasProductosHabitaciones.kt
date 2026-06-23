package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestionusuarios.data.remote.model.ItemCarrito
import com.example.gestionusuarios.data.remote.model.VentaDto
import com.example.gestionusuarios.data.remote.model.DetalleVentaDto
import com.example.gestionusuarios.ui.viewmodel.ProductoViewModel
import com.example.gestionusuarios.ui.viewmodel.RecepcionViewModel
import com.example.gestionusuarios.ui.viewmodel.VentaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasProductosHabitaciones(
    idHabitacion: Int,
    recepcionViewModel: RecepcionViewModel,
    ventaViewModel: VentaViewModel,
    productoViewModel: ProductoViewModel,
    onNavigateBack: () -> Unit
) {
    // Observadores de estado
    val currentRecepcion by recepcionViewModel.recepcion.collectAsStateWithLifecycle()
    val productos by productoViewModel.productos.collectAsStateWithLifecycle()
    val isLoadingRecepcion by recepcionViewModel.loading.collectAsStateWithLifecycle()
    val isProcessingVenta by ventaViewModel.loading.collectAsStateWithLifecycle()
    val mensaje by ventaViewModel.mensaje.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estado del carrito local
    val carrito = remember { mutableStateListOf<ItemCarrito>() }
    var showSheet by remember { mutableStateOf(false) }
    var expandedEstado by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf("PENDIENTE") }
    val opcionesEstado = listOf("PENDIENTE", "PAGADO")

    // Cálculos derivados
    val totalConsumo by remember(carrito.size) { derivedStateOf { carrito.sumOf { it.subTotal ?: 0.0 } } }

    // Validación reactiva para el botón
    val isFinalizarEnabled by remember(currentRecepcion, carrito.size, isProcessingVenta) {
        derivedStateOf {
            val hasValidRecepcion = currentRecepcion != null && (currentRecepcion?.idRecepcion ?: 0 > 0)
            carrito.isNotEmpty() && !isProcessingVenta && hasValidRecepcion
        }
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            ventaViewModel.limpiarMensaje()
        }
    }

    LaunchedEffect(idHabitacion) {
        recepcionViewModel.buscarRecepcionActiva(idHabitacion)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registro de Consumos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoadingRecepcion) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            currentRecepcion?.let {
                HabitacionHeader(
                    numero = it.numero.toString(),
                    categoria = it.categoriaNombre ?: "N/A",
                    piso = it.pisoNombre ?: "N/A",
                    precio = it.precioHabitacion.toString(),
                    cliente = "${it.nombre} ${it.apellido}"
                )
            }

            // Lista de productos
            Surface(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { Text("Productos en carrito", style = MaterialTheme.typography.titleSmall) }
                    items(carrito, key = { it.idProducto }) { item ->
                        CartItemRow(item) { carrito.remove(item) }
                    }
                }
            }

            // Panel inferior de acciones
            Surface(tonalElevation = 6.dp, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Dropdown de Estado
                        ExposedDropdownMenuBox(
                            expanded = expandedEstado,
                            onExpandedChange = { expandedEstado = !expandedEstado },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = estadoSeleccionado,
                                onValueChange = {}, readOnly = true,
                                label = { Text("Estado Venta") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expandedEstado, onDismissRequest = { expandedEstado = false }) {
                                opcionesEstado.forEach { op ->
                                    DropdownMenuItem(text = { Text(op) }, onClick = { estadoSeleccionado = op; expandedEstado = false })
                                }
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        // Total
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TOTAL", style = MaterialTheme.typography.labelLarge)
                            Text("S/ ${"%.2f".format(totalConsumo)}", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    // Botones
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { showSheet = true }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Add, null); Text("Añadir")
                        }
                        Button(
                            onClick = {
                                val idRec = currentRecepcion?.idRecepcion ?: return@Button
                                val detalles = carrito.map { DetalleVentaDto(null, it.idProducto, it.nombre, it.cantidad, it.precio, it.subTotal) }
                                val nuevaVenta = VentaDto(null, idRec, totalConsumo, estadoSeleccionado, detalles)

                                ventaViewModel.registrarVenta(nuevaVenta) { exito ->
                                    if (exito) {
                                        // Al tener éxito, la base de datos local (Room) se actualiza sola.
                                        // Simplemente limpiamos el carrito y cerramos la pantalla.
                                        carrito.clear()
                                        onNavigateBack()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = isFinalizarEnabled
                        ) {
                            Text(if (isProcessingVenta) "Procesando..." else "Finalizar Venta")
                        }
                    }
                }
            }
        }
    }

    // Sheet de selección
    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(productos, key = { it.idProducto ?: 0 }) { prod ->
                    ListItem(
                        headlineContent = { Text(prod.nombre) },
                        trailingContent = {
                            IconButton(onClick = {
                                val id = prod.idProducto ?: 0
                                val idx = carrito.indexOfFirst { it.idProducto == id }
                                if (idx != -1) {
                                    val item = carrito[idx]
                                    carrito[idx] = item.copy(cantidad = item.cantidad + 1, subTotal = (item.cantidad + 1) * item.precio)
                                } else {
                                    carrito.add(ItemCarrito(id, prod.nombre, prod.precio, 1, prod.precio))
                                }
                                showSheet = false
                            }) { Icon(Icons.Default.AddCircle, null, tint = MaterialTheme.colorScheme.primary) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HabitacionHeader(numero: String, categoria: String, piso: String, precio: String, cliente: String? = null) {
    OutlinedCard(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Habitación: $numero", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("S/ $precio / noche", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Categoría: $categoria", style = MaterialTheme.typography.bodyMedium)
                Text("Piso: $piso", style = MaterialTheme.typography.bodyMedium)
            }
            cliente?.let {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text("Cliente: $it", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun CartItemRow(item: ItemCarrito, onDelete: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        ListItem(
            headlineContent = { Text(item.nombre, fontWeight = FontWeight.SemiBold) },
            supportingContent = { Text("Cant: ${item.cantidad} | Subtotal: S/ ${"%.2f".format(item.subTotal ?: 0.0)}") },
            trailingContent = { IconButton(onClick = onDelete) { Icon(Icons.Default.Close, "Eliminar", tint = MaterialTheme.colorScheme.error) } }
        )
    }
}