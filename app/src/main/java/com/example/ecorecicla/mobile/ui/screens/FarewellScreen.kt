package com.example.ecorecicla.mobile.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: FarewellScreen.kt
// RESPONSABILIDAD: Despedida ecológica animada.
// ======================================================

const val DURACION_DESPEDIDA = 3000L

private val Verde = Color(0xFF166534)
private val VerdeMedio = Color(0xFF43A047)
private val VerdeClaro = Color(0xFFE1F1E5)
private val Fondo = Color(0xFFF2F7F3)
private val Azul = Color(0xFF75BEDC)
private val Dorado = Color(0xFFF4C95D)
private val Gris = Color(0xFF526259)

// Modelo de mensajes de despedida.
private data class MensajeDespedida(
    val titulo: String,
    val descripcion: String
)

// Catálogo de mensajes variables.
private val mensajesDespedida = listOf(

    MensajeDespedida(
        "¡Hasta pronto!",
        "Gracias por hacer del mundo un lugar más verde."
    ),

    MensajeDespedida(
        "¡Nos vemos pronto!",
        "Cada pequeño gesto deja una gran huella."
    ),

    MensajeDespedida(
        "¡Sigue transformando el mundo!",
        "Tu compromiso ambiental hace la diferencia."
    ),

    MensajeDespedida(
        "¡Gracias por reciclar!",
        "Un futuro sostenible comienza con personas como tú."
    ),

    MensajeDespedida(
        "¡Vuelve pronto!",
        "Juntos podemos construir un planeta más limpio."
    ),

    MensajeDespedida(
        "¡Hasta la próxima!",
        "El planeta agradece cada una de tus acciones."
    )
)

// ======================================================
// PANTALLA PRINCIPAL DE DESPEDIDA
// ======================================================

@Composable
fun FarewellScreen(
    nombreUsuario: String? = null,
    variante: Int = 0
) {

    val indice = variante.coerceIn(
        0,
        mensajesDespedida.lastIndex
    )

    val mensaje = mensajesDespedida[indice]

    val entrada = remember(indice) {
        Animatable(0f)
    }

    val progreso = remember(indice) {
        Animatable(0f)
    }

    // Animación de entrada.
    LaunchedEffect(indice) {

        entrada.animateTo(
            targetValue = 1f,

            animationSpec = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            )
        )
    }

    // Barra de progreso.
    LaunchedEffect(indice) {

        progreso.animateTo(
            targetValue = 1f,

            animationSpec = tween(
                durationMillis =
                    DURACION_DESPEDIDA.toInt(),

                easing = LinearEasing
            )
        )
    }

    // Movimiento continuo del personaje.
    val movimiento = rememberInfiniteTransition(
        label = "Despedida ecológica"
    )

    val flotacion by movimiento.animateFloat(
        initialValue = -7f,

        targetValue = 7f,

        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            ),

            repeatMode = RepeatMode.Reverse
        ),

        label = "Flotación"
    )

    val saludo by movimiento.animateFloat(
        initialValue = -12f,

        targetValue = 12f,

        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 450
            ),

            repeatMode = RepeatMode.Reverse
        ),

        label = "Saludo"
    )

    Scaffold(
        containerColor = Fondo
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentAlignment = Alignment.Center
        ) {

            // Decoración ambiental.
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {

                drawCircle(
                    color = VerdeClaro.copy(
                        alpha = 0.7f
                    ),

                    radius = size.width * 0.43f,

                    center = Offset(
                        size.width,
                        size.height * 0.08f
                    )
                )

                drawCircle(
                    color = VerdeClaro,

                    radius = size.width * 0.40f,

                    center = Offset(
                        0f,
                        size.height * 0.95f
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(28.dp)
                    .graphicsLayer {
                        alpha = entrada.value

                        translationY =
                            (1f - entrada.value) * 30f
                    },

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                // Identidad de EcoRecicla.
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Verde,
                                RoundedCornerShape(14.dp)
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "♻",
                            fontSize = 30.sp,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = "EcoRecicla",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Verde
                    )
                }

                Spacer(Modifier.height(34.dp))

                // Personaje ambiental animado.
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .graphicsLayer {
                            translationY =
                                flotacion.dp.toPx()
                        },

                    contentAlignment =
                        Alignment.Center
                ) {

                    PlanetaDespedida()

                    Text(
                        text = "👋",

                        fontSize = 49.sp,

                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(
                                x = (-8).dp,
                                y = (-30).dp
                            )
                            .graphicsLayer {
                                rotationZ = saludo
                            }
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Nombre del usuario.
                nombreUsuario
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { nombre ->

                        Text(
                            text = nombre.substringBefore(" "),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Verde
                        )

                        Spacer(Modifier.height(8.dp))
                    }

                Text(
                    text = mensaje.titulo,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Verde,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = mensaje.descripcion,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Gris,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(25.dp))

                // Confirmación de sesión cerrada.
                Surface(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(18.dp),

                    color = VerdeClaro
                ) {

                    Row(
                        modifier = Modifier.padding(17.dp),

                        horizontalArrangement =
                            Arrangement.Center,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    Verde,
                                    CircleShape
                                ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text = "✓",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "Sesión cerrada correctamente",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Verde
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Barra de progreso animada.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .background(
                            Color(0xFFD5E6D8),
                            CircleShape
                        )
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(
                                progreso.value.coerceIn(
                                    0f,
                                    1f
                                )
                            )
                            .fillMaxHeight()
                            .background(
                                Verde,
                                CircleShape
                            )
                    )
                }

                Spacer(Modifier.height(13.dp))

                Text(
                    text = "Regresando al inicio...",
                    fontSize = 13.sp,
                    color = Gris
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Recicla · Gana · Transforma",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Verde
                )
            }
        }
    }
}

// ======================================================
// ILUSTRACIÓN VECTORIAL DEL PLANETA
// No requiere imágenes externas ni conexión a internet.
// ======================================================

@Composable
private fun PlanetaDespedida() {

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {

        val w = size.width
        val h = size.height

        // Fondo circular.
        drawCircle(
            color = VerdeClaro,
            radius = w * 0.45f,
            center = center
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = w * 0.36f,
            center = center
        )

        // Sombra inferior.
        drawOval(
            color = VerdeMedio.copy(alpha = 0.20f),

            topLeft = Offset(
                w * 0.24f,
                h * 0.82f
            ),

            size = Size(
                w * 0.52f,
                h * 0.07f
            )
        )

        // Planeta Tierra.
        drawCircle(
            color = Azul,

            radius = w * 0.29f,

            center = Offset(
                w * 0.50f,
                h * 0.49f
            )
        )

        // Primer continente.
        val continente1 = Path().apply {

            moveTo(w * 0.34f, h * 0.25f)

            lineTo(w * 0.48f, h * 0.26f)

            lineTo(w * 0.53f, h * 0.36f)

            lineTo(w * 0.42f, h * 0.41f)

            lineTo(w * 0.43f, h * 0.49f)

            lineTo(w * 0.31f, h * 0.45f)

            lineTo(w * 0.25f, h * 0.35f)

            close()
        }

        drawPath(
            path = continente1,
            color = VerdeMedio
        )

        // Segundo continente.
        val continente2 = Path().apply {

            moveTo(w * 0.56f, h * 0.44f)

            lineTo(w * 0.72f, h * 0.40f)

            lineTo(w * 0.78f, h * 0.53f)

            lineTo(w * 0.66f, h * 0.62f)

            lineTo(w * 0.59f, h * 0.75f)

            lineTo(w * 0.49f, h * 0.63f)

            close()
        }

        drawPath(
            path = continente2,
            color = VerdeMedio
        )

        // Ojo izquierdo.
        drawOval(
            color = Verde,

            topLeft = Offset(
                w * 0.40f,
                h * 0.46f
            ),

            size = Size(
                w * 0.035f,
                h * 0.06f
            )
        )

        // Ojo derecho.
        drawOval(
            color = Verde,

            topLeft = Offset(
                w * 0.57f,
                h * 0.46f
            ),

            size = Size(
                w * 0.035f,
                h * 0.06f
            )
        )

        // Sonrisa.
        val sonrisa = Path().apply {

            moveTo(w * 0.43f, h * 0.57f)

            quadraticBezierTo(
                w * 0.50f,
                h * 0.65f,
                w * 0.57f,
                h * 0.57f
            )
        }

        drawPath(
            path = sonrisa,

            color = Verde,

            style =
                androidx.compose.ui.graphics.drawscope.Stroke(
                    width = w * 0.015f,
                    cap = StrokeCap.Round
                )
        )

        // Hoja izquierda.
        val hoja1 = Path().apply {

            moveTo(w * 0.50f, h * 0.22f)

            quadraticBezierTo(
                w * 0.24f,
                h * 0.23f,
                w * 0.27f,
                h * 0.08f
            )

            quadraticBezierTo(
                w * 0.48f,
                h * 0.05f,
                w * 0.50f,
                h * 0.22f
            )

            close()
        }

        drawPath(
            path = hoja1,
            color = VerdeMedio
        )

        // Hoja derecha.
        val hoja2 = Path().apply {

            moveTo(w * 0.50f, h * 0.22f)

            quadraticBezierTo(
                w * 0.49f,
                h * 0.04f,
                w * 0.69f,
                h * 0.07f
            )

            quadraticBezierTo(
                w * 0.72f,
                h * 0.23f,
                w * 0.50f,
                h * 0.22f
            )

            close()
        }

        drawPath(
            path = hoja2,
            color = Verde
        )

        // Partículas decorativas.
        drawCircle(
            color = Dorado,
            radius = w * 0.024f,
            center = Offset(
                w * 0.13f,
                h * 0.25f
            )
        )

        drawCircle(
            color = Dorado,
            radius = w * 0.020f,
            center = Offset(
                w * 0.86f,
                h * 0.20f
            )
        )

        drawCircle(
            color = VerdeMedio,
            radius = w * 0.018f,
            center = Offset(
                w * 0.15f,
                h * 0.72f
            )
        )

        drawCircle(
            color = Dorado,
            radius = w * 0.022f,
            center = Offset(
                w * 0.85f,
                h * 0.76f
            )
        )
    }
}