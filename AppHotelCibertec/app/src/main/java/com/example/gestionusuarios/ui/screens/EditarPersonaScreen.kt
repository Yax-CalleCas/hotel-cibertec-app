package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gestionusuarios.ui.viewmodel.PersonaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPersonaScreen(
    idPersona: Int,
    viewModel: PersonaViewModel,
    onBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var documento by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val personaCargada by viewModel.personaSeleccionada.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(idPersona) { viewModel.buscarPersona(idPersona) }
    LaunchedEffect(personaCargada) {
        personaCargada?.let {
            nombre = it.nombre; apellido = it.apellido
            correo = it.correo; documento = it.documento
            fotoUrl = it.fotoUrl ?: ""
            estado = it.estado ?: true
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Usuario") },
            text = { Text("¿Estás seguro de que deseas eliminar este usuario? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarPersona(idPersona) { success ->
                        if (success) onBack()
                    }
                    showDeleteDialog = false
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sección de Foto
            AsyncImage(
                model = fotoUrl.ifBlank { "https://cdn-icons-png.flaticon.com/512/3135/3135715.png" },
                contentDescription = "Foto perfil",
                modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre", icon = Icons.Default.Person)
                    EditTextField(value = apellido, onValueChange = { apellido = it }, label = "Apellido", icon = Icons.Default.Person)
                    EditTextField(value = correo, onValueChange = { correo = it }, label = "Correo", icon = Icons.Default.Email)
                    EditTextField(value = fotoUrl, onValueChange = { fotoUrl = it }, label = "URL de Foto", icon = Icons.Default.Image)
                    DniTextField(value = documento, onValueChange = { documento = it })

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Usuario Activo")
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(checked = estado, onCheckedChange = { estado = it })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    personaCargada?.let {
                        viewModel.actualizarPersona(idPersona, it.copy(
                            nombre = nombre, apellido = apellido, correo = correo,
                            documento = documento, fotoUrl = fotoUrl, estado = estado
                        )) { success -> if (success) onBack() }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text("GUARDAR CAMBIOS")
            }
        }
    }
}

@Composable
fun DniTextField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= 8 && it.all { char -> char.isDigit() }) onValueChange(it) },
        label = { Text("Número de Documento (DNI)") },
        leadingIcon = { Icon(Icons.Default.Badge, null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun EditTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        modifier = Modifier.fillMaxWidth()
    )
}