package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.gestionusuarios.data.remote.model.HabitacionDto
import com.example.gestionusuarios.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitacionFormScreen(
    id: Int? = null,
    viewModel: HabitacionViewModel,
    estadosViewModel: EstadoHabitacionViewModel,
    categoriaViewModel: CategoriaViewModel,
    pisoViewModel: PisoViewModel,
    onNavigateBack: () -> Unit
) {
    var numero by remember { mutableStateOf("") }
    var detalle by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var idPiso by remember { mutableStateOf<Int?>(null) }
    var idCategoria by remember { mutableStateOf<Int?>(null) }
    var idEstadoHabitacion by remember { mutableStateOf<Int?>(null) }
    var listaImagenes by remember { mutableStateOf(listOf<String>()) }
    var urlImagen by remember { mutableStateOf("") }
    val habitacionEdit by viewModel.habitacionEdit.collectAsStateWithLifecycle()

    val listaPisos by pisoViewModel.pisos.collectAsStateWithLifecycle()
    val listaCategorias by categoriaViewModel.categorias.collectAsStateWithLifecycle()
    val listaEstados by estadosViewModel.estados.collectAsStateWithLifecycle()
    val habitaciones by viewModel.habitaciones.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // Este efecto solo se ejecuta si hay un ID (Edición)

    LaunchedEffect(id) {
        if (id != null) {
            viewModel.cargarHabitacion(id)
        }
    }

    LaunchedEffect(habitacionEdit) {
        habitacionEdit?.let {
            numero = it.numero
            detalle = it.detalle ?: ""
            precio = it.precio.toString()
            idPiso = it.idPiso
            idCategoria = it.idCategoria
            idEstadoHabitacion = it.idEstadoHabitacion
            listaImagenes = it.urlsImagenes ?: emptyList()
        }
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text(if (id == null) "Nueva Habitación" else "Editar Habitación", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = numero, onValueChange = { numero = it }, label = { Text("Número de habitación") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = detalle, onValueChange = { detalle = it }, label = { Text("Detalle") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = precio, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) precio = it }, label = { Text("Precio (S/.)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(12.dp))

            SelectionSection(listaPisos, listaCategorias, listaEstados, idPiso, { idPiso = it }, idCategoria, { idCategoria = it }, idEstadoHabitacion, { idEstadoHabitacion = it })

            GallerySection(listaImagenes, urlImagen, { urlImagen = it }, { if(urlImagen.isNotBlank()){ listaImagenes += urlImagen; urlImagen = "" } }, { listaImagenes -= it })

            Button(
                onClick = {
                    val dto = HabitacionDto(id, numero, detalle, precio.toDoubleOrNull() ?: 0.0, idEstadoHabitacion ?: 1, idPiso ?: 1, idCategoria ?: 1, true, null, listaImagenes, null)
                    if (id == null) viewModel.registrar(dto) { if (it) onNavigateBack() }
                    else viewModel.actualizar(id ?: 0, dto) { if (it) onNavigateBack() }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading && numero.isNotBlank() && idPiso != null && idCategoria != null && idEstadoHabitacion != null,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                else Text(if (id == null) "REGISTRAR" else "ACTUALIZAR", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionSection(listaPisos: List<com.example.gestionusuarios.data.local.entity.PisoEntity>, listaCategorias: List<com.example.gestionusuarios.data.local.entity.CategoriaEntity>, listaEstados: List<com.example.gestionusuarios.data.local.entity.EstadoHabitacionEntity>, idPiso: Int?, onPisoChange: (Int) -> Unit, idCat: Int?, onCatChange: (Int) -> Unit, idEst: Int?, onEstChange: (Int) -> Unit) {

    @Composable
    fun DropdownField(label: String, selectedText: String, expanded: Boolean, onToggle: (Boolean) -> Unit, content: @Composable ColumnScope.() -> Unit) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { onToggle(!expanded) }) {
            OutlinedTextField(value = selectedText, onValueChange = {}, readOnly = true, label = { Text(label) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onToggle(false) }, content = content)
        }
    }

    var expPiso by remember { mutableStateOf(false) }
    var expCat by remember { mutableStateOf(false) }
    var expEst by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DropdownField("Piso", listaPisos.find { it.idPiso == idPiso }?.descripcion ?: "Seleccione", expPiso, { expPiso = it }) {
            listaPisos.forEach { p -> DropdownMenuItem(text = { Text(p.descripcion) }, onClick = { onPisoChange(p.idPiso); expPiso = false }) }
        }
        DropdownField("Categoría", listaCategorias.find { it.idCategoria == idCat }?.descripcion ?: "Seleccione", expCat, { expCat = it }) {
            listaCategorias.forEach { c -> DropdownMenuItem(text = { Text(c.descripcion) }, onClick = { onCatChange(c.idCategoria); expCat = false }) }
        }
        DropdownField("Estado", listaEstados.find { it.idEstadoHabitacion == idEst }?.descripcion ?: "Seleccione", expEst, { expEst = it }) {
            listaEstados.forEach { e -> DropdownMenuItem(text = { Text(e.descripcion) }, onClick = { onEstChange(e.idEstadoHabitacion); expEst = false }) }
        }
    }
}

@Composable
fun GallerySection(lista: List<String>, urlInput: String, onUrlChange: (String) -> Unit, onAdd: () -> Unit, onDelete: (String) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = urlInput, onValueChange = onUrlChange, label = { Text("URL Imagen") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
            IconButton(onClick = onAdd) { Icon(Icons.Default.Add, "Agregar") }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            items(lista) { url ->
                Box(modifier = Modifier.size(80.dp)) {
                    AsyncImage(model = url, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).background(Color.Gray))
                    IconButton(onClick = { onDelete(url) }, modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.Black.copy(0.5f), RoundedCornerShape(12.dp))) {
                        Icon(Icons.Default.Close, "Eliminar", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}