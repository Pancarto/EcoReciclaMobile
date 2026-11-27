package com.example.ecorecicla.mobile.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: EcoBottomBar.kt
// RESPONSABILIDAD: Navegación inferior reutilizable.
// PATRÓN: Reutilización de componentes.
// STATE HOISTING: La pantalla activa se recibe externamente.
// ======================================================

// Colores de navegación.

private val BVerde = Color(0xFF166534)
private val BVerdeClaro = Color(0xFFE4F2E8)
private val BGris = Color(0xFF718078)
private val BBlanco = Color.White
private val BBorde = Color(0xFFE0E8E2)

// ======================================================
// DESTINOS DE NAVEGACIÓN
// ======================================================

enum class EcoTab(

    val titulo: String

) {

    INICIO("Inicio"),

    RECICLAR("Reciclar"),

    PREMIOS("Premios"),

    HISTORIAL("Historial"),

    PERFIL("Perfil")
}

// ======================================================
// BARRA PRINCIPAL
// ======================================================

@Composable
fun EcoBottomBar(

    selectedTab: EcoTab,

    onTabSelected: (EcoTab) -> Unit,

    modifier: Modifier = Modifier

) {

    Surface(

        modifier = modifier.fillMaxWidth(),

        color = BBlanco,

        shadowElevation = 10.dp

    ) {

        Column(

            modifier = Modifier.fillMaxWidth()

        ) {

            // Separación superior.

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BBorde)
            )

            // Destinos principales.

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .selectableGroup()
                    .padding(
                        horizontal = 7.dp,
                        vertical = 9.dp
                    ),

                horizontalArrangement =
                    Arrangement.SpaceEvenly,

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                EcoTab.values().forEach { tab ->

                    EcoBottomBarItem(

                        tab = tab,

                        seleccionado = selectedTab == tab,

                        modifier = Modifier.weight(1f),

                        onClick = {

                            if (selectedTab != tab) {

                                onTabSelected(tab)
                            }
                        }
                    )
                }
            }
        }
    }
}

// ======================================================
// ELEMENTO INDIVIDUAL
// ======================================================

@Composable
private fun EcoBottomBarItem(

    tab: EcoTab,

    seleccionado: Boolean,

    modifier: Modifier = Modifier,

    onClick: () -> Unit

) {

    // Animación del color del icono.

    val colorIcono by animateColorAsState(

        targetValue = if (seleccionado)
            BVerde
        else
            BGris,

        animationSpec = tween(220),

        label = "Color del icono"
    )

    // Animación del fondo seleccionado.

    val colorFondo by animateColorAsState(

        targetValue = if (seleccionado)
            BVerdeClaro
        else
            Color.Transparent,

        animationSpec = tween(220),

        label = "Fondo seleccionado"
    )

    // Animación del indicador inferior.

    val anchoIndicador by animateDpAsState(

        targetValue = if (seleccionado)
            19.dp
        else
            0.dp,

        animationSpec = tween(220),

        label = "Indicador de navegación"
    )

    Column(

        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .selectable(
                selected = seleccionado,
                onClick = onClick,
                role = Role.Tab
            )
            .padding(vertical = 4.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {

        Box(

            modifier = Modifier
                .widthIn(max = 60.dp)
                .fillMaxWidth()
                .height(34.dp)
                .background(
                    colorFondo,
                    RoundedCornerShape(20.dp)
                ),

            contentAlignment = Alignment.Center

        ) {

            EcoNavigationIcon(

                tab = tab,

                color = colorIcono,

                modifier = Modifier.size(23.dp)
            )
        }

        Spacer(Modifier.height(5.dp))

        Text(

            text = tab.titulo,

            fontSize = 10.sp,

            fontWeight = if (seleccionado)
                FontWeight.ExtraBold
            else
                FontWeight.Medium,

            color = colorIcono,

            maxLines = 1
        )

        Spacer(Modifier.height(6.dp))

        Box(

            modifier = Modifier
                .width(anchoIndicador)
                .height(3.dp)
                .background(
                    BVerde,
                    RoundedCornerShape(10.dp)
                )
        )
    }
}

// ======================================================
// ICONOS VECTORIALES PERSONALIZADOS
// No requieren dependencias adicionales.
// ======================================================

@Composable
private fun EcoNavigationIcon(

    tab: EcoTab,

    color: Color,

    modifier: Modifier = Modifier

) {

    Canvas(
        modifier = modifier
    ) {

        val w = size.width
        val h = size.height

        val grosor = w * 0.075f

        val estilo = Stroke(

            width = grosor,

            cap = StrokeCap.Round,

            join = StrokeJoin.Round
        )

        when (tab) {

            // ==========================================
            // ICONO DE INICIO
            // ==========================================

            EcoTab.INICIO -> {

                val techo = Path().apply {

                    moveTo(
                        w * 0.08f,
                        h * 0.45f
                    )

                    lineTo(
                        w * 0.50f,
                        h * 0.10f
                    )

                    lineTo(
                        w * 0.92f,
                        h * 0.45f
                    )
                }

                drawPath(
                    path = techo,
                    color = color,
                    style = estilo
                )

                val casa = Path().apply {

                    moveTo(
                        w * 0.20f,
                        h * 0.40f
                    )

                    lineTo(
                        w * 0.20f,
                        h * 0.90f
                    )

                    lineTo(
                        w * 0.40f,
                        h * 0.90f
                    )

                    lineTo(
                        w * 0.40f,
                        h * 0.63f
                    )

                    lineTo(
                        w * 0.60f,
                        h * 0.63f
                    )

                    lineTo(
                        w * 0.60f,
                        h * 0.90f
                    )

                    lineTo(
                        w * 0.80f,
                        h * 0.90f
                    )

                    lineTo(
                        w * 0.80f,
                        h * 0.40f
                    )
                }

                drawPath(
                    path = casa,
                    color = color,
                    style = estilo
                )
            }

            // ==========================================
            // ICONO DE RECICLAJE
            // ==========================================

            EcoTab.RECICLAR -> {

                // Primera flecha.

                val flechaSuperior = Path().apply {

                    moveTo(
                        w * 0.24f,
                        h * 0.40f
                    )

                    lineTo(
                        w * 0.44f,
                        h * 0.10f
                    )

                    quadraticBezierTo(
                        w * 0.50f,
                        h * 0.03f,
                        w * 0.56f,
                        h * 0.13f
                    )

                    lineTo(
                        w * 0.73f,
                        h * 0.40f
                    )
                }

                drawPath(
                    path = flechaSuperior,
                    color = color,
                    style = estilo
                )

                val puntaSuperior = Path().apply {

                    moveTo(
                        w * 0.56f,
                        h * 0.35f
                    )

                    lineTo(
                        w * 0.73f,
                        h * 0.40f
                    )

                    lineTo(
                        w * 0.79f,
                        h * 0.23f
                    )
                }

                drawPath(
                    path = puntaSuperior,
                    color = color,
                    style = estilo
                )

                // Segunda flecha.

                val flechaDerecha = Path().apply {

                    moveTo(
                        w * 0.80f,
                        h * 0.48f
                    )

                    lineTo(
                        w * 0.94f,
                        h * 0.73f
                    )

                    quadraticBezierTo(
                        w * 0.99f,
                        h * 0.84f,
                        w * 0.85f,
                        h * 0.84f
                    )

                    lineTo(
                        w * 0.48f,
                        h * 0.84f
                    )
                }

                drawPath(
                    path = flechaDerecha,
                    color = color,
                    style = estilo
                )

                val puntaDerecha = Path().apply {

                    moveTo(
                        w * 0.61f,
                        h * 0.69f
                    )

                    lineTo(
                        w * 0.48f,
                        h * 0.84f
                    )

                    lineTo(
                        w * 0.61f,
                        h * 0.96f
                    )
                }

                drawPath(
                    path = puntaDerecha,
                    color = color,
                    style = estilo
                )

                // Tercera flecha.

                val flechaIzquierda = Path().apply {

                    moveTo(
                        w * 0.37f,
                        h * 0.84f
                    )

                    lineTo(
                        w * 0.13f,
                        h * 0.84f
                    )

                    quadraticBezierTo(
                        w * 0.01f,
                        h * 0.84f,
                        w * 0.07f,
                        h * 0.72f
                    )

                    lineTo(
                        w * 0.25f,
                        h * 0.46f
                    )
                }

                drawPath(
                    path = flechaIzquierda,
                    color = color,
                    style = estilo
                )

                val puntaIzquierda = Path().apply {

                    moveTo(
                        w * 0.08f,
                        h * 0.50f
                    )

                    lineTo(
                        w * 0.25f,
                        h * 0.46f
                    )

                    lineTo(
                        w * 0.30f,
                        h * 0.64f
                    )
                }

                drawPath(
                    path = puntaIzquierda,
                    color = color,
                    style = estilo
                )
            }

            // ==========================================
            // ICONO DE RECOMPENSAS
            // ==========================================

            EcoTab.PREMIOS -> {

                // Cuerpo del regalo.

                drawRect(

                    color = color,

                    topLeft = Offset(
                        w * 0.16f,
                        h * 0.47f
                    ),

                    size = Size(
                        w * 0.68f,
                        h * 0.43f
                    ),

                    style = estilo
                )

                // Tapa.

                drawRect(

                    color = color,

                    topLeft = Offset(
                        w * 0.08f,
                        h * 0.31f
                    ),

                    size = Size(
                        w * 0.84f,
                        h * 0.16f
                    ),

                    style = estilo
                )

                // Cinta vertical.

                drawLine(

                    color = color,

                    start = Offset(
                        w * 0.50f,
                        h * 0.31f
                    ),

                    end = Offset(
                        w * 0.50f,
                        h * 0.90f
                    ),

                    strokeWidth = grosor,

                    cap = StrokeCap.Round
                )

                // Lazo izquierdo.

                val lazoIzquierdo = Path().apply {

                    moveTo(
                        w * 0.50f,
                        h * 0.31f
                    )

                    cubicTo(
                        w * 0.07f,
                        h * 0.32f,
                        w * 0.16f,
                        h * -0.10f,
                        w * 0.50f,
                        h * 0.31f
                    )
                }

                drawPath(
                    path = lazoIzquierdo,
                    color = color,
                    style = estilo
                )

                // Lazo derecho.

                val lazoDerecho = Path().apply {

                    moveTo(
                        w * 0.50f,
                        h * 0.31f
                    )

                    cubicTo(
                        w * 0.93f,
                        h * 0.32f,
                        w * 0.84f,
                        h * -0.10f,
                        w * 0.50f,
                        h * 0.31f
                    )
                }

                drawPath(
                    path = lazoDerecho,
                    color = color,
                    style = estilo
                )
            }

            // ==========================================
            // ICONO DE HISTORIAL
            // ==========================================

            EcoTab.HISTORIAL -> {

                // Círculo del reloj.

                drawArc(

                    color = color,

                    startAngle = 220f,

                    sweepAngle = 310f,

                    useCenter = false,

                    topLeft = Offset(
                        w * 0.12f,
                        h * 0.10f
                    ),

                    size = Size(
                        w * 0.78f,
                        h * 0.78f
                    ),

                    style = estilo
                )

                // Flecha de historial.

                val flecha = Path().apply {

                    moveTo(
                        w * 0.08f,
                        h * 0.15f
                    )

                    lineTo(
                        w * 0.08f,
                        h * 0.43f
                    )

                    lineTo(
                        w * 0.34f,
                        h * 0.43f
                    )
                }

                drawPath(
                    path = flecha,
                    color = color,
                    style = estilo
                )

                // Manecillas.

                val manecillas = Path().apply {

                    moveTo(
                        w * 0.51f,
                        h * 0.27f
                    )

                    lineTo(
                        w * 0.51f,
                        h * 0.51f
                    )

                    lineTo(
                        w * 0.68f,
                        h * 0.63f
                    )
                }

                drawPath(
                    path = manecillas,
                    color = color,
                    style = estilo
                )
            }

            // ==========================================
            // ICONO DE PERFIL
            // ==========================================

            EcoTab.PERFIL -> {

                // Cabeza.

                drawCircle(

                    color = color,

                    radius = w * 0.19f,

                    center = Offset(
                        w * 0.50f,
                        h * 0.27f
                    ),

                    style = estilo
                )

                // Silueta del cuerpo.

                val cuerpo = Path().apply {

                    moveTo(
                        w * 0.15f,
                        h * 0.90f
                    )

                    lineTo(
                        w * 0.15f,
                        h * 0.79f
                    )

                    cubicTo(
                        w * 0.15f,
                        h * 0.45f,
                        w * 0.85f,
                        h * 0.45f,
                        w * 0.85f,
                        h * 0.79f
                    )

                    lineTo(
                        w * 0.85f,
                        h * 0.90f
                    )

                    close()
                }

                drawPath(
                    path = cuerpo,
                    color = color,
                    style = estilo
                )
            }
        }
    }
}

// ======================================================
// VISTA PREVIA EN ANDROID STUDIO
// ======================================================

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF4F7F5
)
@Composable
private fun EcoBottomBarPreview() {

    var seleccionado by remember {

        mutableStateOf(EcoTab.INICIO)
    }

    EcoBottomBar(

        selectedTab = seleccionado,

        onTabSelected = {

            seleccionado = it
        }
    )
}

// ======================================================
// FIN DEL ARCHIVO
// ======================================================