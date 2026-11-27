package com.example.ecorecicla.mobile.navigation

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: Routes.kt
// RESPONSABILIDAD: Centralizar las rutas de navegación.
// ======================================================

sealed class Routes(val route: String) {

    // Autenticación.
    object Login : Routes("login")

    object Register : Routes("register")

    // Dashboard principal.
    object Dashboard : Routes("dashboard")

    // Registro de reciclajes.
    object Recycling : Routes("recycling")

    // Catálogo de recompensas.
    object Rewards : Routes("rewards")

    // Historial de movimientos.
    object History : Routes("history")

    // Premios adquiridos y códigos de reclamación.
    object MyRewards : Routes("my_rewards")

    // Perfil independiente del usuario.
    object Profile : Routes("profile")
}