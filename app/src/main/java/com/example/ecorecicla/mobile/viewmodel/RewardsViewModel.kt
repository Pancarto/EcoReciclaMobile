package com.example.ecorecicla.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorecicla.mobile.data.repository.MovimientoLocal
import com.example.ecorecicla.mobile.data.repository.RecompensaLocal
import com.example.ecorecicla.mobile.data.repository.RewardsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado observable.
data class RewardsUiState(
    val saldo: Int = 0,
    val recompensas: List<RecompensaLocal> = emptyList(),
    val movimientos: List<MovimientoLocal> = emptyList(),
    val isLoading: Boolean = true,
    val mensaje: String? = null,
    val error: String? = null
)

// Arquitectura MVVM y patrón Observer.
class RewardsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        RewardsRepository(application)

    private val _uiState =
        MutableStateFlow(RewardsUiState())

    val uiState: StateFlow<RewardsUiState> =
        _uiState.asStateFlow()

    // Consultar catálogo y saldo.
    fun cargarCatalogo(usuarioId: Long) {

        _uiState.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {

            try {

                val catalogo =
                    repository.obtenerCatalogo(usuarioId)

                _uiState.update {
                    it.copy(
                        saldo = catalogo.saldo,
                        recompensas = catalogo.recompensas,
                        isLoading = false
                    )
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudo cargar el catálogo"
                    )
                }
            }
        }
    }

    // Ejecutar canje real.
    fun canjear(
        usuarioId: Long,
        recompensaId: Long
    ) {

        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(
                isLoading = true,
                mensaje = null,
                error = null
            )
        }

        viewModelScope.launch {

            try {

                val mensaje = repository.canjear(
                    usuarioId,
                    recompensaId
                )

                _uiState.update {
                    it.copy(mensaje = mensaje)
                }

                // Actualizar el catálogo después del canje.
                val catalogo =
                    repository.obtenerCatalogo(usuarioId)

                _uiState.update {
                    it.copy(
                        saldo = catalogo.saldo,
                        recompensas = catalogo.recompensas,
                        isLoading = false
                    )
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = if (it.mensaje != null)
                            "Canje realizado. Actualiza el catálogo."
                        else
                            e.message ?: "No se pudo realizar el canje"
                    )
                }
            }
        }
    }

    // Consultar historial real.
    fun cargarHistorial(usuarioId: Long) {

        _uiState.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {

            try {

                val movimientos =
                    repository.obtenerHistorial(usuarioId)

                _uiState.update {
                    it.copy(
                        movimientos = movimientos,
                        isLoading = false
                    )
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudo consultar el historial"
                    )
                }
            }
        }
    }

    fun limpiarMensajes() {

        _uiState.update {
            it.copy(
                mensaje = null,
                error = null
            )
        }
    }
}