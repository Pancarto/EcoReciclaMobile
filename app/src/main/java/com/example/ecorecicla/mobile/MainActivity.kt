package com.example.ecorecicla.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ecorecicla.mobile.navigation.AppNavigation
import com.example.ecorecicla.mobile.ui.theme.EcoReciclaMobileTheme

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: MainActivity.kt
// RESPONSABILIDAD: Punto de entrada de la aplicación.
// ARQUITECTURA: Single Activity.
// ======================================================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Aprovechamiento del espacio de pantalla.
        enableEdgeToEdge()

        // Inicialización de Jetpack Compose.
        setContent {

            // Aplicación de la identidad visual.
            EcoReciclaMobileTheme {

                // Inicialización del sistema de navegación.
                AppNavigation()

            }
        }
    }
}