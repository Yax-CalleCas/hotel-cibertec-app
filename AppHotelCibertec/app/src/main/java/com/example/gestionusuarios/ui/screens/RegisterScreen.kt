import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.gestionusuarios.data.remote.model.Persona
import com.example.gestionusuarios.data.remote.model.TipoPersonaDto
import com.example.gestionusuarios.ui.viewmodel.PersonaViewModel
import com.example.gestionusuarios.data.remote.model.RegistroUiState


@Composable
fun RegistroPersonaScreen(viewModel: PersonaViewModel, onBack: () -> Unit) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val tipos by viewModel.tiposPersona.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var uiState by remember { mutableStateOf(RegistroUiState()) }

    LaunchedEffect(Unit) { viewModel.cargarTiposPersona() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    // Definición clara del lambda para evitar mismatch de tipos
    val onRegister: () -> Unit = {
        uiState.selectedTipo?.let { tipo ->
            val nuevaPersona = Persona(
                tipoDocumento = "DNI",
                documento = uiState.documento,
                nombre = uiState.nombre,
                apellido = uiState.apellido,
                correo = uiState.correo,
                clave = uiState.clave,
                idTipoPersona = tipo.idTipoPersona,
                fotoUrl = null,
                estado = true,
                fechaCreacion = null
            )
            viewModel.registrarPersona(nuevaPersona) { success -> if (success) onBack() }
        }
    }

    RegistroPersonaContent(
        uiState = uiState,
        onStateChange = { uiState = it },
        tipos = tipos,
        isLoading = isLoading,
        snackbarHostState = snackbarHostState,
        onRegister = onRegister,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroPersonaContent(
    uiState: RegistroUiState,
    onStateChange: (RegistroUiState) -> Unit,
    tipos: List<TipoPersonaDto>,
    isLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    onRegister: () -> Unit,
    onBack: () -> Unit
) {
    // Validamos que el documento tenga exactamente 8 dígitos según tu requerimiento
    val isFormValid = uiState.nombre.isNotBlank() &&
            uiState.apellido.isNotBlank() &&
            uiState.documento.length == 8 &&
            uiState.correo.contains("@") &&
            uiState.clave.length >= 6 &&
            uiState.selectedTipo != null

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Nuevo Usuario",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Registro de personal del sistema",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // HEADER VISUAL
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Completa el formulario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "Los campos marcados son obligatorios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // CARD: DATOS PERSONALES
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        "Datos personales",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = uiState.nombre,
                        onValueChange = { onStateChange(uiState.copy(nombre = it)) },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.apellido,
                        onValueChange = { onStateChange(uiState.copy(apellido = it)) },
                        label = { Text("Apellido") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.documento,
                        onValueChange = {
                            if (it.length <= 8 && it.all { c -> c.isDigit() }) {
                                onStateChange(uiState.copy(documento = it))
                            }
                        },
                        label = { Text("Documento (8 dígitos)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    TipoPersonaSelector(
                        tipos,
                        uiState.selectedTipo
                    ) {
                        onStateChange(uiState.copy(selectedTipo = it))
                    }
                }
            }

            // CARD: CREDENCIALES
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        "Acceso al sistema",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = uiState.correo,
                        onValueChange = { onStateChange(uiState.copy(correo = it)) },
                        label = { Text("Correo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    OutlinedTextField(
                        value = uiState.clave,
                        onValueChange = { onStateChange(uiState.copy(clave = it)) },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    onStateChange(
                                        uiState.copy(
                                            isPasswordVisible = !uiState.isPasswordVisible
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    if (uiState.isPasswordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (uiState.isPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
            }

            // BOTÓN PRINCIPAL
            Button(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = !isLoading && isFormValid,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "CREAR USUARIO",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoPersonaSelector(
    tipos: List<TipoPersonaDto>,
    selectedTipo: TipoPersonaDto?,
    onTipoSelected: (TipoPersonaDto) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedTipo?.descripcion ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de Persona") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            tipos.forEach { tipo ->
                DropdownMenuItem(
                    text = { Text(tipo.descripcion) },
                    onClick = { onTipoSelected(tipo); expanded = false }
                )
            }
        }
    }
}