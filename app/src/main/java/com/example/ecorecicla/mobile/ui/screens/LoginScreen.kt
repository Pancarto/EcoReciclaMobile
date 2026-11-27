package com.example.ecorecicla.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
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
// PATRÓN: OBSERVER
// RESPONSABILIDAD: Interfaz gráfica de autenticación.
// ======================================================

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLogin: (String, String) -> Unit = { _, _ -> },
    onRegister: () -> Unit = {}
) {

    // Observer: observar los cambios del ViewModel.
    val uiState by authViewModel.uiState.collectAsState()

    // Notificar únicamente cuando exista autenticación.
    LaunchedEffect(uiState.isAuthenticated) {

        if (uiState.isAuthenticated) {

            onLogin(
                uiState.email.trim(),
                uiState.password
            )
        }
    }

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

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Spacer(Modifier.height(24.dp))

            // ==========================================
            // IDENTIDAD VISUAL
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

            Spacer(Modifier.height(16.dp))

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

            Spacer(Modifier.height(32.dp))

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

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Inicia sesión y comienza a generar un impacto positivo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(24.dp))

                    // ==================================
                    // CORREO ELECTRÓNICO
                    // ==================================

                    OutlinedTextField(

                        value = uiState.email,

                        onValueChange =
                            authViewModel::onEmailChange,

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

                            uiState.emailError?.let {
                                Text(it)
                            }
                        }
                    )

                    Spacer(Modifier.height(8.dp))

                    // ==================================
                    // CONTRASEÑA
                    // ==================================

                    OutlinedTextField(

                        value = uiState.password,

                        onValueChange =
                            authViewModel::onPasswordChange,

                        modifier = Modifier.fillMaxWidth(),

                        label = {
                            Text("Contraseña")
                        },

                        placeholder = {
                            Text("Ingresa tu contraseña")
                        },

                        singleLine = true,

                        enabled = !uiState.isLoading,

                        isError =
                            uiState.passwordError != null,

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),

                        visualTransformation =
                            if (uiState.passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },

                        trailingIcon = {

                            TextButton(

                                onClick =
                                    authViewModel::togglePasswordVisibility

                            ) {

                                Text(
                                    if (uiState.passwordVisible)
                                        "Ocultar"
                                    else
                                        "Ver"
                                )
                            }
                        },

                        shape = MaterialTheme.shapes.medium,

                        supportingText = {

                            uiState.passwordError?.let {
                                Text(it)
                            }
                        }
                    )

                    // ==================================
                    // NUEVA OPCIÓN: RECORDAR SESIÓN
                    // ==================================

                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = uiState.recordarSesion,
                                enabled = !uiState.isLoading,
                                role = Role.Checkbox,
                                onValueChange =
                                    authViewModel::onRecordarSesionChange
                            )
                            .heightIn(min = 48.dp),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Checkbox(

                            checked = uiState.recordarSesion,

                            // La fila controla la interacción.
                            onCheckedChange = null,

                            enabled = !uiState.isLoading,

                            colors = CheckboxDefaults.colors(

                                checkedColor =
                                    MaterialTheme.colorScheme.primary,

                                checkmarkColor =
                                    MaterialTheme.colorScheme.onPrimary,

                                uncheckedColor =
                                    MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(

                            text = "Recordar sesión siempre",

                            style = MaterialTheme.typography.bodyMedium,

                            fontWeight = FontWeight.Medium,

                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Explicar el comportamiento de la opción.
                    Text(

                        text = if (uiState.recordarSesion) {

                            "Tu sesión permanecerá activa en este dispositivo hasta que decidas cerrarla."

                        } else {

                            "Activa esta opción si deseas ingresar automáticamente la próxima vez."
                        },

                        style = MaterialTheme.typography.bodySmall,

                        color = MaterialTheme.colorScheme.onSurfaceVariant,

                        modifier = Modifier.padding(
                            start = 36.dp,
                            bottom = 18.dp
                        )
                    )

                    // ==================================
                    // BOTÓN DE INICIO DE SESIÓN
                    // ==================================

                    Button(

                        onClick = authViewModel::login,

                        enabled = !uiState.isLoading,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape = MaterialTheme.shapes.medium

                    ) {

                        if (uiState.isLoading) {

                            CircularProgressIndicator(

                                modifier = Modifier.size(22.dp),

                                color = MaterialTheme.colorScheme.onPrimary,

                                strokeWidth = 2.dp
                            )

                            Spacer(Modifier.width(12.dp))

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

                        Spacer(Modifier.height(12.dp))

                        Text(

                            text = message,

                            style = MaterialTheme.typography.bodySmall,

                            color = if (uiState.isAuthenticated) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },

                            textAlign = TextAlign.Center,

                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Spacer(Modifier.height(12.dp))

                    // ==================================
                    // ACCESO AL REGISTRO
                    // ==================================

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.Center,

                        verticalAlignment =
                            Alignment.CenterVertically

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

            Spacer(Modifier.height(32.dp))

            // ==========================================
            // PIE DE PÁGINA
            // ==========================================

            Text(

                text = "Cada acción cuenta para un futuro sostenible.",

                style = MaterialTheme.typography.bodySmall,

                color = MaterialTheme.colorScheme.onSurfaceVariant,

                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(

                text = "EcoRecicla Mobile · V1.0",

                style = MaterialTheme.typography.labelSmall,

                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}