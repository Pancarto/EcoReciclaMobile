package com.example.ecorecicla.mobile.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.ecorecicla.mobile.data.repository.DashboardRepository
import com.example.ecorecicla.mobile.viewmodel.DashboardViewModel
import com.example.ecorecicla.mobile.ui.theme.EcoReciclaMobileTheme
import kotlinx.coroutines.delay

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: DashboardTestPreview.kt
// RESPONSABILIDAD: Comprobar la actualización reactiva.
// PATRONES: REPOSITORY, OBSERVER Y MVVM.
// ======================================================

@Preview(
    name = "Prueba dinámica del patrón Observer",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DashboardTestPreview() {

    // Crear el repositorio.

    val repository = remember {
        DashboardRepository()
    }

    // Crear el ViewModel con inyección de dependencias.

    val dashboardViewModel = remember {
        DashboardViewModel(repository)
    }

    // ==================================================
    // SIMULACIÓN DE ACTUALIZACIÓN DE DATOS
    // ==================================================

    LaunchedEffect(Unit) {

        // Cargar los datos iniciales desde el repositorio.

        dashboardViewModel.cargarDatosDemostracion()

        // Esperar cinco segundos.

        delay(5000)

        // Simular la recepción de nuevos datos.

        dashboardViewModel.actualizarDatosUsuario(
            nombre = "Usuario de prueba",
            puntos = 1500,
            kilogramos = 21.0,
            reciclajes = 13
        )
    }

    // ==================================================
    // PATRÓN OBSERVER
    // ==================================================

    // DashboardRoute observa StateFlow y actualiza
    // automáticamente la interfaz cuando cambia el estado.

    EcoReciclaMobileTheme {

        DashboardRoute(
            dashboardViewModel = dashboardViewModel
        )
    }
}