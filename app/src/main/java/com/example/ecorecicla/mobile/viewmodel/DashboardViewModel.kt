package com.example.ecorecicla.mobile.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ecorecicla.mobile.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: DashboardViewModel.kt
// ARQUITECTURA: MVVM
// PATRONES DE DISEÑO: OBSERVER Y REPOSITORY
// RESPONSABILIDAD: Gestionar el estado del Dashboard.
// ======================================================

// ======================================================
// MODELO DEL ESTADO DE LA INTERFAZ
// ======================================================

data class DashboardUiState(

    val nombreUsuario: String = "",
    val puntos: Int = 0,
    val kilogramos: Double = 0.0,
    val reciclajes: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDemo: Boolean = false
)

// ======================================================
// VIEWMODEL DEL DASHBOARD
// ======================================================

class DashboardViewModel(

    // Inyección de dependencias mediante constructor.
    // El repositorio centraliza el acceso a los datos.

    private val repository: DashboardRepository =
        DashboardRepository()

) : ViewModel() {

    // ==================================================
    // PATRÓN OBSERVER
    // ==================================================

    // Estado privado y modificable.

    private val _uiState = MutableStateFlow(
        DashboardUiState()
    )

    // Estado público de solo lectura.

    val uiState: StateFlow<DashboardUiState> =
        _uiState.asStateFlow()

    // ==================================================
    // CARGAR DATOS DE DEMOSTRACIÓN
    // ==================================================

    fun cargarDatosDemostracion() {

        setLoading(true)

        try {

            // PATRÓN REPOSITORY:
            // Los datos se obtienen desde el repositorio.

            val datos = repository.obtenerDatosDemostracion()

            // Actualización reactiva de la interfaz.

            _uiState.update {

                it.copy(
                    nombreUsuario = datos.nombreUsuario,
                    puntos = datos.puntos,
                    kilogramos = datos.kilogramos,
                    reciclajes = datos.reciclajes,
                    isLoading = false,
                    error = null,
                    isDemo = true
                )
            }

        } catch (e: Exception) {

            mostrarError(
                "No fue posible cargar la información."
            )
        }
    }

    // ==================================================
    // ACTUALIZAR INFORMACIÓN REAL DEL USUARIO
    // ==================================================

    fun actualizarDatosUsuario(
        nombre: String,
        puntos: Int,
        kilogramos: Double,
        reciclajes: Int
    ) {

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
    // RESTABLECER EL ESTADO DEL DASHBOARD
    // ==================================================

    fun clearState() {

        _uiState.value = DashboardUiState()
    }

    // ==================================================
    // FUTURA INTEGRACIÓN CON LA API REST
    // ==================================================

    // Las consultas reales se realizarán mediante
    // DashboardRepository cuando estén disponibles
    // los endpoints del servidor de EcoRecicla.
}