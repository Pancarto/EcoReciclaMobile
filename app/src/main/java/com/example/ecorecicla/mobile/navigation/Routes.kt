package com.example.ecorecicla.mobile.navigation

// Rutas centralizadas de navegación.
sealed class Routes(val route: String) {

    object Login : Routes("login")

    object Register : Routes("register")

    object Dashboard : Routes("dashboard")

    object Recycling : Routes("recycling")

    object Rewards : Routes("rewards")

    object History : Routes("history")
}