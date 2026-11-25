package com.example.ecorecicla.mobile.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: RegisterViewModel.kt
// ARQUITECTURA: MVVM
// PATRÓN DE COMPORTAMIENTO: OBSERVER
// RESPONSABILIDAD: Estado y validación del registro.
// ======================================================

// ======================================================
// ESTADO DE LA INTERFAZ DE REGISTRO
// ======================================================

data class RegisterUiState(

    // Datos personales del usuario.
    val nombre: String = "",
    val correo: String = "",

    // Credenciales de acceso.
    val password: String = "",
    val confirmarPassword: String = "",

    // Visibilidad de las contraseñas.
    val passwordVisible: Boolean = false,
    val confirmarVisible: Boolean = false,

    // Mensajes de error de cada campo.
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorPassword: String? = null,
    val errorConfirmacion: String? = null,

    // Estados de la operación.
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,

    // Mensaje informativo.
    val mensaje: String? = null
)

// ======================================================
// VIEWMODEL DE REGISTRO
// ======================================================

class RegisterViewModel : ViewModel() {

    // Estado privado y modificable.
    private val _uiState = MutableStateFlow(RegisterUiState())

    // Estado público de solo lectura.
    // La interfaz observa sus cambios mediante StateFlow.
    val uiState: StateFlow<RegisterUiState> =
        _uiState.asStateFlow()

    // ==================================================
    // ACTUALIZAR NOMBRE
    // ==================================================

    fun onNombreChange(nombre: String) {

        _uiState.update {

            it.copy(
                nombre = nombre,
                errorNombre = null,
                mensaje = null
            )
        }
    }

    // ==================================================
    // ACTUALIZAR CORREO ELECTRÓNICO
    // ==================================================

    fun onCorreoChange(correo: String) {

        _uiState.update {

            it.copy(
                correo = correo,
                errorCorreo = null,
                mensaje = null
            )
        }
    }

    // ==================================================
    // ACTUALIZAR CONTRASEÑA
    // ==================================================

    fun onPasswordChange(password: String) {

        _uiState.update {

            it.copy(
                password = password,
                errorPassword = null,
                errorConfirmacion = null,
                mensaje = null
            )
        }
    }

    // ==================================================
    // ACTUALIZAR CONFIRMACIÓN DE CONTRASEÑA
    // ==================================================

    fun onConfirmarPasswordChange(confirmacion: String) {

        _uiState.update {

            it.copy(
                confirmarPassword = confirmacion,
                errorConfirmacion = null,
                mensaje = null
            )
        }
    }

    // ==================================================
    // MOSTRAR U OCULTAR CONTRASEÑA
    // ==================================================

    fun togglePasswordVisibility() {

        _uiState.update {

            it.copy(
                passwordVisible = !it.passwordVisible
            )
        }
    }

    // ==================================================
    // MOSTRAR U OCULTAR CONFIRMACIÓN
    // ==================================================

    fun toggleConfirmarVisibility() {

        _uiState.update {

            it.copy(
                confirmarVisible = !it.confirmarVisible
            )
        }
    }

    // ==================================================
    // VALIDAR FORMULARIO DE REGISTRO
    // ==================================================

    fun register() {

        // Obtener el estado actual del formulario.
        val estado = _uiState.value

        val nombre = estado.nombre.trim()
        val correo = estado.correo.trim()
        val password = estado.password
        val confirmacion = estado.confirmarPassword

        // ==============================================
        // VALIDACIÓN DEL NOMBRE
        // ==============================================

        val errorNombre = when {

            nombre.isBlank() ->
                "Ingresa tu nombre completo"

            else -> null
        }

        // ==============================================
        // VALIDACIÓN DEL CORREO
        // ==============================================

        val errorCorreo = when {

            correo.isBlank() ->
                "Ingresa tu correo electrónico"

            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
                "Ingresa un correo válido"

            else -> null
        }

        // ==============================================
        // VALIDACIÓN DE CONTRASEÑA
        // ==============================================

        val errorPassword = when {

            password.isBlank() ->
                "Ingresa una contraseña"

            password.length < 8 ->
                "La contraseña debe tener mínimo 8 caracteres"

            else -> null
        }

        // ==============================================
        // VALIDACIÓN DE CONFIRMACIÓN
        // ==============================================

        val errorConfirmacion = when {

            confirmacion.isBlank() ->
                "Confirma tu contraseña"

            password != confirmacion ->
                "Las contraseñas no coinciden"

            else -> null
        }

        // ==============================================
        // COMPROBAR FORMULARIO COMPLETO
        // ==============================================

        val formularioValido =
            errorNombre == null &&
                    errorCorreo == null &&
                    errorPassword == null &&
                    errorConfirmacion == null

        // Actualizar el estado de la interfaz.
        _uiState.update {

            it.copy(

                errorNombre = errorNombre,
                errorCorreo = errorCorreo,
                errorPassword = errorPassword,
                errorConfirmacion = errorConfirmacion,

                isLoading = false,

                // No existe todavía un registro real.
                isRegistered = false,

                mensaje = if (formularioValido) {

                    "Formulario válido. Conexión al servidor pendiente."

                } else {

                    null
                }
            )
        }

        // ==============================================
        // FUTURA INTEGRACIÓN CON EL SERVIDOR
        // ==============================================

        // Cuando la API de EcoRecicla esté disponible,
        // se utilizará AuthRepository para enviar los
        // datos al servidor y registrar al usuario.

        // isRegistered solo cambiará a true cuando
        // el servidor confirme el registro exitoso.
    }

    // ==================================================
    // LIMPIAR MENSAJE INFORMATIVO
    // ==================================================

    fun clearMessage() {

        _uiState.update {

            it.copy(mensaje = null)
        }
    }

    // ==================================================
    // RESTABLECER FORMULARIO
    // ==================================================

    fun clearState() {

        _uiState.value = RegisterUiState()
    }
}