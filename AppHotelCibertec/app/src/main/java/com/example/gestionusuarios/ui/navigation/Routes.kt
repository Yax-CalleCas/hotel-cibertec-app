package com.example.gestionusuarios.ui.navigation

sealed class Routes(val route: String) {

    // --- AUTENTICACIÓN ---
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object RegistrarCliente : Routes("registrar_cliente")
    // Nueva ruta para el catálogo

    data object CatalogoCliente : Routes("catalogo_cliente")

    data object DetalleReserva : Routes("detalle_reserva/{habitacionId}/{numeroHabitacion}/{precioPorNoche}") {
        fun createRoute(id: Int, numero: String, precio: Double): String {
            val encodedNumero = java.net.URLEncoder.encode(numero, "UTF-8")
            val formattedPrecio = String.format(java.util.Locale.US, "%.2f", precio)
            return "detalle_reserva/$id/$encodedNumero/$formattedPrecio"
        }
    }
    data object RecepcionHabitacionesOcupadas : Routes("lista_ocupadas")

    // --- PANEL PRINCIPAL ---
    data object Home : Routes("home")

    // --- GESTIÓN DE HABITACIONES ---


    data object HabitacionList : Routes("habitacion_list")
    data object GestionHabitaciones : Routes("gestion_habitaciones")
    data object EstadoHabitacionList : Routes("estado_habitacion_list")
    data object RecepcionHabitacionesDisponibles : Routes("recepcion_habitaciones_disponibles")
    data object ListaOcupadas : Routes("lista_ocupadas")


    object HabitacionForm : Routes("habitacion_form/{id}") {
        fun createRoute(id: Int?) = "habitacion_form/${id ?: 0}"
    }

    object HabitacionDetailScreen : Routes("habitacion_detail/{id}") {
        fun createRoute(id: Int) = "habitacion_detail/$id"
    }


    object DetalleHabitacionAlquilerScreen : Routes("alquiler_habitacion/{idHabitacion}") {
        fun createRoute(idHabitacion: Int) = "alquiler_habitacion/$idHabitacion"
    }

    object DetalleHabitacion : Routes("detalle_habitacion/{idHabitacion}") {
        fun createRoute(idHabitacion: Int) = "detalle_habitacion/$idHabitacion"
    }

    object DetalleVentasHabitaciones : Routes("detalle_ventas_habitaciones/{idHabitacion}") {
        fun createRoute(idHabitacion: Int) = "detalle_ventas_habitaciones/$idHabitacion"
    }

    object GestionSalidasDetalle : Routes("gestion_salidas_detalle/{idHabitacion}") {
        fun createRoute(idHabitacion: Int) = "gestion_salidas_detalle/$idHabitacion"
    }



    // --- GESTIÓN DE PRODUCTOS ---
    data object ProductoList : Routes("producto_list")
    object ProductoForm : Routes("producto_form/{productoId}") {
        fun createRoute(productoId: Int?) = "producto_form/${productoId ?: 0}"
    }

    object ProductoDetalle : Routes("producto_detalle/{productoId}") {
        fun createRoute(productoId: Int) = "producto_detalle/$productoId"
    }

    // --- GESTIÓN DE PERSONAS ---
    data object GestionPersonas : Routes("gestion_personas")
    data object ListarUsuarios : Routes("ListarUsuarios")
    data object NuevoUsuario : Routes("nuevo_usuario")
    object EditarUsuario : Routes("editar_usuario/{userId}") {
        fun createRoute(userId: Int) = "editar_usuario/$userId"
    }

    data object Locales : Routes("locales")
    data object GestionSalidasLista : Routes("gestion_salidas_lista")
    data object GestionClientes : Routes("gestion_clientes")

}
