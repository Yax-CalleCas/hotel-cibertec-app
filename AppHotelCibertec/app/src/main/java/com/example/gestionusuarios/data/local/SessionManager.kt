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
            Log.e("SessionManager", "Error al inicializar EncryptedSharedPreferences, usando fallback", e)
            context.getSharedPreferences("fallback_prefs", Context.MODE_PRIVATE)
        }
    }

    fun saveToken(token: String) {
        prefs.edit()
            .putString("token", token)
            .putLong("login_time", System.currentTimeMillis())
            .apply()
    }
//token de la sesion
    fun getToken(): String? = prefs.getString("token", null)

    fun isSessionValid(): Boolean {
        val token = getToken()
        if (token.isNullOrBlank()) return false

        val loginTime = prefs.getLong("login_time", 0L)
        val currentTime = System.currentTimeMillis()
        val sessionDuration = 24 * 60 * 60 * 1000L // 24 horas

        return (currentTime - loginTime) < sessionDuration
    }

    fun saveUserData(nombre: String, apellido: String) {
        prefs.edit()
            .putString("user_name", "$nombre $apellido")
            .apply()
    }
    //nombre del usuario
    fun getNombre(): String? = prefs.getString("user_name", null)

    fun saveUserRole(idTipoPersona: Int) {
        Log.d("DEBUG_SESSION", "Guardando rol: $idTipoPersona")
        prefs.edit()
            .putInt("user_role", idTipoPersona)
            .apply()
    }

    fun getUserRole(): Int {
        val rol = prefs.getInt("user_role", 0)
        Log.d("DEBUG_SESSION", "Recuperando rol: $rol")
        return rol
    }

    fun isLoggedIn(): Boolean {
        val tokenExists = !getToken().isNullOrBlank()
        val valid = isSessionValid()
        return tokenExists && valid
    }
    //reservas
    fun getIdPersona(): Int = prefs.getInt("id_persona", 0)
    fun saveIdPersona(id: Int) = prefs.edit().putInt("id_persona", id).apply()

   //cerrar sesion
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}

