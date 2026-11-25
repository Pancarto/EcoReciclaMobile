package com.example.ecorecicla.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecorecicla.mobile.viewmodel.AuthViewModel

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: LoginScreen.kt
// ARQUITECTURA: MVVM
// PATRÓN DE COMPORTAMIENTO: OBSERVER
// RESPONSABILIDAD: Interfaz gráfica de autenticación.
// ======================================================

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLogin: (String, String) -> Unit = { _, _ -> },
    onRegister: () -> Unit = {}
) {

    // ==================================================
    // PATRÓN OBSERVER
    // ==================================================

    // Observa los cambios del StateFlow del ViewModel.
    // Compose actualiza la interfaz automáticamente.
    val uiState by authViewModel.uiState.collectAsState()

    // Notifica únicamente cuando exista autenticación real.
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLogin(uiState.email.trim(), uiState.password)
        }
    }

    // ==================================================
    // ESTRUCTURA PRINCIPAL DE LA INTERFAZ
    // ==================================================

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 24.dp,
                    vertical = 32.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // IDENTIDAD VISUAL DE ECORECICLA
            // ==========================================

            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "♻",
                    fontSize = 48.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EcoRecicla",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Recicla · Gana · Transforma",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ==========================================
            // TARJETA DE INICIO DE SESIÓN
            // ==========================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(
                        text = "Bienvenido",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Inicia sesión y comienza a generar un impacto positivo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ==================================
                    // CAMPO DE CORREO ELECTRÓNICO
                    // ==================================

                    OutlinedTextField(

                        // Obtiene el estado desde el ViewModel.
                        value = uiState.email,

                        // Envía los cambios al ViewModel.
                        onValueChange = authViewModel::onEmailChange,

                        modifier = Modifier.fillMaxWidth(),

                        label = {
                            Text("Correo electrónico")
                        },

                        placeholder = {
                            Text("Ingresa tu correo")
                        },

                        singleLine = true,

                        enabled = !uiState.isLoading,

                        isError = uiState.emailError != null,

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),

                        shape = MaterialTheme.shapes.medium,

                        supportingText = {
                            uiState.emailError?.let { error ->
                                Text(error)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ==================================
                    // CAMPO DE CONTRASEÑA
                    // ==================================

                    OutlinedTextField(

                        value = uiState.password,

                        onValueChange = authViewModel::onPasswordChange,

                        modifier = Modifier.fillMaxWidth(),

                        label = {
                            Text("Contraseña")
                        },

                        placeholder = {
                            Text("Ingresa tu contraseña")
                        },

                        singleLine = true,

                        enabled = !uiState.isLoading,

                        isError = uiState.passwordError != null,

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),

                        // El ViewModel controla la visibilidad.
                        visualTransformation =
                            if (uiState.passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },

                        trailingIcon = {

                            TextButton(
                                onClick = authViewModel::togglePasswordVisibility
                            ) {

                                Text(
                                    text = if (uiState.passwordVisible) {
                                        "Ocultar"
                                    } else {
                                        "Ver"
                                    }
                                )
                            }
                        },

                        shape = MaterialTheme.shapes.medium,

                        supportingText = {
                            uiState.passwordError?.let { error ->
                                Text(error)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ==================================
                    // BOTÓN DE INICIO DE SESIÓN
                    // ==================================

                    Button(

                        // Delega la validación al ViewModel.
                        onClick = authViewModel::login,

                        enabled = !uiState.isLoading,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape = MaterialTheme.shapes.medium

                    ) {

                        // Indicador preparado para peticiones HTTP.
                        if (uiState.isLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text("Iniciando sesión...")

                        } else {

                            Text(
                                text = "Iniciar sesión",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    // ==================================
                    // MENSAJES DE VALIDACIÓN
                    // ==================================

                    uiState.message?.let { message ->

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ==================================
                    // ACCESO A LA PANTALLA DE REGISTRO
                    // ==================================

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "¿No tienes cuenta?",
                            style = MaterialTheme.typography.bodySmall
                        )

                        TextButton(
                            onClick = onRegister,
                            enabled = !uiState.isLoading
                        ) {

                            Text(
                                text = "Regístrate",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ==========================================
            // PIE DE PÁGINA
            // ==========================================

            Text(
                text = "Cada acción cuenta para un futuro sostenible.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "EcoRecicla Mobile · V1.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}