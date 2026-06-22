package com.example.gestionusuarios.ui.navigation

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult

// FacebookAuthHelper.kt
class FacebookAuthHelper(
    val callbackManager: CallbackManager, // Recibe el manager desde fuera
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    val callback = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) {
            onResult(result.accessToken.token)
        }
        override fun onCancel() { onError("Cancelado por el usuario") }
        override fun onError(error: FacebookException) { onError(error.message ?: "Error desconocido") }
    }
}