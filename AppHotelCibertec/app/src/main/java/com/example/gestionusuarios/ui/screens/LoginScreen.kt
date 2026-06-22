package com.example.gestionusuarios.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.gestionusuarios.MainActivity
import com.example.gestionusuarios.R
import com.example.gestionusuarios.data.remote.model.LoginUiState
import com.example.gestionusuarios.ui.viewmodel.LoginViewModel
import com.example.gestionusuarios.ui.viewmodel.PersonaViewModel
import com.example.gestionusuarios.ui.viewmodel.UiEvent
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    personaViewModel: PersonaViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegisterCliente: () -> Unit
) {
    val context = LocalContext.current as MainActivity
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val loginState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Inicializar el botón de Facebook oculto
    val loginButton = remember {
        LoginButton(context).apply {
            setPermissions("email", "public_profile")
            registerCallback(context.fbCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val request = GraphRequest.newMeRequest(result.accessToken) { obj, _ ->
                        val email = obj?.optString("email") ?: ""
                        val name = obj?.optString("name")?.split(" ") ?: listOf("Usuario", "Social")
                        viewModel.validarUsuarioEnBackend(result.accessToken.token, name[0], name.getOrElse(1) { "" }, email)
                    }
                    val params = android.os.Bundle().apply { putString("fields", "name,email") }
                    request.parameters = params
                    request.executeAsync()
                }

                override fun onCancel() {}
                override fun onError(e: FacebookException) {}
            })
        }
    }
// 1. Esto gestiona los mensajes de error/info (Snackbars)
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is UiEvent.ShowSnackbar) {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(loginState) {
        if (loginState is LoginUiState.Success) {
            val data = (loginState as LoginUiState.Success).data
            personaViewModel.setUsuarioLogueado(data)
            personaViewModel .personaSeleccionada;
            onLoginSuccess()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        // Usamos un Box con scroll para asegurar que sea responsive
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp), // Aumentamos margen para elegancia
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo con un ligero tratamiento de sombra o estilo más suave
            Image(
                painter = painterResource(id = R.drawable.logohotel),
                contentDescription = "Logo",
                modifier = Modifier.size(110.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Bienvenido de nuevo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "Ingresa a tu cuenta para gestionar reservas",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            // TextFields con estilo refinado (menos borde oscuro, más sutil)
            LoginTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = "Correo electrónico",
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = "Contraseña",
                icon = Icons.Default.Lock,
                isPassword = true,
                passwordVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón principal con elevación sutil y curva más orgánica
            Button(
                onClick = { focusManager.clearFocus(); viewModel.iniciarSesion(usuario, contrasena) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = loginState !is LoginUiState.Loading
            ) {
                if (loginState is LoginUiState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                else Text("ACCEDER", style = MaterialTheme.typography.titleMedium, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Separador elegante tipo "Divider"
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(" o ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Facebook estilizado (Sin bordes agresivos)
            OutlinedButton(
                onClick = { loginButton.performClick() },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.iconofacebook),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified // Mantiene el color original del logo
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Continuar con Facebook", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// Sub-componente para limpiar el código y mantener elegancia
@Composable
fun LoginTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector, isPassword: Boolean = false, passwordVisible: Boolean = false, onToggleVisibility: () -> Unit = {}) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = if (isPassword) {
            { IconButton(onClick = onToggleVisibility) { Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null) } }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    )
}