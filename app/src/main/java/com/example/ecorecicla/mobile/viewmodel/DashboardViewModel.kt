package com.example.ecorecicla.mobile.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: DashboardViewModel.kt
// ARQUITECTURA: MVVM
// PATRÓN DE COMPORTAMIENTO: OBSERVER
// RESPONSABILIDAD: Gestión del estado del Dashboard.
// ======================================================

// ======================================================
// ESTADO DE LA PANTALLA PRINCIPAL
// ======================================================

data class DashboardUiState(

    // Información del usuario.
    val nombreUsuario: String = "",

    // Saldo de puntos disponibles.
    val puntos: Int = 0,

    // Cantidad de material reciclado.
    val kilogramos: Double = 0.0,

    // Número de reciclajes realizados.
    val reciclajes: Int = 0,

    // Estado de carga de información.
    val isLoading: Boolean = false,

    // Mensaje de error.
    val error: String? = null,

    // Identifica si los datos son de demostración.
    val isDemo: Boolean = false
)

// ======================================================
// VIEWMODEL DEL DASHBOARD
// ======================================================

class DashboardViewModel : ViewModel() {

    // Estado privado y modificable.
    private val _uiState = MutableStateFlow(
        DashboardUiState()
    )

    // Estado público de solo lectura.
    // La interfaz observará los cambios automáticamente.
    val uiState: StateFlow<DashboardUiState> =
        _uiState.asStateFlow()

    // ==================================================
    // CARGAR DATOS DE DEMOSTRACIÓN
    // ==================================================

    fun cargarDatosDemostracion() {

        // Información ficticia utilizada exclusivamente
        // durante las pruebas de desarrollo.

        _uiState.update {

            it.copy(
                nombreUsuario = "Usuario de prueba",
                puntos = 1250,
                kilogramos = 18.5,
                reciclajes = 12,
                isLoading = false,
                error = null,
                isDemo = true
            )
        }
    }

    // ==================================================
    // ACTUALIZAR INFORMACIÓN DEL USUARIO
    // ==================================================

    fun actualizarDatosUsuario(
        nombre: String,
        puntos: Int,
        kilogramos: Double,
        reciclajes: Int
    ) {

        // Método preparado para recibir información
        // previamente obtenida desde el servidor.

        _uiState.update {

            it.copy(
                nombreUsuario = nombre,
                puntos = puntos,
                kilogramos = kilogramos,
                reciclajes = reciclajes,
                isLoading = false,
                error = null,
                isDemo = false
            )
        }
    }

    // ==================================================
    // CONTROLAR ESTADO DE CARGA
    // ==================================================

    fun setLoading(isLoading: Boolean) {

        _uiState.update {

            it.copy(
                isLoading = isLoading
            )
        }
    }

    // ==================================================
    // GESTIONAR ERRORES
    // ==================================================

    fun mostrarError(mensaje: String) {

        _uiState.update {

            it.copy(
                error = mensaje,
                isLoading = false
            )
        }
    }

    // ==================================================
    // LIMPIAR MENSAJES DE ERROR
    // ==================================================

    fun clearError() {

        _uiState.update {

            it.copy(
                error = null
            )
        }
    }

    // ==================================================
    // RESTABLECER ESTADO DEL DASHBOARD
    // ==================================================

    fun clearState() {

        _uiState.value = DashboardUiState()
    }

    // ==================================================
    // FUTURA INTEGRACIÓN CON LA API
    // ==================================================

    // El DashboardRepository será responsable de consultar:
    //
    // 1. Información del usuario autenticado.
    // 2. Saldo real de puntos.
    // 3. Cantidad de material reciclado.
    // 4. Número de reciclajes realizados.
    //
    // El ViewModel recibirá los resultados y actualizará
    // el estado de la interfaz mediante StateFlow.
}