package com.example.ecorecicla.mobile.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecorecicla.mobile.viewmodel.DashboardViewModel
import com.example.ecorecicla.mobile.ui.theme.EcoReciclaMobileTheme
import com.example.ecorecicla.mobile.ui.components.EcoBottomBar
import com.example.ecorecicla.mobile.ui.components.EcoTab
import java.text.NumberFormat
import java.util.Locale

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: DashboardScreen.kt
// RESPONSABILIDAD: Interfaz principal del usuario.
// ARQUITECTURA: MVVM.
// PATRONES: Observer, State Hoisting y reutilización.
// ======================================================

// Identidad visual institucional de EcoRecicla.

private val VerdePrincipal = Color(0xFF166534)
private val VerdeProfundo = Color(0xFF103D29)
private val VerdeMedio = Color(0xFF23834D)
private val VerdeClaro = Color(0xFFE4F2E8)
private val FondoDashboard = Color(0xFFF4F7F5)
private val TextoOscuro = Color(0xFF172B21)
private val TextoSecundario = Color(0xFF718078)
private val Blanco = Color.White
private val Dorado = Color(0xFFEBC76C)
private val Azul = Color(0xFF3469A6)
private val Naranja = Color(0xFFB77924)
private val BordeDashboard = Color(0xFFE0E8E2)

// ======================================================
// FORMATO NUMÉRICO COLOMBIANO
// ======================================================

private fun formatoDashboard(numero: Number): String {

    return NumberFormat
        .getNumberInstance(Locale("es", "CO"))
        .apply {
            maximumFractionDigits = 2
        }
        .format(numero)
}

// ======================================================
// PANTALLA PRINCIPAL
// ======================================================

@Composable
fun DashboardScreen(

    nombreUsuario: String = "Usuario de prueba",

    puntos: Int = 0,

    kilogramos: Double = 0.0,

    reciclajes: Int = 0,

    onRecyclingClick: () -> Unit = {},

    onRewardsClick: () -> Unit = {},

    onPointsClick: () -> Unit = {},

    onHistoryClick: () -> Unit = {},

    onProfileClick: () -> Unit = {},

    // Nuevo evento para consultar los premios canjeados.

    onMyRewardsClick: () -> Unit = onRewardsClick

) {

    val primerNombre = nombreUsuario
        .trim()
        .substringBefore(" ")
        .ifBlank { "Reciclador" }

    Scaffold(

        containerColor = FondoDashboard,

        // Barra reutilizable de navegación.
        // AppNavigation no muestra otra barra en el dashboard.

        bottomBar = {

            EcoBottomBar(

                selectedTab = EcoTab.INICIO,

                onTabSelected = { tab ->

                    when (tab) {

                        EcoTab.INICIO -> Unit

                        EcoTab.RECICLAR ->
                            onRecyclingClick()

                        EcoTab.PREMIOS ->
                            onRewardsClick()

                        EcoTab.HISTORIAL ->
                            onHistoryClick()

                        EcoTab.PERFIL ->
                            onProfileClick()
                    }
                }
            )
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),

            verticalArrangement =
                Arrangement.spacedBy(22.dp)

        ) {

            Spacer(Modifier.height(2.dp))

            // Encabezado principal.

            EncabezadoDashboard(

                nombreUsuario = nombreUsuario,

                onProfileClick = onProfileClick
            )

            // Saludo personalizado.

            Column {

                Text(

                    text = "TU ESPACIO SOSTENIBLE",

                    fontSize = 10.sp,

                    letterSpacing = 1.8.sp,

                    fontWeight = FontWeight.Bold,

                    color = VerdeMedio
                )

                Spacer(Modifier.height(8.dp))

                Text(

                    text = "¡Hola, $primerNombre!",

                    fontSize = 29.sp,

                    fontWeight = FontWeight.ExtraBold,

                    color = TextoOscuro
                )

                Spacer(Modifier.height(6.dp))

                Text(

                    text = "Hoy tienes una nueva oportunidad para transformar el planeta.",

                    fontSize = 13.sp,

                    lineHeight = 20.sp,

                    color = TextoSecundario
                )
            }

            // Billetera de puntos.

            TarjetaSaldo(

                puntos = puntos,

                onRewardsClick = onRewardsClick,

                onPointsClick = onPointsClick
            )

            // Estadísticas reales del usuario.

            Column {

                TituloSeccion(

                    titulo = "Tu impacto ambiental",

                    subtitulo =
                        "Cada acción deja una huella positiva."
                )

                Spacer(Modifier.height(14.dp))

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)

                ) {

                    TarjetaImpacto(

                        modifier = Modifier.weight(1f),

                        icono = "hoja",

                        valor = formatoDashboard(kilogramos),

                        unidad = "kg",

                        titulo = "Material recuperado",

                        color = VerdePrincipal,

                        fondo = VerdeClaro
                    )

                    TarjetaImpacto(

                        modifier = Modifier.weight(1f),

                        icono = "reciclar",

                        valor = formatoDashboard(reciclajes),

                        unidad = "",

                        titulo = "Reciclajes realizados",

                        color = Azul,

                        fondo = Color(0xFFE8F0FA)
                    )
                }
            }

            // Accesos principales.

            Column {

                TituloSeccion(

                    titulo = "Explora EcoRecicla",

                    subtitulo =
                        "Todo lo que necesitas en un solo lugar."
                )

                Spacer(Modifier.height(14.dp))

                AccionPrincipal(

                    icono = "reciclar",

                    titulo = "Registrar reciclaje",

                    descripcion =
                        "Convierte tus residuos en puntos.",

                    color = VerdePrincipal,

                    fondo = VerdeClaro,

                    onClick = onRecyclingClick
                )

                Spacer(Modifier.height(10.dp))

                AccionPrincipal(

                    icono = "premios",

                    titulo = "Catálogo de recompensas",

                    descripcion =
                        "Descubre los premios disponibles.",

                    color = Naranja,

                    fondo = Color(0xFFFFF1DB),

                    onClick = onRewardsClick
                )

                Spacer(Modifier.height(10.dp))

                AccionPrincipal(

                    icono = "historial",

                    titulo = "Mis movimientos",

                    descripcion =
                        "Consulta tus reciclajes y canjes.",

                    color = Azul,

                    fondo = Color(0xFFE8F0FA),

                    onClick = onHistoryClick
                )
            }

            // Acceso directo a Mis premios.
            // Permite consultar los códigos ECO# almacenados.

            TarjetaReclamacion(

                onClick = onMyRewardsClick
            )

            // Mensaje ambiental.

            TarjetaComunidad()

            // Información de la aplicación.

            Column(

                modifier = Modifier.fillMaxWidth(),

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {

                Text(

                    text = "EcoRecicla Mobile",

                    fontSize = 12.sp,

                    fontWeight = FontWeight.Bold,

                    color = VerdePrincipal
                )

                Spacer(Modifier.height(4.dp))

                Text(

                    text = "Recicla · Gana · Transforma",

                    fontSize = 10.sp,

                    color = TextoSecundario
                )

                Spacer(Modifier.height(5.dp))

                Text(

                    text = "Versión 1.0",

                    fontSize = 10.sp,

                    color = TextoSecundario
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ======================================================
// ENCABEZADO PRINCIPAL
// ======================================================

@Composable
private fun EncabezadoDashboard(

    nombreUsuario: String,

    onProfileClick: () -> Unit

) {

    val iniciales = nombreUsuario
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") {
            it.take(1).uppercase()
        }
        .ifBlank { "ER" }

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically

    ) {

        // Logotipo ambiental.

        Box(

            modifier = Modifier
                .size(48.dp)
                .background(

                    brush = Brush.linearGradient(

                        listOf(
                            VerdeProfundo,
                            VerdeMedio
                        )
                    ),

                    shape = RoundedCornerShape(16.dp)
                ),

            contentAlignment = Alignment.Center

        ) {

            EcoDashboardIcon(

                tipo = "reciclar",

                color = Blanco,

                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.width(11.dp))

        Column(

            modifier = Modifier.weight(1f)

        ) {

            Text(

                text = "EcoRecicla",

                fontSize = 23.sp,

                fontWeight = FontWeight.ExtraBold,

                color = VerdePrincipal
            )

            Text(

                text = "RECICLA, GANA Y TRANSFORMA",

                fontSize = 8.sp,

                letterSpacing = 0.6.sp,

                fontWeight = FontWeight.Medium,

                color = TextoSecundario
            )
        }

        // Acceso al perfil independiente.

        Surface(

            onClick = onProfileClick,

            shape = CircleShape,

            color = VerdeClaro,

            modifier = Modifier.size(46.dp)

        ) {

            Box(

                contentAlignment = Alignment.Center

            ) {

                Text(

                    text = iniciales,

                    fontSize = 15.sp,

                    fontWeight = FontWeight.ExtraBold,

                    color = VerdePrincipal
                )
            }
        }
    }
}

// ======================================================
// TARJETA PRINCIPAL DE SALDO
// ======================================================

@Composable
private fun TarjetaSaldo(

    puntos: Int,

    onRewardsClick: () -> Unit,

    onPointsClick: () -> Unit

) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(29.dp))
            .background(

                Brush.linearGradient(

                    listOf(
                        VerdeProfundo,
                        VerdePrincipal,
                        VerdeMedio
                    )
                )
            )

    ) {

        // Decoración vectorial.

        Canvas(

            modifier = Modifier.matchParentSize()

        ) {

            drawCircle(

                color = Blanco.copy(alpha = 0.05f),

                radius = size.width * 0.43f,

                center = Offset(
                    size.width * 0.95f,
                    size.height * 0.12f
                )
            )

            drawCircle(

                color = Blanco.copy(alpha = 0.05f),

                radius = size.width * 0.31f,

                center = Offset(
                    size.width * 0.95f,
                    size.height * 0.12f
                )
            )

            drawCircle(

                color = Blanco.copy(alpha = 0.04f),

                radius = size.width * 0.26f,

                center = Offset(
                    size.width * 0.08f,
                    size.height
                )
            )
        }

        EcoDashboardIcon(

            tipo = "hoja",

            color = Blanco.copy(alpha = 0.10f),

            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 15.dp)
                .size(130.dp)
        )

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(23.dp)

        ) {

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Box(

                    modifier = Modifier
                        .size(40.dp)
                        .background(

                            Blanco.copy(alpha = 0.13f),

                            RoundedCornerShape(13.dp)
                        ),

                    contentAlignment = Alignment.Center

                ) {

                    EcoDashboardIcon(

                        tipo = "billetera",

                        color = Blanco,

                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(

                    modifier = Modifier.weight(1f)

                ) {

                    Text(

                        text = "Mi billetera ecológica",

                        fontSize = 13.sp,

                        fontWeight = FontWeight.Bold,

                        color = Blanco
                    )

                    Text(

                        text = "EcoPuntos disponibles",

                        fontSize = 10.sp,

                        color = Blanco.copy(alpha = 0.72f)
                    )
                }

                Surface(

                    onClick = onPointsClick,

                    color = Blanco.copy(alpha = 0.12f),

                    shape = RoundedCornerShape(11.dp)

                ) {

                    Box(

                        modifier = Modifier.size(36.dp),

                        contentAlignment = Alignment.Center

                    ) {

                        EcoDashboardIcon(

                            tipo = "historial",

                            color = Blanco,

                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(25.dp))

            Text(

                text = "SALDO DISPONIBLE",

                fontSize = 10.sp,

                letterSpacing = 1.3.sp,

                fontWeight = FontWeight.Medium,

                color = Blanco.copy(alpha = 0.75f)
            )

            Spacer(Modifier.height(5.dp))

            Row(

                verticalAlignment =
                    Alignment.Bottom

            ) {

                Text(

                    text = formatoDashboard(puntos),

                    fontSize = 42.sp,

                    fontWeight = FontWeight.ExtraBold,

                    color = Blanco,

                    modifier = Modifier.weight(
                        weight = 1f,
                        fill = false
                    )
                )

                Spacer(Modifier.width(8.dp))

                Text(

                    text = "PTS",

                    modifier = Modifier.padding(bottom = 8.dp),

                    fontSize = 15.sp,

                    fontWeight = FontWeight.Bold,

                    color = Dorado
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(

                text = "Tus buenas acciones tienen recompensa.",

                fontSize = 11.sp,

                color = Blanco.copy(alpha = 0.78f)
            )

            Spacer(Modifier.height(22.dp))

            Button(

                onClick = onRewardsClick,

                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 49.dp),

                shape = RoundedCornerShape(14.dp),

                colors = ButtonDefaults.buttonColors(

                    containerColor = Blanco,

                    contentColor = VerdePrincipal
                )

            ) {

                EcoDashboardIcon(

                    tipo = "premios",

                    color = VerdePrincipal,

                    modifier = Modifier.size(19.dp)
                )

                Spacer(Modifier.width(9.dp))

                Text(

                    text = "Explorar recompensas",

                    fontSize = 13.sp,

                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

// ======================================================
// TÍTULOS DE SECCIÓN
// ======================================================

@Composable
private fun TituloSeccion(

    titulo: String,

    subtitulo: String

) {

    Column {

        Text(

            text = titulo,

            fontSize = 21.sp,

            fontWeight = FontWeight.ExtraBold,

            color = TextoOscuro
        )

        Spacer(Modifier.height(5.dp))

        Text(

            text = subtitulo,

            fontSize = 12.sp,

            color = TextoSecundario
        )
    }
}

// ======================================================
// TARJETAS DE IMPACTO AMBIENTAL
// ======================================================

@Composable
private fun TarjetaImpacto(

    modifier: Modifier = Modifier,

    icono: String,

    valor: String,

    unidad: String,

    titulo: String,

    color: Color,

    fondo: Color

) {

    Surface(

        modifier = modifier,

        color = Blanco,

        shape = RoundedCornerShape(23.dp),

        shadowElevation = 1.dp

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {

            Box(

                modifier = Modifier
                    .size(45.dp)
                    .background(

                        fondo,

                        RoundedCornerShape(14.dp)
                    ),

                contentAlignment = Alignment.Center

            ) {

                EcoDashboardIcon(

                    tipo = icono,

                    color = color,

                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(

                verticalAlignment = Alignment.Bottom

            ) {

                Text(

                    text = valor,

                    fontSize = 27.sp,

                    fontWeight = FontWeight.ExtraBold,

                    color = TextoOscuro,

                    maxLines = 1,

                    overflow = TextOverflow.Ellipsis,

                    modifier = Modifier.weight(
                        weight = 1f,
                        fill = false
                    )
                )

                if (unidad.isNotBlank()) {

                    Spacer(Modifier.width(4.dp))

                    Text(

                        text = unidad,

                        fontSize = 12.sp,

                        fontWeight = FontWeight.Bold,

                        color = color,

                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(

                text = titulo,

                fontSize = 11.sp,

                lineHeight = 16.sp,

                color = TextoSecundario
            )
        }
    }
}

// ======================================================
// TARJETAS DE ACCIONES PRINCIPALES
// ======================================================

@Composable
private fun AccionPrincipal(

    icono: String,

    titulo: String,

    descripcion: String,

    color: Color,

    fondo: Color,

    onClick: () -> Unit

) {

    Surface(

        onClick = onClick,

        modifier = Modifier.fillMaxWidth(),

        color = Blanco,

        shape = RoundedCornerShape(21.dp),

        shadowElevation = 1.dp

    ) {

        Row(

            modifier = Modifier.padding(15.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Box(

                modifier = Modifier
                    .size(53.dp)
                    .background(

                        fondo,

                        RoundedCornerShape(16.dp)
                    ),

                contentAlignment = Alignment.Center

            ) {

                EcoDashboardIcon(

                    tipo = icono,

                    color = color,

                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(13.dp))

            Column(

                modifier = Modifier.weight(1f)

            ) {

                Text(

                    text = titulo,

                    fontSize = 14.sp,

                    fontWeight = FontWeight.ExtraBold,

                    color = TextoOscuro
                )

                Spacer(Modifier.height(5.dp))

                Text(

                    text = descripcion,

                    fontSize = 11.sp,

                    lineHeight = 16.sp,

                    color = TextoSecundario
                )
            }

            Spacer(Modifier.width(8.dp))

            EcoDashboardIcon(

                tipo = "flecha",

                color = color,

                modifier = Modifier.size(19.dp)
            )
        }
    }
}

// ======================================================
// TARJETA DE RECLAMACIÓN FÍSICA
// ======================================================

@Composable
private fun TarjetaReclamacion(

    onClick: () -> Unit

) {

    Surface(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(25.dp),

        color = Color(0xFFFFF1DB)

    ) {

        Column(

            modifier = Modifier.padding(19.dp)

        ) {

            Row(

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Box(

                    modifier = Modifier
                        .size(52.dp)
                        .background(

                            Color(0xFFFFE4B4),

                            RoundedCornerShape(16.dp)
                        ),

                    contentAlignment = Alignment.Center

                ) {

                    EcoDashboardIcon(

                        tipo = "ticket",

                        color = Naranja,

                        modifier = Modifier.size(29.dp)
                    )
                }

                Spacer(Modifier.width(13.dp))

                Column(

                    modifier = Modifier.weight(1f)

                ) {

                    Text(

                        text = "PUNTO DE RECLAMACIÓN",

                        fontSize = 9.sp,

                        letterSpacing = 0.8.sp,

                        fontWeight = FontWeight.Bold,

                        color = Naranja
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(

                        text = "Reclama tus premios",

                        fontSize = 18.sp,

                        fontWeight = FontWeight.ExtraBold,

                        color = Color(0xFF754B13)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(

                text = "Consulta tus recompensas canjeadas y presenta tu código ECO# en el punto físico de la papelería de la universidad.",

                fontSize = 12.sp,

                lineHeight = 19.sp,

                color = Color(0xFF806035)
            )

            Spacer(Modifier.height(15.dp))

            Surface(

                modifier = Modifier.fillMaxWidth(),

                color = Blanco.copy(alpha = 0.65f),

                shape = RoundedCornerShape(13.dp)

            ) {

                Row(

                    modifier = Modifier.padding(12.dp),

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    EcoDashboardIcon(

                        tipo = "ticket",

                        color = Naranja,

                        modifier = Modifier.size(21.dp)
                    )

                    Spacer(Modifier.width(10.dp))

                    Column {

                        Text(

                            text = "Tu código de reclamación",

                            fontSize = 11.sp,

                            fontWeight = FontWeight.Bold,

                            color = Naranja
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(

                            text = "Disponible en Mis premios",

                            fontSize = 11.sp,

                            color = Color(0xFF806035)
                        )
                    }
                }
            }

            Spacer(Modifier.height(15.dp))

            Button(

                onClick = onClick,

                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),

                shape = RoundedCornerShape(14.dp),

                colors = ButtonDefaults.buttonColors(

                    containerColor = Color(0xFF99651D),

                    contentColor = Blanco
                )

            ) {

                Text(

                    text = "Consultar mis premios",

                    fontSize = 13.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.width(8.dp))

                EcoDashboardIcon(

                    tipo = "flecha",

                    color = Blanco,

                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ======================================================
// TARJETA DE COMUNIDAD AMBIENTAL
// ======================================================

@Composable
private fun TarjetaComunidad() {

    Surface(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        color = VerdeClaro

    ) {

        Row(

            modifier = Modifier.padding(19.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Box(

                modifier = Modifier
                    .size(63.dp)
                    .background(

                        Blanco,

                        CircleShape
                    ),

                contentAlignment = Alignment.Center

            ) {

                EcoDashboardIcon(

                    tipo = "hoja",

                    color = VerdePrincipal,

                    modifier = Modifier.size(39.dp)
                )
            }

            Spacer(Modifier.width(15.dp))

            Column(

                modifier = Modifier.weight(1f)

            ) {

                Text(

                    text = "Pequeñas acciones, grandes cambios.",

                    fontSize = 15.sp,

                    fontWeight = FontWeight.ExtraBold,

                    color = VerdeProfundo
                )

                Spacer(Modifier.height(7.dp))

                Text(

                    text = "Juntos construimos una comunidad universitaria más limpia y sostenible.",

                    fontSize = 11.sp,

                    lineHeight = 18.sp,

                    color = VerdePrincipal
                )
            }
        }
    }
}

// ======================================================
// SISTEMA DE ICONOS VECTORIALES
// No requiere librerías adicionales.
// ======================================================

@Composable
private fun EcoDashboardIcon(

    tipo: String,

    color: Color,

    modifier: Modifier = Modifier

) {

    Canvas(modifier = modifier) {

        val w = size.width

        val h = size.height

        val grosor = w * 0.075f

        // Dibujar una línea proporcional.

        fun linea(

            x1: Float,

            y1: Float,

            x2: Float,

            y2: Float

        ) {

            drawLine(

                color = color,

                start = Offset(
                    w * x1,
                    h * y1
                ),

                end = Offset(
                    w * x2,
                    h * y2
                ),

                strokeWidth = grosor,

                cap = StrokeCap.Round
            )
        }

        // Dibujar el contorno de una figura.

        fun contorno(path: Path) {

            drawPath(

                path = path,

                color = color,

                style = Stroke(

                    width = grosor,

                    cap = StrokeCap.Round
                )
            )
        }

        when (tipo) {

            // ==========================================
            // ICONO DE INICIO
            // ==========================================

            "inicio" -> {

                val casa = Path().apply {

                    moveTo(w * .10f, h * .45f)

                    lineTo(w * .50f, h * .12f)

                    lineTo(w * .90f, h * .45f)

                    lineTo(w * .80f, h * .45f)

                    lineTo(w * .80f, h * .87f)

                    lineTo(w * .20f, h * .87f)

                    lineTo(w * .20f, h * .45f)

                    close()
                }

                contorno(casa)

                linea(.42f, .87f, .42f, .60f)

                linea(.42f, .60f, .58f, .60f)

                linea(.58f, .60f, .58f, .87f)
            }

            // ==========================================
            // ICONO DE RECICLAJE
            // ==========================================

            "reciclar" -> {

                linea(.28f, .36f, .42f, .13f)

                linea(.42f, .13f, .58f, .13f)

                linea(.58f, .13f, .72f, .38f)

                linea(.51f, .34f, .72f, .38f)

                linea(.72f, .38f, .79f, .18f)

                linea(.78f, .48f, .91f, .70f)

                linea(.91f, .70f, .81f, .85f)

                linea(.81f, .85f, .52f, .85f)

                linea(.65f, .70f, .52f, .85f)

                linea(.52f, .85f, .65f, .97f)

                linea(.40f, .85f, .16f, .85f)

                linea(.16f, .85f, .08f, .70f)

                linea(.08f, .70f, .24f, .45f)

                linea(.07f, .50f, .24f, .45f)

                linea(.24f, .45f, .30f, .64f)
            }

            // ==========================================
            // ICONO DE HOJA
            // ==========================================

            "hoja" -> {

                val hoja = Path().apply {

                    moveTo(
                        w * .20f,
                        h * .80f
                    )

                    cubicTo(

                        w * .05f,
                        h * .20f,

                        w * .48f,
                        h * .10f,

                        w * .88f,
                        h * .12f
                    )

                    cubicTo(

                        w * .95f,
                        h * .68f,

                        w * .65f,
                        h * .93f,

                        w * .20f,
                        h * .80f
                    )

                    close()
                }

                contorno(hoja)

                linea(.10f, .93f, .72f, .30f)

                linea(.38f, .65f, .30f, .38f)

                linea(.52f, .50f, .76f, .52f)
            }

            // ==========================================
            // ICONO DE PREMIOS
            // ==========================================

            "premios" -> {

                drawRect(

                    color = color,

                    topLeft = Offset(
                        w * .16f,
                        h * .43f
                    ),

                    size = Size(
                        w * .68f,
                        h * .46f
                    ),

                    style = Stroke(grosor)
                )

                drawRect(

                    color = color,

                    topLeft = Offset(
                        w * .08f,
                        h * .29f
                    ),

                    size = Size(
                        w * .84f,
                        h * .14f
                    ),

                    style = Stroke(grosor)
                )

                linea(.50f, .29f, .50f, .89f)

                val lazo = Path().apply {

                    moveTo(
                        w * .50f,
                        h * .29f
                    )

                    cubicTo(

                        w * .05f,
                        h * .20f,

                        w * .30f,
                        h * -.10f,

                        w * .50f,
                        h * .29f
                    )

                    cubicTo(

                        w * .70f,
                        h * -.10f,

                        w * .95f,
                        h * .20f,

                        w * .50f,
                        h * .29f
                    )
                }

                contorno(lazo)
            }

            // ==========================================
            // ICONO DE HISTORIAL
            // ==========================================

            "historial" -> {

                drawCircle(

                    color = color,

                    radius = w * .36f,

                    center = Offset(
                        w * .52f,
                        h * .52f
                    ),

                    style = Stroke(grosor)
                )

                linea(.52f, .29f, .52f, .53f)

                linea(.52f, .53f, .68f, .64f)

                linea(.08f, .12f, .08f, .36f)

                linea(.08f, .36f, .31f, .36f)
            }

            // ==========================================
            // ICONO DE PERFIL
            // ==========================================

            "perfil" -> {

                drawCircle(

                    color = color,

                    radius = w * .19f,

                    center = Offset(
                        w * .50f,
                        h * .28f
                    ),

                    style = Stroke(grosor)
                )

                val cuerpo = Path().apply {

                    moveTo(
                        w * .15f,
                        h * .90f
                    )

                    cubicTo(

                        w * .15f,
                        h * .48f,

                        w * .85f,
                        h * .48f,

                        w * .85f,
                        h * .90f
                    )

                    close()
                }

                contorno(cuerpo)
            }

            // ==========================================
            // ICONO DE BILLETERA
            // ==========================================

            "billetera" -> {

                drawRoundRect(

                    color = color,

                    topLeft = Offset(
                        w * .08f,
                        h * .24f
                    ),

                    size = Size(
                        w * .80f,
                        h * .61f
                    ),

                    cornerRadius = CornerRadius(
                        w * .12f
                    ),

                    style = Stroke(grosor)
                )

                drawRoundRect(

                    color = color,

                    topLeft = Offset(
                        w * .60f,
                        h * .43f
                    ),

                    size = Size(
                        w * .33f,
                        h * .24f
                    ),

                    cornerRadius = CornerRadius(
                        w * .06f
                    ),

                    style = Stroke(grosor)
                )

                drawCircle(

                    color = color,

                    radius = w * .035f,

                    center = Offset(
                        w * .72f,
                        h * .55f
                    )
                )

                linea(.15f, .24f, .70f, .10f)
            }

            // ==========================================
            // ICONO DE COMPROBANTE ECO#
            // ==========================================

            "ticket" -> {

                val ticket = Path().apply {

                    moveTo(w * .10f, h * .22f)

                    lineTo(w * .90f, h * .22f)

                    lineTo(w * .90f, h * .40f)

                    quadraticBezierTo(

                        w * .65f,
                        h * .50f,

                        w * .90f,
                        h * .60f
                    )

                    lineTo(w * .90f, h * .78f)

                    lineTo(w * .10f, h * .78f)

                    lineTo(w * .10f, h * .60f)

                    quadraticBezierTo(

                        w * .35f,
                        h * .50f,

                        w * .10f,
                        h * .40f
                    )

                    close()
                }

                contorno(ticket)

                linea(.60f, .30f, .60f, .39f)

                linea(.60f, .46f, .60f, .54f)

                linea(.60f, .61f, .60f, .70f)
            }

            // ==========================================
            // FLECHA DE NAVEGACIÓN
            // ==========================================

            "flecha" -> {

                linea(.35f, .20f, .65f, .50f)

                linea(.65f, .50f, .35f, .80f)
            }
        }
    }
}

// ======================================================
// CONTENEDOR MVVM
// PATRÓN OBSERVER MEDIANTE STATEFLOW
// ======================================================

@Composable
fun DashboardRoute(

    dashboardViewModel: DashboardViewModel = viewModel(),

    onRecyclingClick: () -> Unit = {},

    onRewardsClick: () -> Unit = {},

    onPointsClick: () -> Unit = {},

    onHistoryClick: () -> Unit = {},

    onProfileClick: () -> Unit = {},

    onMyRewardsClick: () -> Unit = onRewardsClick

) {

    // Observar el estado del ViewModel.

    val uiState by dashboardViewModel
        .uiState.collectAsState()

    // Enviar datos y eventos a la interfaz.

    DashboardScreen(

        nombreUsuario = uiState.nombreUsuario,

        puntos = uiState.puntos,

        kilogramos = uiState.kilogramos,

        reciclajes = uiState.reciclajes,

        onRecyclingClick = onRecyclingClick,

        onRewardsClick = onRewardsClick,

        onPointsClick = onPointsClick,

        onHistoryClick = onHistoryClick,

        onProfileClick = onProfileClick,

        onMyRewardsClick = onMyRewardsClick
    )
}

// ======================================================
// VISTA PREVIA EN ANDROID STUDIO
// Los valores son únicamente ilustrativos.
// ======================================================

@Preview(

    name = "EcoRecicla Dashboard Premium",

    showBackground = true,

    showSystemUi = true
)

@Composable
fun DashboardScreenPreview() {

    EcoReciclaMobileTheme {

        DashboardScreen(

            nombreUsuario = "Usuario EcoRecicla",

            puntos = 4550,

            kilogramos = 45.5,

            reciclajes = 18
        )
    }
}

// ======================================================
// FIN DEL ARCHIVO
// ECORECICLA MOBILE
// ======================================================