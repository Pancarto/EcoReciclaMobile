package com.example.ecorecicla.mobile.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.ecorecicla.mobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: AuthViewModel.kt
// ARQUITECTURA: MVVM
// PATRÓN DE COMPORTAMIENTO: OBSERVER
// RESPONSABILIDAD: Gestión del estado de autenticación.
// ======================================================

// Representa el estado de la pantalla de inicio de sesión.
data class AuthUiState(

    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val message: String? = null

)

// ViewModel encargado de la lógica de autenticación.
class AuthViewModel(

    // Inyección de dependencia mediante el constructor.
    private val repository: AuthRepository = AuthRepository()

) : ViewModel() {

    // ==================================================
    // PATRÓN OBSERVER - STATEFLOW
    // ==================================================

    // Estado privado que únicamente modifica el ViewModel.
    private val _uiState = MutableStateFlow(AuthUiState())

    // Estado público de solo lectura para la interfaz.
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ==================================================
    // ACTUALIZACIÓN DEL CORREO ELECTRÓNICO
    // ==================================================

    fun onEmailChange(email: String) {

        _uiState.update {

            it.copy(
                email = email,
                emailError = null,
                message = null
            )

        }

    }

    // ==================================================
    // ACTUALIZACIÓN DE LA CONTRASEÑA
    // ==================================================

    fun onPasswordChange(password: String) {

        _uiState.update {

            it.copy(
                password = password,
                passwordError = null,
                message = null
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
    // VALIDACIÓN DEL FORMULARIO
    // ==================================================

    fun login() {

        val currentState = _uiState.value

        val email = currentState.email.trim()

        val password = currentState.password

        // Validación del formato del correo electrónico.
        val emailError = when {

            email.isBlank() ->
                "Ingresa tu correo electrónico"

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Ingresa un correo válido"

            else -> null

        }

        // Validación de la contraseña.
        val passwordError = when {

            password.isBlank() ->
                "Ingresa tu contraseña"

            else -> null

        }

        // Determina si el formulario es válido.
        val isValid =
            emailError == null && passwordError == null

        // Actualización del estado observable.
        _uiState.update {

            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                isAuthenticated = false,
                message = if (isValid) {
                    "Formulario válido. Conexión al servidor pendiente."
                } else {
                    null
                }
            )

        }

        // La autenticación real mediante el Repository
        // se incorporará cuando estén disponibles
        // los endpoints de la API de EcoRecicla.

    }

    // ==================================================
    // LIMPIEZA DE MENSAJES
    // ==================================================

    fun clearMessage() {

        _uiState.update {

            it.copy(message = null)

        }

    }

    // ==================================================
    // LIMPIEZA DEL ESTADO DE AUTENTICACIÓN
    // ==================================================

    fun clearState() {

        _uiState.value = AuthUiState()

    }

}