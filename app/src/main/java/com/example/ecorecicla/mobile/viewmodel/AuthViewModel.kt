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
    val usuarioId: Long? = null
)

// Arquitectura MVVM y patrón Observer.
class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _uiState = MutableStateFlow(AuthUiState())

    val uiState: StateFlow<AuthUiState> =
        _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                message = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                message = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(passwordVisible = !it.passwordVisible)
        }
    }

    // Inicio de sesión real.
    fun login() {

        val estado = _uiState.value

        if (estado.isLoading) return

        val correo = estado.email.trim()
        val password = estado.password

        val errorCorreo = when {
            correo.isBlank() ->
                "Ingresa tu correo electrónico"

            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
                "Ingresa un correo válido"

            else -> null
        }

        val errorPassword = if (password.isBlank()) {
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

        if (errorCorreo != null || errorPassword != null) {
            return
        }

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

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            usuarioId = usuario.id,
                            password = "",
                            message = "Inicio de sesión exitoso"
                        )
                    }

                } else {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = false,
                            message = "Correo o contraseña incorrectos"
                        )
                    }
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = "No fue posible iniciar sesión"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(message = null)
        }
    }

    fun clearState() {
        _uiState.value = AuthUiState()
    }
}