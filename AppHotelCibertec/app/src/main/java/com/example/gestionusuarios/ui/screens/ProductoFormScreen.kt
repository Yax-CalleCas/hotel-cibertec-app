import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.gestionusuarios.data.remote.model.Producto
import com.example.gestionusuarios.ui.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    viewModel: ProductoViewModel,
    productoId: Int? = null,
    onNavigateBack: () -> Unit
) {
    val productos by viewModel.productos.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var nombre by remember { mutableStateOf("") }
    var detalle by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }

    val isNombreError = nombre.isNotEmpty() && nombre.length < 3
    val isPrecioError = precio.isNotEmpty() && precio.toDoubleOrNull() == null
    val isFormValid = nombre.isNotBlank() && precio.isNotEmpty() && !isPrecioError && !isLoading

    LaunchedEffect(productoId, productos) {
        productoId?.let { id ->
            productos.find { it.idProducto == id }?.let {
                nombre = it.nombre
                detalle = it.detalle ?: ""
                precio = it.precio.toString()
                cantidad = it.cantidad.toString()
                imagenUrl = it.imagenUrl ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (productoId == null) "NUEVO PRODUCTO" else "EDITAR PRODUCTO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant)) {
                    AsyncImage(
                        model = imagenUrl,
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (imagenUrl.isBlank()) {
                        Icon(Icons.Default.Image, null, modifier = Modifier.align(Alignment.Center).size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth(),
                isError = isNombreError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = detalle,
                onValueChange = { detalle = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = precio,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) precio = it },
                    label = { Text("Precio") },
                    modifier = Modifier.weight(1f),
                    isError = isPrecioError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { if (it.all { char -> char.isDigit() }) cantidad = it },
                    label = { Text("Stock") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            OutlinedTextField(
                value = imagenUrl,
                onValueChange = { imagenUrl = it },
                label = { Text("URL de la Imagen") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    val prod = Producto(productoId, nombre, detalle, precio.toDoubleOrNull() ?: 0.0, cantidad.toIntOrNull() ?: 0, true, imagenUrl)
                    if (productoId == null) viewModel.registrar(prod) { if (it) onNavigateBack() }
                    else viewModel.actualizar(productoId, prod) { if (it) onNavigateBack() }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isFormValid,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text(if (productoId == null) "REGISTRAR" else "ACTUALIZAR", fontWeight = FontWeight.Bold)
            }
        }
    }
}