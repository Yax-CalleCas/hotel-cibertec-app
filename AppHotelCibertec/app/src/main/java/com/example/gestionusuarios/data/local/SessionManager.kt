package com.example.gestionusuarios.data.local

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {

    private val prefs by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "auth_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e("SessionManager", "Error al inicializar EncryptedSharedPreferences", e)
            context.getSharedPreferences("fallback_prefs", Context.MODE_PRIVATE)
        }
    }

    /**
     * Guarda el token y el momento exacto del login para control de tiempo.
     */
    fun saveToken(token: String) {
        prefs.edit()
            .putString("token", token)
            .putLong("login_time", System.currentTimeMillis())
            .apply()
    }

    fun getToken(): String? = prefs.getString("token", null)

    /**
     * Verifica si el token existe y si está dentro del margen de 30 segundos.
     */
    fun isSessionValid(): Boolean {
        val token = getToken()
        if (token.isNullOrBlank()) return false

        val loginTime = prefs.getLong("login_time", 0)
        val currentTime = System.currentTimeMillis()
        val sessionDuration = 30 * 1000 // 30,000 ms = 30 segundos

        return (currentTime - loginTime) < sessionDuration
    }


    // En SessionManager.kt
    fun saveUserData(nombre: String, apellido: String) {
        prefs.edit()
            .putString("user_name", "$nombre $apellido")
            .apply()
    }

    fun getNombre(): String? = prefs.getString("user_name", null)
    /**
     * Verifica si el usuario está logueado (sin validar tiempo, útil para rutas públicas).
     */
    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    /**
     * Borra todo el contenido de la sesión.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}