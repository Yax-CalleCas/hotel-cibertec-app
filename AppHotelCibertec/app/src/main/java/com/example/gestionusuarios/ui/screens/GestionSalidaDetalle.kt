package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestionusuarios.ui.viewmodel.RecepcionViewModel
import com.example.gestionusuarios.ui.viewmodel.VentaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionSalidasDetalle(
    recepcionViewModel: RecepcionViewModel,
    ventaViewModel: VentaViewModel,
    idHabitacion: Int,
    onNavigateBack: () -> Unit
) {
    val recepcion by recepcionViewModel.recepcion.collectAsStateWithLifecycle()
    val servicios by ventaViewModel.serviciosAplanados.collectAsStateWithLifecycle()
    val loading by recepcionViewModel.loading.collectAsStateWithLifecycle()

    var penalidadStr by remember { mutableStateOf("0.0") }
    var showDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // --- BLOQUE DE DEPURACIÓN ---
    LaunchedEffect(recepcion, servicios) {
        if (recepcion != null) {
            android.util.Log.d("DEBUG_DATA", "--- DATOS RECIBIDOS EN UI ---")
            android.util.Log.d("DEBUG_DATA", "Habitacion: ${recepcion?.numero}")
            android.util.Log.d("DEBUG_DATA", "Precio Inicial: ${recepcion?.precioInicial}")
            android.util.Log.d("DEBUG_DATA", "Adelanto: ${recepcion?.adelanto}")
            android.util.Log.d("DEBUG_DATA", "Saldo Restante: ${recepcion?.precioRestante}")
            android.util.Log.d("DEBUG_DATA", "Total Pagado: ${recepcion?.totalPagado}")
        } else {
            android.util.Log.d("DEBUG_DATA", "Recepcion es NULL actualmente.")
        }

        android.util.Log.d("DEBUG_DATA", "Cantidad de servicios: ${servicios.size}")
        servicios.forEachIndexed { index, item ->
            android.util.Log.d("DEBUG_DATA", "Servicio $index: ${item.nombreProducto} | Subtotal: ${item.subTotal}")
        }
    }
    // 1. Cargamos la recepción activa cuando el idHabitacion cambia
    LaunchedEffect(idHabitacion) {
        recepcionViewModel.buscarRecepcionActiva(idHabitacion)
    }

// 1. Cargamos la recepción activa cuando el idHabitacion cambia
    LaunchedEffect(idHabitacion) {
        recepcionViewModel.buscarRecepcionActiva(idHabitacion)
    }

    // 2. Cargamos las ventas solo cuando tenemos una recepción válida
    // Al usar 'recepcion?.idRecepcion' como key, se dispara solo cuando el ID está presente.
    LaunchedEffect(recepcion?.idRecepcion) {
        val idRec = recepcion?.idRecepcion
        if (idRec != null) {
            ventaViewModel.cargarVentasPorRecepcion(idRec)
        }
    }

    // 3. Cálculo del monto total usando derivedStateOf para eficiencia y reactividad.
    // Se recalcula automáticamente si cambia recepcion, servicios o penalidadStr.
    val penalidad = penalidadStr.toDoubleOrNull() ?: 0.0
    val totalNeto by remember(recepcion, servicios, penalidad) {
        derivedStateOf {
            val restante = recepcion?.precioRestante ?: 0.0
            val consumos = servicios.filter { it.estadoVenta != "PAGADO" }.sumOf { it.subTotal }
            restante + penalidad + consumos
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Registrar Salida " +
                                "", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Habitación ${recepcion?.numero ?: "..."}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (recepcion != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = 24.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- RESUMEN ---
                item {
                    Text(
                        "Resumen de Habitación",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = 0.3f
                            )
                        )
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Cabecera con Habitación y Categoría
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Hab. ${recepcion?.numero ?: "-"}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    recepcion?.categoriaNombre ?: "-",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            HorizontalDivider()

                            // Datos del cliente
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    "${recepcion?.nombre ?: ""} ${recepcion?.apellido ?: ""}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "${recepcion?.tipoDocumento ?: "-"}: ${recepcion?.documento ?: "-"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Detalle de Pagos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = 0.3f
                            )
                        )
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Filas de costos
                            val costos = listOf(
                                "Inicial" to (recepcion?.precioInicial ?: 0.0),
                                "Adelantado" to (recepcion?.adelanto ?: 0.0)
                            )

                            costos.forEach { (label, value) ->
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "S/ ${"%.2f".format(value)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            // Total Restante destacado
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Saldo Restante",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "S/ ${"%.2f".format(recepcion?.precioRestante ?: 0.0)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Input de penalidad estilizado
                            OutlinedTextField(
                                value = penalidadStr,
                                onValueChange = { penalidadStr = it },
                                label = { Text("Monto Penalidad") },
                                prefix = {
                                    Text(
                                        "S/ ",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                textStyle = MaterialTheme.typography.bodySmall,
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                }

                // --- SERVICIOS ---
                item {
                    Text(
                        text = "Servicios a la Habitación",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Header de la tabla
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Producto",
                            Modifier.weight(1f),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Cant",
                            Modifier.width(40.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Precio",
                            Modifier.width(60.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Estado",
                            Modifier.width(80.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Total",
                            Modifier.width(60.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(thickness = 1.dp)
                }

                items(servicios) { item ->
                    val esPendiente = item.estadoVenta.equals("PENDIENTE", ignoreCase = true)

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.nombreProducto ?: "-",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.cantidad.toString(),
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "%.2f".format(item.precioUnitario),
                            modifier = Modifier.width(60.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodySmall
                        )

                        // Badge de estado
                        Box(modifier = Modifier.width(80.dp), contentAlignment = Alignment.Center) {
                            Surface(
                                color = if (esPendiente) Color(0xFFD32F2F) else Color(0xFF2E7D32),
                                shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Text(
                                    text = if (esPendiente) "PENDIENTE" else "PAGADO",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Text(
                            text = "%.2f".format(item.subTotal),
                            modifier = Modifier.width(60.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        onClick = { showDialog = true },
                        enabled = !isProcessing
                    ) {
                        Text(
                            text = if (isProcessing) "Procesando..." else "Registrar Salida: S/ ${
                                "%.2f".format(
                                    totalNeto
                                )
                            }",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!isProcessing) showDialog = false },
            icon = {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("¿Confirmar salida?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Esta acción cerrará la habitación actual de forma permanente.")
                    Text(
                        text = "Total a procesar: S/ ${"%.2f".format(totalNeto)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isProcessing = true
                        recepcionViewModel.registrarSalida(
                            recepcion!!.idRecepcion,
                            idHabitacion,
                            penalidad,
                            totalNeto
                        ) { success ->
                            isProcessing = false
                            showDialog = false
                            if (success) onNavigateBack()
                        }
                    },
                    enabled = !isProcessing
                ) {
                    Text(if (isProcessing) "Procesando..." else "Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    enabled = !isProcessing
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}