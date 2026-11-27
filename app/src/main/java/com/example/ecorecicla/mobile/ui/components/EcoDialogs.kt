package com.example.ecorecicla.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: EcoDialogs.kt
// RESPONSABILIDAD: Ventanas emergentes reutilizables.
// ======================================================

// Identidad visual de los componentes.
private val Verde = Color(0xFF166534)
private val VerdeClaro = Color(0xFFE5F3E8)
private val Fondo = Color(0xFFF4F8F5)
private val Gris = Color(0xFF64748B)
private val Rojo = Color(0xFFB91C1C)
private val RojoClaro = Color(0xFFFDECEC)
private val Naranja = Color(0xFFD97706)
private val Texto = Color(0xFF183B26)

// ======================================================
// CONTENEDOR BASE REUTILIZABLE
// ======================================================

@Composable
private fun EcoDialogBase(
    onDismiss: () -> Unit,
    permitirCerrar: Boolean = true,
    contenido: @Composable ColumnScope.() -> Unit
) {

    Dialog(
        onDismissRequest = {
            if (permitirCerrar) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = permitirCerrar,
            dismissOnClickOutside = permitirCerrar,
            usePlatformDefaultWidth = false
        )
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 440.dp),

            shape = RoundedCornerShape(30.dp),

            color = Color.White,

            shadowElevation = 12.dp
        ) {

            Column(
                modifier = Modifier
                    .heightIn(max = 680.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                content = contenido
            )
        }
    }
}

// ======================================================
// ICONO CIRCULAR
// ======================================================

@Composable
private fun EcoIcono(
    simbolo: String,
    color: Color = Verde,
    fondo: Color = VerdeClaro
) {

    Box(
        modifier = Modifier
            .size(82.dp)
            .background(
                color = fondo,
                shape = CircleShape
            ),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = simbolo,
            fontSize = 38.sp,
            color = color
        )
    }
}

// ======================================================
// BOTÓN PRINCIPAL REUTILIZABLE
// ======================================================

@Composable
private fun EcoBoton(
    texto: String,
    onClick: () -> Unit,
    color: Color = Verde,
    habilitado: Boolean = true
) {

    Button(
        onClick = onClick,

        enabled = habilitado,

        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),

        shape = RoundedCornerShape(15.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White
        )
    ) {

        Text(
            text = texto,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ======================================================
// BOTÓN SECUNDARIO
// ======================================================

@Composable
private fun EcoBotonSecundario(
    texto: String,
    onClick: () -> Unit,
    color: Color = Verde
) {

    OutlinedButton(
        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),

        shape = RoundedCornerShape(15.dp),

        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color
        )
    ) {

        Text(
            text = texto,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ======================================================
// ENCABEZADO DE VENTANAS
// ======================================================

@Composable
private fun EcoEncabezado(
    titulo: String,
    descripcion: String
) {

    Text(
        text = titulo,
        fontSize = 25.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Texto,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(8.dp))

    Text(
        text = descripcion,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        color = Gris,
        textAlign = TextAlign.Center
    )
}

// ======================================================
// CUADRO PERSONALIZADO DEL PERFIL
// ======================================================

@Composable
fun EcoPerfilDialog(
    nombre: String,
    correo: String,
    puntos: Number,
    reciclajes: Number,
    kilogramos: Number,
    onDismiss: () -> Unit,
    onCerrarSesion: () -> Unit
) {

    EcoDialogBase(
        onDismiss = onDismiss
    ) {

        // Avatar con iniciales del usuario.
        val iniciales = nombre
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") {
                it.take(1).uppercase()
            }
            .ifBlank { "E" }

        Box(
            modifier = Modifier
                .size(88.dp)
                .background(
                    color = Verde,
                    shape = CircleShape
                )
                .border(
                    width = 5.dp,
                    color = VerdeClaro,
                    shape = CircleShape
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = iniciales,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(15.dp))

        EcoEncabezado(
            titulo = "Mi perfil",
            descripcion = "Comunidad EcoRecicla"
        )

        Spacer(Modifier.height(22.dp))

        // Información personal.
        Surface(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(18.dp),

            color = Fondo
        ) {

            Column(
                modifier = Modifier.padding(17.dp)
            ) {

                Text(
                    text = "INFORMACIÓN PERSONAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Verde
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Nombre completo",
                    fontSize = 12.sp,
                    color = Gris
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Texto
                )

                Spacer(Modifier.height(13.dp))

                HorizontalDivider(
                    color = Color(0xFFDCE8DF)
                )

                Spacer(Modifier.height(13.dp))

                Text(
                    text = "Correo electrónico",
                    fontSize = 12.sp,
                    color = Gris
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = correo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Texto
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Tu impacto ambiental",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Verde,

            modifier = Modifier.align(
                Alignment.Start
            )
        )

        Spacer(Modifier.height(12.dp))

        // Tabla visual de estadísticas reales.
        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            EcoEstadistica(
                modifier = Modifier.weight(1f),
                icono = "🏅",
                valor = puntos.toString(),
                etiqueta = "Puntos"
            )

            EcoEstadistica(
                modifier = Modifier.weight(1f),
                icono = "♻",
                valor = reciclajes.toString(),
                etiqueta = "Reciclajes"
            )

            EcoEstadistica(
                modifier = Modifier.weight(1f),
                icono = "🌱",
                valor = kilogramos.toString(),
                etiqueta = "Kilogramos"
            )
        }

        Spacer(Modifier.height(20.dp))

        EcoMensajeInformativo(
            icono = "🌿",
            mensaje = "Gracias por contribuir a una comunidad más sostenible."
        )

        Spacer(Modifier.height(24.dp))

        EcoBoton(
            texto = "Volver al inicio",
            onClick = onDismiss
        )

        Spacer(Modifier.height(10.dp))

        EcoBotonSecundario(
            texto = "Cerrar sesión",
            onClick = onCerrarSesion,
            color = Rojo
        )
    }
}

// ======================================================
// CELDA DE ESTADÍSTICAS
// ======================================================

@Composable
private fun EcoEstadistica(
    modifier: Modifier = Modifier,
    icono: String,
    valor: String,
    etiqueta: String
) {

    Surface(
        modifier = modifier,

        shape = RoundedCornerShape(16.dp),

        color = VerdeClaro
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 5.dp,
                vertical = 15.dp
            ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = icono,
                fontSize = 25.sp,
                color = Verde
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = valor,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Verde,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = etiqueta,
                fontSize = 11.sp,
                color = Gris,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ======================================================
// MENSAJE INFORMATIVO
// ======================================================

@Composable
private fun EcoMensajeInformativo(
    icono: String,
    mensaje: String
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(15.dp),

        color = VerdeClaro
    ) {

        Row(
            modifier = Modifier.padding(14.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = icono,
                fontSize = 25.sp
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = mensaje,
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Verde
            )
        }
    }
}

// ======================================================
// CONFIRMACIÓN DE CIERRE DE SESIÓN
// ======================================================

@Composable
fun EcoCerrarSesionDialog(
    nombre: String,
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit
) {

    EcoDialogBase(
        onDismiss = onCancelar
    ) {

        EcoIcono(
            simbolo = "↪",
            color = Naranja,
            fondo = Color(0xFFFFF1DF)
        )

        Spacer(Modifier.height(20.dp))

        EcoEncabezado(
            titulo = "¿Ya te vas?",
            descripcion = "${nombre.substringBefore(" ")}, ¿estás seguro de que deseas cerrar tu sesión?"
        )

        Spacer(Modifier.height(22.dp))

        EcoMensajeInformativo(
            icono = "🔒",
            mensaje = "Tus puntos, reciclajes y recompensas permanecerán guardados."
        )

        Spacer(Modifier.height(24.dp))

        EcoBoton(
            texto = "Sí, cerrar sesión",
            color = Rojo,
            onClick = onConfirmar
        )

        Spacer(Modifier.height(10.dp))

        EcoBotonSecundario(
            texto = "Seguir reciclando",
            onClick = onCancelar
        )
    }
}

// ======================================================
// CUADRO DE ÉXITO REUTILIZABLE
// ======================================================

@Composable
fun EcoExitoDialog(
    titulo: String,
    mensaje: String,
    detalle: String? = null,
    icono: String = "✓",
    textoBoton: String = "Continuar",
    onDismiss: () -> Unit
) {

    EcoDialogBase(
        onDismiss = onDismiss
    ) {

        EcoIcono(
            simbolo = icono
        )

        Spacer(Modifier.height(20.dp))

        EcoEncabezado(
            titulo = titulo,
            descripcion = mensaje
        )

        detalle?.let {

            Spacer(Modifier.height(20.dp))

            EcoMensajeInformativo(
                icono = "🌱",
                mensaje = it
            )
        }

        Spacer(Modifier.height(24.dp))

        EcoBoton(
            texto = textoBoton,
            onClick = onDismiss
        )
    }
}

// ======================================================
// CUADRO DE ERROR REUTILIZABLE
// ======================================================

@Composable
fun EcoErrorDialog(
    titulo: String = "Algo salió mal",
    mensaje: String,
    onDismiss: () -> Unit,
    onReintentar: (() -> Unit)? = null
) {

    EcoDialogBase(
        onDismiss = onDismiss
    ) {

        EcoIcono(
            simbolo = "!",
            color = Rojo,
            fondo = RojoClaro
        )

        Spacer(Modifier.height(20.dp))

        EcoEncabezado(
            titulo = titulo,
            descripcion = mensaje
        )

        Spacer(Modifier.height(24.dp))

        if (onReintentar != null) {

            EcoBoton(
                texto = "Reintentar",
                onClick = onReintentar
            )

            Spacer(Modifier.height(10.dp))

            EcoBotonSecundario(
                texto = "Cancelar",
                onClick = onDismiss
            )

        } else {

            EcoBoton(
                texto = "Entendido",
                onClick = onDismiss
            )
        }
    }
}

// ======================================================
// CUADRO DE CARGA PERSONALIZADO
// ======================================================

@Composable
fun EcoCargandoDialog(
    titulo: String = "Un momento...",
    mensaje: String = "Estamos preparando todo para ti."
) {

    EcoDialogBase(
        onDismiss = {},
        permitirCerrar = false
    ) {

        Box(
            modifier = Modifier
                .size(90.dp)
                .background(
                    color = VerdeClaro,
                    shape = CircleShape
                ),

            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator(
                modifier = Modifier.size(46.dp),
                color = Verde,
                strokeWidth = 4.dp
            )
        }

        Spacer(Modifier.height(22.dp))

        EcoEncabezado(
            titulo = titulo,
            descripcion = mensaje
        )

        Spacer(Modifier.height(12.dp))
    }
}