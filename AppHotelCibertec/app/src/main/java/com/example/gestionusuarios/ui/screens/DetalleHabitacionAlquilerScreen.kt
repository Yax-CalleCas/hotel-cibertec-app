package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestionusuarios.data.local.entity.HabitacionEntity
import com.example.gestionusuarios.data.remote.model.Persona
import com.example.gestionusuarios.data.remote.model.RecepcionDto
import com.example.gestionusuarios.ui.viewmodel.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleHabitacionAlquilerScreen(
    idHabitacion: Int,
    habitacion: HabitacionEntity,
    habitacionViewModel: HabitacionViewModel,
    personaViewModel: PersonaViewModel,
    categoriaViewModel: CategoriaViewModel,
    pisoViewModel: PisoViewModel,
    recepcionViewModel: RecepcionViewModel,
    onVolver: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { personaViewModel.cargarPersonas() }

    val clientes by personaViewModel.clientes.collectAsStateWithLifecycle()
    val categorias by categoriaViewModel.categorias.collectAsState()
    val pisos by pisoViewModel.pisos.collectAsState()

    var clienteSeleccionado by remember { mutableStateOf<Persona?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val fechaEntrada = remember { LocalDate.now() }
    var fechaSalida by remember { mutableStateOf<LocalDate?>(null) }
    var openSalida by remember { mutableStateOf(false) }
    var adelantoStr by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    val noches = if (fechaSalida != null) ChronoUnit.DAYS.between(fechaEntrada, fechaSalida).coerceAtLeast(1) else 0
    val totalHospedaje = habitacion.precio.toBigDecimal().multiply(BigDecimal.valueOf(noches))
    val adelanto = adelantoStr.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val restante = totalHospedaje.subtract(adelanto).coerceAtLeast(BigDecimal.ZERO)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registrar Recepción", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CARD HABITACIÓN
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Habitación ${habitacion.numero}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cat: ${categorias.find { it.idCategoria == habitacion.idCategoria }?.descripcion ?: "-"}")
                        Text("Piso: ${pisos.find { it.idPiso == habitacion.idPiso }?.descripcion ?: "-"}")
                        Text("S/ ${habitacion.precio}", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // CARD CLIENTE
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("Detalle del Cliente") },
                    supportingContent = { Text(clienteSeleccionado?.let { "${it.nombre} ${it.apellido} | ${it.documento}" } ?: "Ningún cliente seleccionado") },
                    trailingContent = { FilledTonalButton(onClick = { showDialog = true }) { Icon(Icons.Default.PersonSearch, null) } }
                )
            }

            // CARD RESERVA
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Detalle de Reserva", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = fechaEntrada.toString(), onValueChange = {}, label = { Text("Entrada") }, modifier = Modifier.weight(1f), readOnly = true)
                        OutlinedTextField(value = fechaSalida?.toString() ?: "", onValueChange = {}, label = { Text("Salida") }, modifier = Modifier.weight(1f).clickable { openSalida = true }, readOnly = true, enabled = false)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = noches.toString(), onValueChange = {}, label = { Text("Noches") }, modifier = Modifier.weight(1f), readOnly = true)
                        OutlinedTextField(value = "S/ $totalHospedaje", onValueChange = {}, label = { Text("Total") }, modifier = Modifier.weight(1f), readOnly = true)
                    }
                    OutlinedTextField(value = adelantoStr, onValueChange = { if (it.all { c -> c.isDigit() }) adelantoStr = it }, label = { Text("Adelanto (S/)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = observacion, onValueChange = { observacion = it }, label = { Text("Observación") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                }
            }

            // ACCIONES
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onVolver, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("Volver") }
                Button(
                    onClick = {
                        if (clienteSeleccionado == null || fechaSalida == null) {
                            scope.launch { snackbarHostState.showSnackbar("Complete todos los campos") }
                            return@Button
                        }
                        isRegistering = true
                        val dto = RecepcionDto(
                            idRecepcion = null, idCliente = clienteSeleccionado!!.idPersona, idHabitacion = idHabitacion,
                            numero = habitacion.numero, categoriaNombre = categorias.find { it.idCategoria == habitacion.idCategoria }?.descripcion ?: "",
                            pisoNombre = pisos.find { it.idPiso == habitacion.idPiso }?.descripcion ?: "", detalleHabitacion = habitacion.detalle ?: "",
                            precioHabitacion = habitacion.precio.toDouble(), estadoHabitacion = "OCUPADO", tipoDocumento = clienteSeleccionado!!.tipoDocumento,
                            documento = clienteSeleccionado!!.documento, nombre = clienteSeleccionado!!.nombre, apellido = clienteSeleccionado!!.apellido,
                            correo = clienteSeleccionado!!.correo ?: "", precioInicial = totalHospedaje.toDouble(), adelanto = adelanto.toDouble(),
                            precioRestante = restante.toDouble(), totalPagado = adelanto.toDouble(), costoPenalidad = 0.0,
                            fechaEntrada = fechaEntrada.toString(), fechaSalida = fechaSalida.toString(), fechaSalidaConfirmacion = null,
                            observacion = observacion, estado = true
                        )
                        recepcionViewModel.registrarRecepcion(dto) { success ->
                            isRegistering = false
                            if (success) onVolver() else scope.launch { snackbarHostState.showSnackbar("Error al registrar") }
                        }
                    },
                    modifier = Modifier.weight(2f), enabled = !isRegistering
                ) { Text(if (isRegistering) "Procesando..." else "Confirmar Reserva") }
            }
        }
    }

    // DIÁLOGOS
    if (showDialog) {
        var busqueda by remember { mutableStateOf("") }
        val filtrados = clientes.filter { (it.documento ?: "").contains(busqueda, true) || (it.nombre ?: "").contains(busqueda, true) }
        AlertDialog(
            onDismissRequest = { showDialog = false }, icon = { Icon(Icons.Default.PersonSearch, null) },
            title = { Text("Seleccionar Cliente") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = busqueda, onValueChange = { busqueda = it }, label = { Text("Buscar DNI o Nombre") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(16.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(filtrados) { cliente ->
                            ListItem(headlineContent = { Text("${cliente.nombre} ${cliente.apellido}") }, supportingContent = { Text("DNI: ${cliente.documento}") },
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { clienteSeleccionado = cliente; showDialog = false })
                        }
                    }
                }
            }, confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Cerrar") } }
        )
    }

    if (openSalida) {
        val state = rememberDatePickerState(initialSelectedDateMillis = fechaSalida?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { openSalida = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        val selected = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        if (selected.isAfter(fechaEntrada)) fechaSalida = selected
                    }
                    openSalida = false
                }, enabled = state.selectedDateMillis != null) { Text("Confirmar") }
            }, dismissButton = { TextButton(onClick = { openSalida = false }) { Text("Cancelar") } }
        ) { DatePicker(state = state) }
    }
}