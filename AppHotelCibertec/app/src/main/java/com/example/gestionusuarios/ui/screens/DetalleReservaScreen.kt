package com.example.gestionusuarios.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestionusuarios.ui.navigation.LocalViewModelFactory
import com.example.gestionusuarios.ui.viewmodel.RecepcionViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReservaScreen(
    habitacionId: Int,
    numeroHabitacion: String,
    precioPorNoche: Double,
    onReservaExitosa: () -> Unit,
    viewModel: RecepcionViewModel = viewModel(factory = LocalViewModelFactory.current)
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val cliente by viewModel.clienteLogueado.collectAsState()

    // Estados para fechas
    var fechaEntrada by remember { mutableStateOf("") }
    var fechaSalida by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf<String?>(null) }

    // Cargar datos del cliente al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarDatosCliente()
    }

    // Manejo de mensajes de error/éxito
    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensaje()
        }
    }

    val esValido = fechaEntrada.isNotEmpty() && fechaSalida.isNotEmpty() && cliente != null

    Scaffold(topBar = { TopAppBar(title = { Text("Confirmar Reserva") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- DETALLE DE HABITACIÓN ---
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Habitación: $numeroHabitacion", style = MaterialTheme.typography.titleLarge)
                    Text("Precio: S/ $precioPorNoche / noche", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- DETALLE CLIENTE (Autocompletado) ---
            Text("Cliente", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = cliente?.let { "${it.nombre} ${it.apellido}" } ?: "Cargando...",
                onValueChange = {},
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            OutlinedTextField(
                value = cliente?.documento ?: "...",
                onValueChange = {},
                label = { Text("Documento de Identidad") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- SELECTORES DE FECHA ---
            OutlinedTextField(
                value = fechaEntrada,
                onValueChange = {},
                label = { Text("Fecha de Entrada") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = "entrada" },
                enabled = false
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = fechaSalida,
                onValueChange = {},
                label = { Text("Fecha de Salida") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = "salida" },
                enabled = false
            )

            // DatePicker Logic
            showDatePicker?.let { tipo ->
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = null },
                    confirmButton = {
                        TextButton(onClick = {
                            val selectedDate = datePickerState.selectedDateMillis?.let {
                                java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.of("UTC")).toLocalDate().toString()
                            } ?: ""
                            if (tipo == "entrada") fechaEntrada = selectedDate else fechaSalida = selectedDate
                            showDatePicker = null
                        }) { Text("Aceptar") }
                    }
                ) { DatePicker(state = datePickerState) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        viewModel.solicitarReservaConFechas(habitacionId, fechaEntrada, fechaSalida, precioPorNoche) {
                            if (it) onReservaExitosa()
                        }
                    },
                    enabled = esValido,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Confirmar Reserva")
                }
            }
        }
    }
}