package com.example.ecorecicla.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorecicla.mobile.data.repository.CanjeLocal
import com.example.ecorecicla.mobile.data.repository.MovimientoLocal
import com.example.ecorecicla.mobile.data.repository.RecompensaLocal
import com.example.ecorecicla.mobile.data.repository.RewardsRepository
import com.example.ecorecicla.mobile.data.repository.MisPremiosLocal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: RewardsViewModel.kt
// ARQUITECTURA: MVVM
// PATRÓN DE DISEÑO: Observer
// PERSISTENCIA: SQLite
// ======================================================

// ======================================================
// RESULTADO EXITOSO DE UN CANJE
// ======================================================

data class ResultadoCanje(

    val recompensaId: Long,

    val nombre: String,

    val puntosUtilizados: Int,

    val saldoRestante: Int? = null,

    // Nuevos campos del comprobante.

    val canjeId: Long? = null,

    val codigoReclamacion: String? = null,

    val estado: String = "PENDIENTE",

    val fecha: String? = null,

    val puntoEntrega: String =
        "Papelería de la universidad",

    val comprobante: CanjeLocal? = null
)

// ======================================================
// ESTADO OBSERVABLE DE LA INTERFAZ
// ======================================================

data class RewardsUiState(

    // Saldo disponible.

    val saldo: Int = 0,

    // Catálogo de recompensas.

    val recompensas: List<RecompensaLocal> =
        emptyList(),

    // Historial de operaciones.

    val movimientos: List<MovimientoLocal> =
        emptyList(),

    // Estados generales.

    val isLoading: Boolean = true,

    val mensaje: String? = null,

    val error: String? = null,

    // Resultado del último canje.

    val resultadoCanje: ResultadoCanje? = null,

    // Premios almacenados en SQLite.

    val premiosPendientes: List<CanjeLocal> =
        emptyList(),

    val premiosEntregados: List<CanjeLocal> =
        emptyList(),

    val premiosAnteriores: List<CanjeLocal> =
        emptyList(),

    // Cantidad de premios pendientes.

    val cantidadPendientes: Int = 0,

    // Comprobante seleccionado por el usuario.

    val comprobanteSeleccionado: CanjeLocal? = null,

    // Estados independientes para comprobantes.

    val cargandoComprobante: Boolean = false,

    val errorComprobante: String? = null,

    // Control de operaciones de canje.

    val realizandoCanje: Boolean = false,

    // Identificador del usuario consultado.

    val usuarioId: Long? = null

) {

    // Total de premios entregados.

    val cantidadEntregados: Int
        get() = premiosEntregados.size

    // Total de operaciones anteriores.

    val cantidadAnteriores: Int
        get() = premiosAnteriores.size

    // Total de premios consultados.

    val totalCanjes: Int
        get() =
            cantidadPendientes +
                    cantidadEntregados +
                    cantidadAnteriores

    // Listado completo de premios cargados.

    val todosLosPremios: List<CanjeLocal>
        get() = (
                premiosPendientes +
                        premiosEntregados +
                        premiosAnteriores
                ).sortedWith(
                compareByDescending<CanjeLocal> {
                    it.fecha
                }.thenByDescending {
                    it.id
                }
            )

    // Total de puntos ganados mediante reciclajes.

    val puntosGanados: Long
        get() = movimientos
            .filter {
                it.tipo.equals(
                    "Reciclaje",
                    ignoreCase = true
                )
            }
            .sumOf {
                it.puntos.toLong()
            }

    // Total de puntos utilizados en recompensas.

    val puntosCanjeados: Long
        get() = movimientos
            .filter {
                it.tipo.equals(
                    "Canje",
                    ignoreCase = true
                )
            }
            .sumOf {
                kotlin.math.abs(it.puntos.toLong())
            }
}

// ======================================================
// VIEWMODEL DE RECOMPENSAS
// ======================================================

class RewardsViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Patrón Repository.

    private val repository =
        RewardsRepository(application)

    // Patrón Observer mediante StateFlow.

    private val _uiState =
        MutableStateFlow(RewardsUiState())

    val uiState: StateFlow<RewardsUiState> =
        _uiState.asStateFlow()

    // Control de consultas simultáneas.

    private var trabajoConsulta: Job? = null

    private var trabajoComprobante: Job? = null

    private var trabajoCanje: Job? = null

    // ==================================================
    // PREPARAR CONSULTA DEL USUARIO
    // ==================================================

    private fun prepararConsulta(
        usuarioId: Long
    ): Boolean {

        // No interrumpir una operación de canje.

        if (
            trabajoCanje?.isActive == true ||
            _uiState.value.realizandoCanje
        ) {
            return false
        }

        trabajoConsulta?.cancel()

        // Evitar mostrar información de otra cuenta.

        if (_uiState.value.usuarioId != usuarioId) {

            trabajoComprobante?.cancel()

            _uiState.value = RewardsUiState(
                usuarioId = usuarioId,
                isLoading = true
            )

        } else {

            _uiState.update {

                it.copy(
                    isLoading = true,
                    error = null,
                    mensaje = null
                )
            }
        }

        return true
    }

    // ==================================================
    // CARGAR CATÁLOGO DE RECOMPENSAS
    // ==================================================

    fun cargarCatalogo(
        usuarioId: Long
    ) {

        if (!prepararConsulta(usuarioId)) return

        trabajoConsulta = viewModelScope.launch {

            try {

                // Consultar saldo y catálogo reales.

                val catalogo =
                    repository.obtenerCatalogo(usuarioId)

                // Consultar premios pendientes.

                val pendientes =
                    repository.contarCanjesPendientes(
                        usuarioId
                    )

                _uiState.update {

                    it.copy(
                        saldo = catalogo.saldo,

                        recompensas =
                            catalogo.recompensas,

                        cantidadPendientes =
                            pendientes,

                        isLoading = false,

                        error = null
                    )
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {

                    it.copy(
                        isLoading = false,

                        error =
                            "No se pudo cargar el catálogo"
                    )
                }
            }
        }
    }

    // ==================================================
    // REALIZAR CANJE CON CÓDIGO DE RECLAMACIÓN
    // ==================================================

    fun canjear(
        usuarioId: Long,
        recompensaId: Long
    ) {

        val estado = _uiState.value

        // Evitar operaciones simultáneas.

        if (
            trabajoCanje?.isActive == true ||
            estado.realizandoCanje ||
            estado.isLoading ||
            estado.resultadoCanje != null ||
            estado.error != null ||
            estado.usuarioId != usuarioId
        ) {
            return
        }

        // Obtener recompensa seleccionada.

        val recompensa = estado.recompensas.find {

            it.id == recompensaId
        }

        // Validar existencia.

        if (recompensa == null) {

            _uiState.update {

                it.copy(
                    error =
                        "La recompensa no está disponible"
                )
            }

            return
        }

        // Validar inventario.

        if (recompensa.stock <= 0) {

            _uiState.update {

                it.copy(
                    error =
                        "Esta recompensa está agotada"
                )
            }

            return
        }

        // Validar saldo.

        if (estado.saldo < recompensa.costoPuntos) {

            _uiState.update {

                it.copy(
                    error =
                        "No tienes suficientes puntos"
                )
            }

            return
        }

        // Cancelar consultas anteriores.

        trabajoConsulta?.cancel()

        // Activar estado de procesamiento.

        _uiState.update {

            it.copy(
                isLoading = true,

                realizandoCanje = true,

                mensaje = null,

                error = null,

                resultadoCanje = null
            )
        }

        trabajoCanje = viewModelScope.launch {

            try {

                // ======================================
                // 1. EJECUTAR TRANSACCIÓN REAL
                // ======================================

                val operacion =
                    repository.canjearConComprobante(

                        usuarioId = usuarioId,

                        recompensaId = recompensaId
                    )

                // ======================================
                // 2. RECUPERAR COMPROBANTE
                // ======================================

                val canje = operacion.canje

                // ======================================
                // 3. CONSTRUIR RESULTADO EXITOSO
                // ======================================

                val resultado = ResultadoCanje(

                    recompensaId =
                        canje.recompensaId,

                    nombre =
                        canje.nombre,

                    puntosUtilizados =
                        canje.puntosUtilizados,

                    saldoRestante =
                        operacion.saldoRestante,

                    canjeId =
                        canje.id,

                    codigoReclamacion =
                        canje.codigoReclamacion,

                    estado =
                        canje.estado,

                    fecha =
                        canje.fecha,

                    puntoEntrega =
                        canje.puntoEntrega,

                    comprobante =
                        canje
                )

                // ======================================
                // 4. CONSERVAR RESULTADO DEL CANJE
                // ======================================

                // El canje ya fue guardado en SQLite.
                // Si falla una consulta posterior,
                // no se debe repetir el descuento.

                _uiState.update {

                    it.copy(

                        saldo =
                            operacion.saldoRestante,

                        resultadoCanje =
                            resultado,

                        mensaje =
                            "¡Canje realizado exitosamente!",

                        cantidadPendientes =
                            it.cantidadPendientes + 1,

                        recompensas =
                            it.recompensas.map { premio ->

                                if (
                                    premio.id ==
                                    recompensaId
                                ) {

                                    premio.copy(
                                        stock =
                                            (premio.stock - 1)
                                                .coerceAtLeast(0)
                                    )

                                } else {

                                    premio
                                }
                            },

                        premiosPendientes =
                            listOf(canje) +
                                    it.premiosPendientes.filter {
                                            premio ->
                                        premio.id != canje.id
                                    }
                    )
                }

                // ======================================
                // 5. ACTUALIZAR INFORMACIÓN REAL
                // ======================================

                try {

                    val catalogo =
                        repository.obtenerCatalogo(
                            usuarioId
                        )

                    val misPremios =
                        repository.obtenerMisPremios(
                            usuarioId
                        )

                    val movimientos =
                        repository.obtenerHistorial(
                            usuarioId
                        )

                    _uiState.update {

                        it.copy(

                            saldo =
                                catalogo.saldo,

                            recompensas =
                                catalogo.recompensas,

                            movimientos =
                                movimientos,

                            premiosPendientes =
                                misPremios.pendientes,

                            premiosEntregados =
                                misPremios.entregados,

                            premiosAnteriores =
                                misPremios.anteriores,

                            cantidadPendientes =
                                misPremios.totalPendientes,

                            error = null
                        )
                    }

                } catch (e: CancellationException) {

                    throw e

                } catch (e: Exception) {

                    // El canje fue exitoso.
                    // Solo falló la actualización visual.

                    _uiState.update {

                        it.copy(
                            error =
                                "Tu premio fue reservado. " +
                                        "Actualiza el catálogo " +
                                        "para consultar tus datos."
                        )
                    }
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                // Error durante la operación principal.

                _uiState.update {

                    it.copy(

                        error =
                            if (it.resultadoCanje != null) {

                                "Canje registrado. " +
                                        "Actualiza tus recompensas."

                            } else {

                                when (e.message) {

                                    "La recompensa está agotada" ->
                                        "Esta recompensa está agotada"

                                    "No tienes suficientes puntos" ->
                                        "No tienes suficientes puntos"

                                    "La recompensa ya no está disponible" ->
                                        "La recompensa ya no está disponible"

                                    else ->
                                        "No se pudo confirmar el canje. " +
                                                "Consulta Mis premios antes " +
                                                "de intentarlo nuevamente."
                                }
                            }
                    )
                }

            } finally {

                // Finalizar estado de procesamiento.

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        realizandoCanje = false
                    )
                }
            }
        }
    }

    // ==================================================
    // CARGAR HISTORIAL DE OPERACIONES
    // ==================================================

    fun cargarHistorial(
        usuarioId: Long
    ) {

        if (!prepararConsulta(usuarioId)) return

        trabajoConsulta = viewModelScope.launch {

            try {

                val movimientos =
                    repository.obtenerHistorial(
                        usuarioId
                    )

                val pendientes =
                    repository.contarCanjesPendientes(
                        usuarioId
                    )

                _uiState.update {

                    it.copy(

                        movimientos =
                            movimientos,

                        cantidadPendientes =
                            pendientes,

                        isLoading = false,

                        error = null
                    )
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {

                    it.copy(

                        isLoading = false,

                        error =
                            "No se pudo consultar el historial"
                    )
                }
            }
        }
    }

    // ==================================================
    // CARGAR TODOS LOS PREMIOS DEL USUARIO
    // ==================================================

    fun cargarMisPremios(
        usuarioId: Long
    ) {

        if (!prepararConsulta(usuarioId)) return

        trabajoConsulta = viewModelScope.launch {

            try {

                val misPremios =
                    repository.obtenerMisPremios(
                        usuarioId
                    )

                actualizarMisPremios(misPremios)

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        error = null
                    )
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {

                    it.copy(

                        isLoading = false,

                        error =
                            "No se pudieron consultar tus premios"
                    )
                }
            }
        }
    }

    // ==================================================
    // ACTUALIZAR ESTADO DE MIS PREMIOS
    // ==================================================

    private fun actualizarMisPremios(
        misPremios: MisPremiosLocal
    ) {

        _uiState.update {

            it.copy(

                premiosPendientes =
                    misPremios.pendientes,

                premiosEntregados =
                    misPremios.entregados,

                premiosAnteriores =
                    misPremios.anteriores,

                cantidadPendientes =
                    misPremios.totalPendientes
            )
        }
    }

    // ==================================================
    // CONSULTAR COMPROBANTE POR IDENTIFICADOR
    // ==================================================

    fun cargarComprobante(
        usuarioId: Long,
        canjeId: Long
    ) {

        if (
            _uiState.value.usuarioId != usuarioId ||
            _uiState.value.realizandoCanje
        ) return

        trabajoComprobante?.cancel()

        _uiState.update {

            it.copy(

                cargandoComprobante = true,

                comprobanteSeleccionado = null,

                errorComprobante = null
            )
        }

        trabajoComprobante = viewModelScope.launch {

            try {

                val comprobante =
                    repository.obtenerCanje(

                        usuarioId = usuarioId,

                        canjeId = canjeId
                    )

                if (comprobante == null) {

                    _uiState.update {

                        it.copy(

                            cargandoComprobante = false,

                            errorComprobante =
                                "No se encontró el comprobante"
                        )
                    }

                } else {

                    _uiState.update {

                        it.copy(

                            comprobanteSeleccionado =
                                comprobante,

                            cargandoComprobante = false,

                            errorComprobante = null
                        )
                    }
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {

                    it.copy(

                        cargandoComprobante = false,

                        errorComprobante =
                            "No se pudo consultar el comprobante"
                    )
                }
            }
        }
    }

    // ==================================================
    // CONSULTAR COMPROBANTE MEDIANTE CÓDIGO ECO#
    // ==================================================

    fun buscarComprobantePorCodigo(
        usuarioId: Long,
        codigo: String
    ) {

        if (
            _uiState.value.usuarioId != usuarioId ||
            _uiState.value.realizandoCanje
        ) return

        trabajoComprobante?.cancel()

        _uiState.update {

            it.copy(

                cargandoComprobante = true,

                comprobanteSeleccionado = null,

                errorComprobante = null
            )
        }

        trabajoComprobante = viewModelScope.launch {

            try {

                val comprobante =
                    repository.obtenerCanjePorCodigo(

                        usuarioId = usuarioId,

                        codigo = codigo
                    )

                _uiState.update {

                    it.copy(

                        comprobanteSeleccionado =
                            comprobante,

                        cargandoComprobante = false,

                        errorComprobante =
                            if (comprobante == null) {

                                "No encontramos un premio " +
                                        "asociado a ese código"

                            } else {

                                null
                            }
                    )
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {

                    it.copy(

                        cargandoComprobante = false,

                        errorComprobante =
                            "No se pudo consultar el código"
                    )
                }
            }
        }
    }

    // ==================================================
    // MOSTRAR COMPROBANTE YA CONSULTADO
    // ==================================================

    fun seleccionarComprobante(
        comprobante: CanjeLocal
    ) {

        // Verificar que pertenece a la cuenta actual.

        if (
            comprobante.usuarioId !=
            _uiState.value.usuarioId
        ) return

        trabajoComprobante?.cancel()

        _uiState.update {

            it.copy(

                comprobanteSeleccionado =
                    comprobante,

                cargandoComprobante = false,

                errorComprobante = null
            )
        }
    }

    // ==================================================
    // CERRAR COMPROBANTE
    // ==================================================

    fun cerrarComprobante() {

        trabajoComprobante?.cancel()

        _uiState.update {

            it.copy(

                comprobanteSeleccionado = null,

                cargandoComprobante = false,

                errorComprobante = null
            )
        }
    }

    // ==================================================
    // CERRAR CONFIRMACIÓN DE CANJE EXITOSO
    // ==================================================

    fun cerrarResultadoCanje() {

        if (_uiState.value.realizandoCanje) return

        _uiState.update {

            it.copy(

                resultadoCanje = null,

                mensaje = null
            )
        }
    }

    // ==================================================
    // LIMPIAR ERROR DEL COMPROBANTE
    // ==================================================

    fun limpiarErrorComprobante() {

        _uiState.update {

            it.copy(
                errorComprobante = null
            )
        }
    }

    // ==================================================
    // LIMPIAR MENSAJES DE LA INTERFAZ
    // ==================================================

    fun limpiarMensajes() {

        if (_uiState.value.realizandoCanje) return

        _uiState.update {

            it.copy(

                mensaje = null,

                error = null,

                resultadoCanje = null
            )
        }
    }
}

// ======================================================
// FIN DEL ARCHIVO
// ECORECICLA MOBILE
// ======================================================