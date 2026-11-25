package com.example.ecorecicla.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecorecicla.mobile.viewmodel.RegisterViewModel

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: RegisterScreen.kt
// ARQUITECTURA: MVVM
// PATRÓN DE COMPORTAMIENTO: OBSERVER
// RESPONSABILIDAD: Interfaz gráfica de registro.
// ======================================================

@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit = {},
    registerViewModel: RegisterViewModel = viewModel()
) {

    // ==================================================
    // PATRÓN OBSERVER
    // ==================================================

    // Observa los cambios del estado del ViewModel.
    // Compose actualiza automáticamente la interfaz.

    val uiState by registerViewModel.uiState.collectAsState()

    // ==================================================
    // IDENTIDAD VISUAL
    // ==================================================

    val verde = Color(0xFF166534)
    val fondo = Color(0xFFF2F7F3)
    val textoOscuro = Color(0xFF17251B)

    // ==================================================
    // COLORES DE LOS CAMPOS
    // ==================================================

    val coloresCampos = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = verde,
        unfocusedBorderColor = Color(0xFF82988A),
        focusedLabelColor = verde,
        cursorColor = verde
    )

    // ==================================================
    // CONTENEDOR PRINCIPAL
    // ==================================================

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 24.dp,
                vertical = 24.dp
            ),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        // ==============================================
        // LOGOTIPO
        // ==============================================

        Box(

            modifier = Modifier
                .size(76.dp)
                .background(
                    color = verde,
                    shape = RoundedCornerShape(20.dp)
                ),

            contentAlignment = Alignment.Center

        ) {

            Text(
                text = "♻",
                fontSize = 42.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "EcoRecicla",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = verde
        )

        Text(
            text = "Recicla · Gana · Transforma",
            fontSize = 13.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ==============================================
        // TARJETA DE REGISTRO
        // ==============================================

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(24.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )

        ) {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    text = "Crea tu cuenta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textoOscuro
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Únete a nuestra comunidad y comienza a transformar el planeta.",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ======================================
                // CAMPO: NOMBRE COMPLETO
                // ======================================

                OutlinedTextField(

                    // Obtiene el estado desde el ViewModel.
                    value = uiState.nombre,

                    // Delega los cambios al ViewModel.
                    onValueChange =
                        registerViewModel::onNombreChange,

                    label = {
                        Text("Nombre completo")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    enabled = !uiState.isLoading,

                    isError = uiState.errorNombre != null,

                    shape = RoundedCornerShape(14.dp),

                    colors = coloresCampos,

                    supportingText =
                        if (uiState.errorNombre != null) {
                            {
                                Text(
                                    uiState.errorNombre.orEmpty()
                                )
                            }
                        } else null
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ======================================
                // CAMPO: CORREO ELECTRÓNICO
                // ======================================

                OutlinedTextField(

                    value = uiState.correo,

                    onValueChange =
                        registerViewModel::onCorreoChange,

                    label = {
                        Text("Correo electrónico")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    enabled = !uiState.isLoading,

                    isError = uiState.errorCorreo != null,

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),

                    shape = RoundedCornerShape(14.dp),

                    colors = coloresCampos,

                    supportingText =
                        if (uiState.errorCorreo != null) {
                            {
                                Text(
                                    uiState.errorCorreo.orEmpty()
                                )
                            }
                        } else null
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ======================================
                // CAMPO: CONTRASEÑA
                // ======================================

                OutlinedTextField(

                    value = uiState.password,

                    onValueChange =
                        registerViewModel::onPasswordChange,

                    label = {
                        Text("Contraseña")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    enabled = !uiState.isLoading,

                    isError = uiState.errorPassword != null,

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),

                    shape = RoundedCornerShape(14.dp),

                    colors = coloresCampos,

                    // Visibilidad controlada por el ViewModel.

                    visualTransformation =
                        if (uiState.passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },

                    trailingIcon = {

                        TextButton(

                            onClick =
                                registerViewModel::togglePasswordVisibility

                        ) {

                            Text(

                                text = if (uiState.passwordVisible)
                                    "Ocultar"
                                else
                                    "Ver",

                                color = verde,
                                fontSize = 12.sp
                            )
                        }
                    },

                    supportingText =
                        if (uiState.errorPassword != null) {
                            {
                                Text(
                                    uiState.errorPassword.orEmpty()
                                )
                            }
                        } else null
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ======================================
                // CAMPO: CONFIRMAR CONTRASEÑA
                // ======================================

                OutlinedTextField(

                    value = uiState.confirmarPassword,

                    onValueChange =
                        registerViewModel::onConfirmarPasswordChange,

                    label = {
                        Text("Confirmar contraseña")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    enabled = !uiState.isLoading,

                    isError =
                        uiState.errorConfirmacion != null,

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),

                    shape = RoundedCornerShape(14.dp),

                    colors = coloresCampos,

                    visualTransformation =
                        if (uiState.confirmarVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },

                    trailingIcon = {

                        TextButton(

                            onClick =
                                registerViewModel::toggleConfirmarVisibility

                        ) {

                            Text(

                                text = if (uiState.confirmarVisible)
                                    "Ocultar"
                                else
                                    "Ver",

                                color = verde,
                                fontSize = 12.sp
                            )
                        }
                    },

                    supportingText =
                        if (uiState.errorConfirmacion != null) {
                            {
                                Text(
                                    uiState.errorConfirmacion.orEmpty()
                                )
                            }
                        } else null
                )

                Spacer(modifier = Modifier.height(22.dp))

                // ======================================
                // BOTÓN: CREAR CUENTA
                // ======================================

                Button(

                    // Toda la validación se realiza
                    // desde RegisterViewModel.

                    onClick = registerViewModel::register,

                    enabled = !uiState.isLoading,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                    shape = RoundedCornerShape(14.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = verde
                    )

                ) {

                    // Indicador preparado para la API.

                    if (uiState.isLoading) {

                        CircularProgressIndicator(

                            modifier = Modifier.size(22.dp),

                            color = Color.White,

                            strokeWidth = 2.dp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text("Creando cuenta...")

                    } else {

                        Text(
                            text = "Crear cuenta",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // ======================================
                // MENSAJE INFORMATIVO
                // ======================================

                uiState.mensaje?.let { mensaje ->

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(

                        text = mensaje,

                        color = verde,

                        fontSize = 12.sp,

                        textAlign = TextAlign.Center,

                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(
                    color = Color(0xFFDCE7DF)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ======================================
                // REGRESAR AL INICIO DE SESIÓN
                // ======================================

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement = Arrangement.Center,

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(
                        text = "¿Ya tienes cuenta?",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    TextButton(

                        onClick = {

                            // Limpia los datos del registro
                            // antes de regresar al login.

                            registerViewModel.clearState()

                            onLoginClick()
                        },

                        enabled = !uiState.isLoading

                    ) {

                        Text(
                            text = "Inicia sesión",
                            color = verde,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==============================================
        // PIE DE PÁGINA
        // ==============================================

        Text(
            text = "Cada acción cuenta para un futuro sostenible.",
            fontSize = 11.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "EcoRecicla Mobile · V1.0",
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}