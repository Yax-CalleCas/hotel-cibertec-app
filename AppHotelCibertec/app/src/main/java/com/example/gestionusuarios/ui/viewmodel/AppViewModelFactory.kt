package com.example.gestionusuarios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestionusuarios.data.local.SessionManager
import com.example.gestionusuarios.data.repository.*

class AppViewModelFactory(
    private val loginRepository: LoginRepository,
    private val personRepository: PersonRepository,
    private val productoRepository: ProductoRepository,
    private val habitacionRepository: HabitacionRepository,
    private val estadoRepository: EstadoHabitacionRepository,
    private val categoriaRepository: CategoriaRepository,
    private val pisoRepository: PisoRepository,
    private val ventaRepository: VentaRepository,
    private val recepcionRepository: RecepcionRepository,
    private val sessionManager: SessionManager,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(sessionManager, loginRepository)
            modelClass.isAssignableFrom(PersonaViewModel::class.java) -> PersonaViewModel(personRepository, sessionManager)
            modelClass.isAssignableFrom(ProductoViewModel::class.java) -> ProductoViewModel(productoRepository)
            modelClass.isAssignableFrom(HabitacionViewModel::class.java) -> HabitacionViewModel(habitacionRepository, estadoRepository, categoriaRepository, recepcionRepository)
            modelClass.isAssignableFrom(EstadoHabitacionViewModel::class.java) -> EstadoHabitacionViewModel(estadoRepository)
            modelClass.isAssignableFrom(PisoViewModel::class.java) -> PisoViewModel(pisoRepository)
            modelClass.isAssignableFrom(VentaViewModel::class.java) -> VentaViewModel(ventaRepository)
            modelClass.isAssignableFrom(RecepcionViewModel::class.java) -> RecepcionViewModel(recepcionRepository, sessionManager)
            modelClass.isAssignableFrom(CategoriaViewModel::class.java) -> CategoriaViewModel(categoriaRepository)

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}