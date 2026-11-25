package com.example.ecorecicla.mobile.navigation

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: Routes.kt
// RESPONSABILIDAD: Centralización de rutas de navegación.
// ======================================================

// Clase sellada que define las pantallas de la aplicación.
// Permite controlar las rutas de forma organizada y segura.

sealed class Routes(val route: String) {

    // Pantalla de inicio de sesión.
    object Login : Routes("login")

    // Pantalla de registro de usuarios.
    object Register : Routes("register")

    // Pantalla principal del usuario.
    object Dashboard : Routes("dashboard")

    // Pantalla de registro de reciclaje.
    object Recycling : Routes("recycling")

    // Pantalla de consulta de puntos.
    object Points : Routes("points")

    // Catálogo de recompensas.
    object Rewards : Routes("rewards")

    // Pantalla de canje de recompensas.
    object Redemption : Routes("redemption")

    // Historial de canjes.
    object History : Routes("history")

    // Perfil del usuario.
    object Profile : Routes("profile")
}