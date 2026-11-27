package com.example.ecorecicla.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorecicla.mobile.data.repository.MaterialReciclable
import com.example.ecorecicla.mobile.data.repository.RecyclingRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.floor

// Información del reciclaje guardado correctamente.
data class ResultadoReciclaje(
    val material: String,
    val peso: Double,
    val puntos: Int
)

// Estado observable del formulario.
data class RecyclingUiState(
    val materiales: List<MaterialReciclable> = emptyList(),
    val material: MaterialReciclable? = null,
    val peso: String = "",
    val puntosEstimados: Int = 0,
    val errorMaterial: String? = null,
    val errorPeso: String? = null,
    val isLoading: Boolean = false,
    val mensaje: String? = null,
    val registrado: Boolean = false,

    // Resultado confirmado por el repositorio.
    val resultado: ResultadoReciclaje? = null
)

// Arquitectura MVVM y patrón Observer.
class RecyclingViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        RecyclingRepository(application)

    private val _uiState =
        MutableStateFlow(RecyclingUiState())

    val uiState: StateFlow<RecyclingUiState> =
        _uiState.asStateFlow()

    init {
        cargarMateriales()
    }

    // Consultar el catálogo desde SQLite.
    fun cargarMateriales() {

        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {

            try {

                val materiales =
                    repository.obtenerMateriales()

                _uiState.update {
                    it.copy(
                        materiales = materiales,
                        isLoading = false,
                        mensaje = null
                    )
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensaje = "No se pudieron cargar los materiales"
                    )
                }
            }
        }
    }

    // Seleccionar material.
    fun seleccionarMaterial(
        material: MaterialReciclable
    ) {

        if (_uiState.value.isLoading ||
            _uiState.value.registrado
        ) return

        _uiState.update {
            it.copy(
                material = material,
                errorMaterial = null,
                mensaje = null
            )
        }

        calcularPuntos()
    }

    // Actualizar peso.
    fun onPesoChange(peso: String) {

        if (_uiState.value.isLoading ||
            _uiState.value.registrado
        ) return

        _uiState.update {
            it.copy(
                peso = peso,
                errorPeso = null,
                mensaje = null
            )
        }

        calcularPuntos()
    }

    // Calcular puntos automáticamente.
    private fun calcularPuntos() {

        val estado = _uiState.value

        val peso = estado.peso
            .replace(",", ".")
            .toDoubleOrNull()

        val tarifa =
            estado.material?.puntosPorKg ?: 0

        val puntos = if (
            peso != null &&
            peso.isFinite() &&
            peso > 0 &&
            peso <= 1000
        ) {

            floor(peso * tarifa).toInt()

        } else {

            0
        }

        _uiState.update {
            it.copy(puntosEstimados = puntos)
        }
    }

    // Registrar reciclaje real en SQLite.
    fun registrar(usuarioId: Long) {

        val estado = _uiState.value

        // Evitar registros duplicados.
        if (estado.isLoading || estado.registrado) return

        val material = estado.material

        val peso = estado.peso
            .replace(",", ".")
            .toDoubleOrNull()

        val errorMaterial = if (material == null) {
            "Selecciona un material"
        } else null

        val errorPeso = if (
            peso == null ||
            !peso.isFinite() ||
            peso <= 0 ||
            peso > 1000
        ) {
            "Ingresa un peso mayor a 0 y máximo 1000 kg"
        } else null

        _uiState.update {
            it.copy(
                errorMaterial = errorMaterial,
                errorPeso = errorPeso,
                mensaje = null,
                resultado = null
            )
        }

        // Validar los datos antes de guardar.
        if (
            errorMaterial != null ||
            errorPeso != null ||
            material == null ||
            peso == null
        ) return

        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {

            try {

                // Guardar el reciclaje y obtener
                // los puntos realmente otorgados.
                val puntos =
                    repository.registrarReciclaje(
                        usuarioId = usuarioId,
                        materialId = material.id,
                        peso = peso
                    )

                // Crear el resultado exitoso.
                val resultado = ResultadoReciclaje(
                    material = material.nombre,
                    peso = peso,
                    puntos = puntos
                )

                // Observer notifica automáticamente
                // a la interfaz de usuario.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        registrado = true,
                        puntosEstimados = puntos,
                        resultado = resultado,
                        mensaje = "¡Reciclaje registrado! Ganaste $puntos puntos."
                    )
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                // No mostrar éxito si falla SQLite.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        registrado = false,
                        resultado = null,
                        mensaje = "No se pudo registrar el reciclaje"
                    )
                }
            }
        }
    }

    // Preparar un nuevo registro sin perder
    // el catálogo de materiales.
    fun nuevoReciclaje() {

        if (_uiState.value.isLoading) return

        _uiState.update {
            RecyclingUiState(
                materiales = it.materiales
            )
        }
    }
}