package com.example.ecorecicla.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecorecicla.mobile.ui.screens.LoginScreen
import com.example.ecorecicla.mobile.ui.screens.RegisterScreen

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: AppNavigation.kt
// RESPONSABILIDAD: Gestión centralizada de navegación.
// ARQUITECTURA: Single Activity.
// TECNOLOGÍA: Navigation Compose.
// ======================================================

@Composable
fun AppNavigation() {

    // Controlador principal de navegación.
    val navController = rememberNavController()

    // Contenedor de las pantallas de la aplicación.
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {

        // ==============================================
        // PANTALLA DE INICIO DE SESIÓN
        // ==============================================

        composable(route = Routes.Login.route) {

            LoginScreen(

                // Navegar hacia la pantalla de registro.
                onRegister = {

                    navController.navigate(Routes.Register.route) {

                        // Evita duplicar el destino.
                        launchSingleTop = true
                    }
                }
            )
        }

        // ==============================================
        // PANTALLA DE REGISTRO
        // ==============================================

        composable(route = Routes.Register.route) {

            RegisterScreen(

                // Regresar a la pantalla anterior.
                onLoginClick = {

                    navController.popBackStack()
                }
            )
        }

        // ==============================================
        // PRÓXIMAS PANTALLAS
        // ==============================================

        // Dashboard principal.
        // Registro de reciclaje.
        // Consulta de puntos.
        // Catálogo de recompensas.
        // Canje de recompensas.
        // Historial de canjes.
        // Perfil del usuario.

        // La navegación hacia el Dashboard se habilitará
        // cuando exista autenticación real con el servidor.
    }
}