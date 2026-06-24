package com.example.gestionusuarios

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.example.gestionusuarios.data.local.AppDatabase
import com.example.gestionusuarios.data.local.SessionManager
import com.example.gestionusuarios.data.remote.RetrofitClient
import com.example.gestionusuarios.data.remote.api.*
import com.example.gestionusuarios.data.repository.*
import com.example.gestionusuarios.ui.navigation.AppNavigation
import com.example.gestionusuarios.ui.navigation.FacebookAuthHelper
import com.example.gestionusuarios.ui.navigation.LocalViewModelFactory
import com.example.gestionusuarios.ui.theme.GestionUsuariosTheme
import com.example.gestionusuarios.ui.viewmodel.AppViewModelFactory
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

class MainActivity : ComponentActivity() {

    lateinit var fbCallbackManager: CallbackManager
    lateinit var fbHelper: FacebookAuthHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicialización de Facebook
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        FacebookSdk.setClientToken(getString(R.string.facebook_client_token))
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)

        // Inicializamos el CallbackManager único
        fbCallbackManager = CallbackManager.Factory.create()

        // Inicializamos el Helper pasando el manager para que ambos estén sincronizados
        fbHelper = FacebookAuthHelper(
            callbackManager = fbCallbackManager,
            onResult = { token ->
                Log.d("FB_AUTH", "Token recibido: $token")
            },
            onError = { error ->
                Log.e("FB_AUTH", "Error en login social: $error")
            }
        )

        // 2. Inicialización de red
        RetrofitClient.init(this)

        enableEdgeToEdge()

        setContent {
            GestionUsuariosTheme {
                val sessionManager = remember { SessionManager(this) }
                val db = remember { AppDatabase.getDatabase(this) }

                val personRepository = remember {
                    PersonRepository(RetrofitClient.createService(PersonaService::class.java), db.personaDao())
                }

                val recepcionRepository = remember {
                    RecepcionRepository(
                        recepcionService = RetrofitClient.createService(RecepcionService::class.java),
                        recepcionDao = db.recepcionDao(),
                        personRepository = personRepository, // <-- Ahora es reconocido
                        habitacionDao = db.habitacionDao()
                    )
                }
                // 3. Factory de ViewModels
                val factory = remember {
                    AppViewModelFactory(
                        loginRepository = LoginRepository(RetrofitClient.createService(LoginService::class.java)),
                        personRepository = PersonRepository(RetrofitClient.createService(PersonaService::class.java), db.personaDao()),
                        productoRepository = ProductoRepository(RetrofitClient.createService(ProductoService::class.java), db.productoDao()),
                        habitacionRepository = HabitacionRepository(RetrofitClient.createService(HabitacionService::class.java), db.habitacionDao()),
                        estadoRepository = EstadoHabitacionRepository(RetrofitClient.createService(EstadoHabitacionService::class.java), db.estadoHabitacionDao()),
                        categoriaRepository = CategoriaRepository(RetrofitClient.createService(CategoriaService::class.java), db.categoriaDao()),
                        pisoRepository = PisoRepository(RetrofitClient.createService(PisoService::class.java), db.pisoDao()),
                        ventaRepository = VentaRepository(RetrofitClient.createService(VentaService::class.java), db.ventaDao()),
                        sessionManager = sessionManager,

                        recepcionRepository = RecepcionRepository(
                            recepcionService = RetrofitClient.createService(RecepcionService::class.java),
                            recepcionDao = db.recepcionDao(),
                            personRepository = personRepository,
                            habitacionDao = db.habitacionDao()
                        ))
                }

                // 4. Inyección de dependencias y navegación
                CompositionLocalProvider(LocalViewModelFactory provides factory) {
                    AppNavigation(sessionManager = sessionManager)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // El callbackManager captura el resultado y lo redirige al helper
        fbCallbackManager.onActivityResult(requestCode, resultCode, data)
    }
}