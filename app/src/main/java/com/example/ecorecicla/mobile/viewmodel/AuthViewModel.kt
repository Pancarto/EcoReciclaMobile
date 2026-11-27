package com.example.ecorecicla.mobile.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorecicla.mobile.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado observable del inicio de sesión.
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val message: String? = null,
    val usuarioId: Long? = null,

    // Preferencia de persistencia de sesión.
    val recordarSesion: Boolean = false
)

// Arquitectura MVVM y patrón Observer.
class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        AuthRepository(application)

    private val _uiState =
        MutableStateFlow(AuthUiState())

    val uiState: StateFlow<AuthUiState> =
        _uiState.asStateFlow()

    // Actualizar correo electrónico.
    fun onEmailChange(email: String) {

        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                message = null
            )
        }
    }

    // Actualizar contraseña.
    fun onPasswordChange(password: String) {

        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                message = null
            )
        }
    }

    // Mostrar u ocultar contraseña.
    fun togglePasswordVisibility() {

        _uiState.update {
            it.copy(
                passwordVisible = !it.passwordVisible
            )
        }
    }

    // Actualizar la casilla de recordar sesión.
    fun onRecordarSesionChange(recordar: Boolean) {

        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(recordarSesion = recordar)
        }
    }

    // Inicio de sesión real mediante SQLite.
    fun login() {

        val estado = _uiState.value

        if (estado.isLoading) return

        val correo = estado.email.trim()
        val password = estado.password

        val errorCorreo = when {

            correo.isBlank() ->
                "Ingresa tu correo electrónico"

            !Patterns.EMAIL_ADDRESS
                .matcher(correo).matches() ->
                "Ingresa un correo válido"

            else -> null
        }

        val errorPassword =
            if (password.isBlank()) {
                "Ingresa tu contraseña"
            } else {
                null
            }

        _uiState.update {
            it.copy(
                emailError = errorCorreo,
                passwordError = errorPassword,
                message = null,
                isAuthenticated = false,
                usuarioId = null
            )
        }

        // Detener autenticación si existen errores.
        if (
            errorCorreo != null ||
            errorPassword != null
        ) return

        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {

            try {

                val usuario = repository.login(
                    correo,
                    password
                )

                if (usuario != null) {

                    // Guardar o eliminar la sesión
                    // según la preferencia del usuario.
                    val preferenciaGuardada =
                        if (estado.recordarSesion) {

                            repository.guardarSesion(
                                usuario.id
                            )

                        } else {

                            repository.eliminarSesion()
                        }

                    // No confirmar una preferencia
                    // que no pudo almacenarse.
                    if (!preferenciaGuardada) {

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                message =
                                    "No se pudo guardar la preferencia de sesión. Intenta nuevamente."
                            )
                        }

                        return@launch
                    }

                    // Autenticación completada.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            usuarioId = usuario.id,
                            password = "",
                            passwordVisible = false,
                            message =
                                "Inicio de sesión exitoso"
                        )
                    }

                } else {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = false,
                            message =
                                "Correo o contraseña incorrectos"
                        )
                    }
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message =
                            "No fue posible iniciar sesión"
                    )
                }
            }
        }
    }

    // Limpiar mensajes.
    fun clearMessage() {

        _uiState.update {
            it.copy(message = null)
        }
    }

    // Restablecer el formulario.
    fun clearState() {

        _uiState.value = AuthUiState()
    }
}