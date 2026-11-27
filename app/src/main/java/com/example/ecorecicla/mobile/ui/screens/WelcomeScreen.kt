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
// ARCHIVO: WelcomeScreen.kt
// RESPONSABILIDAD: Bienvenida ecológica animada.
// ======================================================

const val DURACION_BIENVENIDA = 3000L

private val Verde = Color(0xFF166534)
private val VerdeMedio = Color(0xFF43A047)
private val VerdeClaro = Color(0xFFE1F1E5)
private val Fondo = Color(0xFFF2F7F3)
private val Dorado = Color(0xFFF4C95D)
private val Azul = Color(0xFF65B5DB)
private val Tierra = Color(0xFF987052)

// Información de cada bienvenida.
private data class EscenaBienvenida(
    val titulo: String,
    val mensaje: String,
    val consejo: String
)

// Catálogo de mensajes ambientales.
private val escenas = listOf(

    EscenaBienvenida(
        "Cada acción cuenta",
        "Reciclar hoy es construir un mejor mañana.",
        "Separa correctamente tus residuos."
    ),

    EscenaBienvenida(
        "Un futuro más verde",
        "Tus pequeñas acciones generan grandes cambios.",
        "Cuida los espacios que compartimos."
    ),

    EscenaBienvenida(
        "Cuidemos nuestro planeta",
        "El cambio comienza contigo.",
        "Reduce, reutiliza y recicla."
    ),

    EscenaBienvenida(
        "Recicla y gana",
        "Tu compromiso ambiental merece una recompensa.",
        "Transforma tus residuos en oportunidades."
    ),

    EscenaBienvenida(
        "Cultivemos el cambio",
        "Juntos construimos una comunidad sostenible.",
        "Inspira a otros con tus acciones."
    ),

    EscenaBienvenida(
        "Protejamos nuestros recursos",
        "Dale una segunda vida a los materiales.",
        "Cada recurso que ahorras hace la diferencia."
    )
)

// ======================================================
// PANTALLA PRINCIPAL DE BIENVENIDA
// ======================================================

@Composable
fun WelcomeScreen(
    variante: Int = 0,
    nombreUsuario: String? = null
) {

    val indice = variante.coerceIn(0, escenas.lastIndex)
    val escena = escenas[indice]

    // Animación de aparición.
    val entrada = remember(indice) {
        Animatable(0f)
    }

    // Progreso visual de la bienvenida.
    val progreso = remember(indice) {
        Animatable(0f)
    }

    LaunchedEffect(indice) {

        entrada.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 650,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(indice) {

        progreso.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = DURACION_BIENVENIDA.toInt(),
                easing = LinearEasing
            )
        )
    }

    // Movimiento suave de la ilustración.
    val movimiento = rememberInfiniteTransition(
        label = "Movimiento ecológico"
    )

    val flotacion by movimiento.animateFloat(

        initialValue = -7f,

        targetValue = 7f,

        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),

        label = "Flotación"
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

            // Decoración ambiental del fondo.
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {

                drawCircle(
                    color = VerdeClaro.copy(alpha = 0.7f),
                    radius = size.width * 0.42f,
                    center = Offset(
                        size.width * 0.95f,
                        size.height * 0.08f
                    )
                )

                drawCircle(
                    color = VerdeClaro.copy(alpha = 0.6f),
                    radius = size.width * 0.48f,
                    center = Offset(
                        0f,
                        size.height * 0.94f
                    )
                )
            }

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(28.dp)
                    .graphicsLayer {
                        alpha = entrada.value
                        translationY =
                            (1f - entrada.value) * 35f
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

                        contentAlignment = Alignment.Center

                    ) {

                        Text(
                            "♻",
                            fontSize = 30.sp,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        "EcoRecicla",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Verde
                    )
                }

                Spacer(Modifier.height(38.dp))

                // Ilustración ecológica animada.
                Box(

                    modifier = Modifier
                        .size(220.dp)
                        .graphicsLayer {
                            translationY = flotacion.dp.toPx()
                        },

                    contentAlignment = Alignment.Center

                ) {

                    IlustracionEcologica(indice)

                    if (indice == 0) {

                        Text(
                            "♻",
                            fontSize = 100.sp,
                            color = Verde
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Saludo personalizado.
                nombreUsuario
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { nombre ->

                        Text(
                            "¡Bienvenido, ${nombre.substringBefore(" ")}!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Verde
                        )

                        Spacer(Modifier.height(12.dp))
                    }

                // Mensaje principal.
                Text(
                    text = escena.titulo,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Verde,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = escena.mensaje,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFF526259),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(26.dp))

                // Consejo ambiental.
                Surface(

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(18.dp),

                    color = VerdeClaro

                ) {

                    Row(

                        modifier = Modifier.padding(18.dp),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Text(
                            "💡",
                            fontSize = 28.sp
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                "Consejo ecológico",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Verde
                            )

                            Spacer(Modifier.height(5.dp))

                            Text(
                                escena.consejo,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = Verde
                            )
                        }
                    }
                }

                Spacer(Modifier.height(34.dp))

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
                                progreso.value.coerceIn(0f, 1f)
                            )
                            .fillMaxHeight()
                            .background(
                                Verde,
                                CircleShape
                            )
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    "Preparando tu experiencia...",
                    fontSize = 13.sp,
                    color = Color(0xFF526259)
                )

                Spacer(Modifier.height(26.dp))

                Text(
                    "Recicla · Gana · Transforma",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Verde
                )
            }
        }
    }
}

// ======================================================
// ILUSTRACIONES VECTORIALES
// Dibujadas directamente con Canvas.
// No requieren imágenes externas ni conexión a internet.
// ======================================================

@Composable
private fun IlustracionEcologica(
    variante: Int
) {

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
            color = Color.White.copy(alpha = 0.45f),
            radius = w * 0.37f,
            center = center
        )

        // Partículas decorativas.
        drawCircle(
            color = Dorado,
            radius = w * 0.025f,
            center = Offset(w * 0.14f, h * 0.20f)
        )

        drawCircle(
            color = VerdeMedio,
            radius = w * 0.018f,
            center = Offset(w * 0.86f, h * 0.24f)
        )

        drawCircle(
            color = Dorado,
            radius = w * 0.018f,
            center = Offset(w * 0.88f, h * 0.77f)
        )

        drawCircle(
            color = VerdeMedio,
            radius = w * 0.025f,
            center = Offset(w * 0.12f, h * 0.76f)
        )

        when (variante) {

            // 1. Símbolo de reciclaje.
            0 -> {

                drawCircle(
                    color = Color.White,
                    radius = w * 0.30f,
                    center = center
                )
            }

            // 2. Árbol ecológico.
            1 -> {

                drawRoundRect(
                    color = Tierra,
                    topLeft = Offset(w * 0.45f, h * 0.48f),
                    size = Size(w * 0.10f, h * 0.31f)
                )

                drawCircle(
                    color = Verde,
                    radius = w * 0.20f,
                    center = Offset(w * 0.50f, h * 0.33f)
                )

                drawCircle(
                    color = VerdeMedio,
                    radius = w * 0.17f,
                    center = Offset(w * 0.33f, h * 0.46f)
                )

                drawCircle(
                    color = VerdeMedio,
                    radius = w * 0.17f,
                    center = Offset(w * 0.67f, h * 0.46f)
                )

                drawOval(
                    color = VerdeMedio.copy(alpha = 0.35f),
                    topLeft = Offset(w * 0.25f, h * 0.77f),
                    size = Size(w * 0.50f, h * 0.07f)
                )
            }

            // 3. Planeta Tierra estilizado.
            2 -> {

                drawCircle(
                    color = Azul,
                    radius = w * 0.31f,
                    center = center
                )

                val continente1 = Path().apply {

                    moveTo(w * 0.35f, h * 0.25f)
                    lineTo(w * 0.49f, h * 0.27f)
                    lineTo(w * 0.55f, h * 0.37f)
                    lineTo(w * 0.43f, h * 0.43f)
                    lineTo(w * 0.45f, h * 0.52f)
                    lineTo(w * 0.31f, h * 0.47f)
                    lineTo(w * 0.25f, h * 0.35f)
                    close()
                }

                drawPath(
                    path = continente1,
                    color = VerdeMedio
                )

                val continente2 = Path().apply {

                    moveTo(w * 0.53f, h * 0.49f)
                    lineTo(w * 0.70f, h * 0.43f)
                    lineTo(w * 0.77f, h * 0.55f)
                    lineTo(w * 0.65f, h * 0.62f)
                    lineTo(w * 0.59f, h * 0.77f)
                    lineTo(w * 0.49f, h * 0.64f)
                    close()
                }

                drawPath(
                    path = continente2,
                    color = VerdeMedio
                )
            }

            // 4. Regalo y recompensas.
            3 -> {

                drawRoundRect(
                    color = VerdeMedio,
                    topLeft = Offset(w * 0.25f, h * 0.42f),
                    size = Size(w * 0.50f, h * 0.32f)
                )

                drawRoundRect(
                    color = Verde,
                    topLeft = Offset(w * 0.21f, h * 0.35f),
                    size = Size(w * 0.58f, h * 0.12f)
                )

                drawRect(
                    color = Dorado,
                    topLeft = Offset(w * 0.46f, h * 0.35f),
                    size = Size(w * 0.08f, h * 0.39f)
                )

                drawOval(
                    color = Dorado,
                    topLeft = Offset(w * 0.29f, h * 0.22f),
                    size = Size(w * 0.22f, h * 0.13f)
                )

                drawOval(
                    color = Dorado,
                    topLeft = Offset(w * 0.49f, h * 0.22f),
                    size = Size(w * 0.22f, h * 0.13f)
                )
            }

            // 5. Planta en crecimiento.
            4 -> {

                drawOval(
                    color = Tierra,
                    topLeft = Offset(w * 0.22f, h * 0.69f),
                    size = Size(w * 0.56f, h * 0.11f)
                )

                drawLine(
                    color = Verde,
                    start = Offset(w * 0.50f, h * 0.72f),
                    end = Offset(w * 0.50f, h * 0.29f),
                    strokeWidth = w * 0.035f,
                    cap = StrokeCap.Round
                )

                val hojaIzquierda = Path().apply {

                    moveTo(w * 0.50f, h * 0.54f)

                    quadraticBezierTo(
                        w * 0.22f, h * 0.56f,
                        w * 0.25f, h * 0.30f
                    )

                    quadraticBezierTo(
                        w * 0.50f, h * 0.29f,
                        w * 0.50f, h * 0.54f
                    )

                    close()
                }

                drawPath(
                    hojaIzquierda,
                    VerdeMedio
                )

                val hojaDerecha = Path().apply {

                    moveTo(w * 0.50f, h * 0.43f)

                    quadraticBezierTo(
                        w * 0.49f, h * 0.19f,
                        w * 0.76f, h * 0.20f
                    )

                    quadraticBezierTo(
                        w * 0.80f, h * 0.44f,
                        w * 0.50f, h * 0.43f
                    )

                    close()
                }

                drawPath(
                    hojaDerecha,
                    Verde
                )
            }

            // 6. Gota de agua.
            5 -> {

                val gota = Path().apply {

                    moveTo(w * 0.50f, h * 0.16f)

                    cubicTo(
                        w * 0.40f, h * 0.35f,
                        w * 0.23f, h * 0.49f,
                        w * 0.25f, h * 0.62f
                    )

                    cubicTo(
                        w * 0.27f, h * 0.87f,
                        w * 0.73f, h * 0.87f,
                        w * 0.75f, h * 0.62f
                    )

                    cubicTo(
                        w * 0.77f, h * 0.49f,
                        w * 0.60f, h * 0.35f,
                        w * 0.50f, h * 0.16f
                    )

                    close()
                }

                drawPath(
                    path = gota,
                    color = Azul
                )

                drawOval(
                    color = Color.White.copy(alpha = 0.65f),
                    topLeft = Offset(w * 0.33f, h * 0.48f),
                    size = Size(w * 0.08f, h * 0.18f)
                )
            }
        }
    }
}