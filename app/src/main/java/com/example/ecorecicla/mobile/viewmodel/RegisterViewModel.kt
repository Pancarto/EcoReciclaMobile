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

// Estado observable del formulario.
data class RegisterUiState(
    val nombre: String = "",
    val correo: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmarVisible: Boolean = false,
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorPassword: String? = null,
    val errorConfirmacion: String? = null,
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val mensaje: String? = null
)

// ViewModel responsable del registro real.
class RegisterViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _uiState = MutableStateFlow(RegisterUiState())

    val uiState: StateFlow<RegisterUiState> =
        _uiState.asStateFlow()

    fun onNombreChange(nombre: String) {
        _uiState.update {
            it.copy(
                nombre = nombre,
                errorNombre = null,
                mensaje = null
            )
        }
    }

    fun onCorreoChange(correo: String) {
        _uiState.update {
            it.copy(
                correo = correo,
                errorCorreo = null,
                mensaje = null
            )
        }
    }

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

    fun onConfirmarPasswordChange(confirmacion: String) {
        _uiState.update {
            it.copy(
                confirmarPassword = confirmacion,
                errorConfirmacion = null,
                mensaje = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(passwordVisible = !it.passwordVisible)
        }
    }

    fun toggleConfirmarVisibility() {
        _uiState.update {
            it.copy(confirmarVisible = !it.confirmarVisible)
        }
    }

    // Validar y guardar el usuario en SQLite.
    fun register() {

        val estado = _uiState.value

        if (estado.isLoading || estado.isRegistered) return

        val nombre = estado.nombre.trim()
        val correo = estado.correo.trim()
        val password = estado.password

        val errorNombre = if (nombre.isBlank()) {
            "Ingresa tu nombre completo"
        } else null

        val errorCorreo = when {
            correo.isBlank() ->
                "Ingresa tu correo electrónico"

            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
                "Ingresa un correo válido"

            else -> null
        }

        val errorPassword = when {
            password.isBlank() ->
                "Ingresa una contraseña"

            password.length < 8 ->
                "La contraseña debe tener mínimo 8 caracteres"

            else -> null
        }

        val errorConfirmacion = when {
            estado.confirmarPassword.isBlank() ->
                "Confirma tu contraseña"

            password != estado.confirmarPassword ->
                "Las contraseñas no coinciden"

            else -> null
        }

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorCorreo = errorCorreo,
                errorPassword = errorPassword,
                errorConfirmacion = errorConfirmacion,
                mensaje = null
            )
        }

        if (
            errorNombre != null ||
            errorCorreo != null ||
            errorPassword != null ||
            errorConfirmacion != null
        ) {
            return
        }

        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {

            try {

                val registrado = repository.registrar(
                    nombre,
                    correo,
                    password
                )

                if (registrado) {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistered = true,
                            password = "",
                            confirmarPassword = "",
                            mensaje = "Cuenta creada correctamente"
                        )
                    }

                } else {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorCorreo = "Este correo ya está registrado",
                            mensaje = "No se pudo crear la cuenta"
                        )
                    }
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensaje = "Error al registrar el usuario"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(mensaje = null)
        }
    }

    fun clearState() {
        _uiState.value = RegisterUiState()
    }
}