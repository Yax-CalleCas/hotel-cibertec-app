#  Sistema de Gestión Hotelera - Android App

Aplicación móvil desarrollada para la administración integral de hoteles, permitiendo gestionar habitaciones, usuarios, servicios, ventas y operaciones hoteleras desde una interfaz moderna e intuitiva.

---

##  Descripción

El Sistema de Gestión Hotelera es una solución móvil orientada a optimizar los procesos administrativos de un hotel mediante la integración de tecnologías modernas para Android y servicios backend basados en Spring Boot.

La aplicación permite controlar el estado de habitaciones, registrar ventas, administrar usuarios y visualizar información operativa en tiempo real.

##  Tecnologías Utilizadas

###  Desarrollo Android

- Kotlin
- Jetpack Compose
- Material Design 3
- Navigation Compose

###  Arquitectura y Consumo de APIs

- MVVM (Model - View - ViewModel)
- Repository Pattern
- Retrofit 2
- OkHttp
- Coroutines
- StateFlow / Flow

###  Persistencia Local

- Room Database

###  Backend

- Spring Boot
- API REST
- JWT Authentication

---

##  Funcionalidades

###  Seguridad

- Inicio de sesión con autenticación JWT
- Control de acceso basado en roles

###  Gestión de Usuarios

- Registro y administración de usuarios
- Asignación de roles y permisos

###  Gestión Hotelera

- Administración de habitaciones
- Gestión de pisos
- Gestión de categorías
- Control de estados:
  - Disponible
  - Ocupada
  - Mantenimiento

###  Gestión de Servicios

- Registro de servicios hoteleros
- Asociación de servicios a ventas

###  Gestión de Ventas

- Registro de ventas
- Consulta de detalle de ventas
- Búsqueda de ventas por ID
- Seguimiento de operaciones realizadas

###  Dashboard

- Visualización de información operativa
- Indicadores de ocupación y actividad

---

##  Estructura del Proyecto

```text
AppHotelCibertec/
│
├── data/
│   ├── local/          # Room Database
│   ├── remote/         # Retrofit y API Services
│   ├── repository/     # Repositorios
│   └── model/          # Modelos de datos
│
├── ui/
│   ├── screens/        # Pantallas
│   ├── components/     # Componentes reutilizables
│   ├── navigation/     # Navegación
│   ├── viewmodel/      # ViewModels
│   └── theme/          # Temas y estilos
│
└── MainActivity.kt
```

---

## ⚙️ Requisitos

- Android Studio Hedgehog o superior
- Android SDK 30+
- JDK 17
- Gradle 8+
- Backend Spring Boot en ejecución

---

## 🔧 Instalación

### 1️ Clonar el repositorio

```bash
git clone https://github.com/Yax-CalleCas/hotel-cibertec-app.git
```

### 2️ Abrir el proyecto

```bash
File > Open > Seleccionar carpeta del proyecto
```

### 3️ Configurar el Backend

Modificar la URL base utilizada por Retrofit:

```kotlin
const val BASE_URL = "http://tu-ip-o-servidor:8081/"
```

Ejemplo:

```kotlin
const val BASE_URL = "http://192.168.1.10:8081/"
```

### 4️ Configurar Android SDK

Verifica que exista el archivo:

```properties
local.properties
```

Ejemplo:

```properties
sdk.dir=C:\\Users\\Usuario\\AppData\\Local\\Android\\Sdk
```

### 5️ Ejecutar la aplicación

- Sincronizar dependencias Gradle.
- Conectar un dispositivo Android o iniciar un emulador.
- Ejecutar la aplicación desde Android Studio.

---

## 🔗 Backend Relacionado

Repositorio del backend:

```text
https://github.com/Yax-CalleCas/Sistema-de-Gesti-n-Hotelera
```

---

##  Capturas de Pantalla

Puedes agregar aquí imágenes de:

- Login
- Dashboard
- Gestión de Habitaciones
- Gestión de Ventas
- Gestión de Usuarios

Ejemplo:

```markdown
![Login](screenshots/login.png)
![Dashboard](screenshots/dashboard.png)
```

---

##  Arquitectura

```text
UI (Jetpack Compose)
        │
        ▼
ViewModel (MVVM)
        │
        ▼
Repository
        │
 ┌──────┴──────┐
 ▼             ▼
API         Room DB
(Retrofit)
```

---

##  Objetivos del Proyecto

- Digitalizar la administración hotelera.
- Optimizar los procesos operativos.
- Facilitar la gestión de habitaciones y ventas.
- Centralizar la información en una única plataforma móvil.
- Aplicar buenas prácticas de desarrollo Android moderno.

---

##  Proyecto Académico

Este proyecto fue desarrollado como parte de la formación académica en **Cibertec**, aplicando conocimientos de desarrollo móvil, arquitectura de software, bases de datos y servicios web.

---

## 📄 Licencia

Proyecto desarrollado con fines educativos y académicos.

© 2026 Paul Calle Cas - Cibertec
