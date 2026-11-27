package com.example.ecorecicla.mobile.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.animation.core.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecorecicla.mobile.viewmodel.RecyclingViewModel
import com.example.ecorecicla.mobile.viewmodel.ResultadoReciclaje
import com.example.ecorecicla.mobile.ui.components.*
import java.text.NumberFormat
import java.util.Locale

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: RecyclingScreen.kt
// RESPONSABILIDAD: Registro de reciclaje.
// ARQUITECTURA: MVVM.
// PATRONES: Observer, Repository y componentes reutilizables.
// ======================================================

// Identidad visual.

private val VerdeReciclaje = Color(0xFF166534)
private val VerdeProfundo = Color(0xFF103D29)
private val VerdeMedio = Color(0xFF23834D)
private val VerdeSuave = Color(0xFFE4F2E8)
private val FondoReciclaje = Color(0xFFF4F7F5)
private val TextoReciclaje = Color(0xFF172B21)
private val GrisReciclaje = Color(0xFF718078)
private val BordeReciclaje = Color(0xFFE0E8E2)
private val DoradoReciclaje = Color(0xFFEBC76C)
private val RojoReciclaje = Color(0xFFB91C1C)

// Formato numérico colombiano.

private fun formatearNumero(numero: Number): String {

    return NumberFormat
        .getNumberInstance(Locale("es", "CO"))
        .apply {
            maximumFractionDigits = 3
        }
        .format(numero)
}

// ======================================================
// PANTALLA PRINCIPAL
// ======================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecyclingScreen(
    usuarioId: Long,
    onBackClick: () -> Unit = {},
    recyclingViewModel: RecyclingViewModel = viewModel()
) {

    // Observer: observar el estado del ViewModel.

    val estado by recyclingViewModel.uiState.collectAsState()

    var mostrarError by remember {
        mutableStateOf(false)
    }

    val formularioActivo =
        !estado.isLoading && !estado.registrado

    // Evitar abandonar la pantalla durante una operación.

    BackHandler(enabled = !formularioActivo) {
        // La operación debe finalizar primero.
    }

    // Control de errores provenientes del ViewModel.

    LaunchedEffect(
        estado.mensaje,
        estado.registrado,
        estado.isLoading
    ) {

        mostrarError =
            estado.mensaje != null &&
                    !estado.registrado &&
                    !estado.isLoading
    }

    Scaffold(
        containerColor = FondoReciclaje,

        topBar = {

            TopAppBar(
                title = {

                    Column {

                        Text(
                            text = "Registrar reciclaje",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextoReciclaje
                        )

                        Text(
                            text = "TRANSFORMA TUS RESIDUOS",
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            color = GrisReciclaje
                        )
                    }
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick,
                        enabled = formularioActivo
                    ) {

                        IconoReciclaje(
                            tipo = "atras",
                            color = VerdeReciclaje,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {

            // Encabezado ambiental.

            EncabezadoReciclaje()

            // Indicador visual del formulario.

            IndicadorPasosReciclaje(
                materialSeleccionado = estado.material != null,

                pesoValido =
                    (estado.peso
                        .replace(",", ".")
                        .toDoubleOrNull() ?: 0.0) > 0 &&
                            estado.errorPeso == null
            )

            // ==========================================
            // PASO 1: SELECCIÓN DE MATERIAL
            // ==========================================

            Column {

                TituloPasoReciclaje(
                    numero = "01",
                    titulo = "Selecciona el material",
                    descripcion = "Elige el tipo de residuo que vas a reciclar."
                )

                Spacer(Modifier.height(16.dp))

                // Los materiales se obtienen de SQLite.
                // No existen tarifas inventadas en la interfaz.

                if (estado.materiales.isEmpty()) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(22.dp),

                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            IconoReciclaje(
                                tipo = "reciclar",
                                color = VerdeReciclaje,
                                modifier = Modifier.size(42.dp)
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = if (estado.isLoading) {
                                    "Consultando materiales..."
                                } else {
                                    "No hay materiales disponibles"
                                },

                                fontWeight = FontWeight.Bold,
                                color = TextoReciclaje,
                                textAlign = TextAlign.Center
                            )

                            if (!estado.isLoading) {

                                Spacer(Modifier.height(8.dp))

                                TextButton(
                                    onClick = {
                                        mostrarError = false
                                        recyclingViewModel
                                            .cargarMateriales()
                                    }
                                ) {

                                    Text(
                                        "Reintentar",
                                        color = VerdeReciclaje
                                    )
                                }
                            }
                        }
                    }

                } else {

                    // Distribución dinámica en dos columnas.
                    // Admite cualquier cantidad de materiales.

                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        estado.materiales
                            .chunked(2)
                            .forEach { fila ->

                                Row(
                                    modifier = Modifier.fillMaxWidth(),

                                    horizontalArrangement =
                                        Arrangement.spacedBy(12.dp)
                                ) {

                                    fila.forEach { material ->

                                        TarjetaMaterialReciclaje(
                                            modifier = Modifier.weight(1f),

                                            nombre = material.nombre,

                                            puntosPorKg =
                                                material.puntosPorKg,

                                            seleccionado =
                                                estado.material == material,

                                            habilitado = formularioActivo,

                                            onClick = {

                                                mostrarError = false

                                                recyclingViewModel
                                                    .seleccionarMaterial(
                                                        material
                                                    )
                                            }
                                        )
                                    }

                                    if (fila.size == 1) {

                                        Spacer(
                                            Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                    }
                }

                estado.errorMaterial?.let { error ->

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = error,
                        color = RojoReciclaje,
                        fontSize = 12.sp
                    )
                }
            }

            // ==========================================
            // PASO 2: REGISTRO DEL PESO
            // ==========================================

            Column {

                TituloPasoReciclaje(
                    numero = "02",
                    titulo = "Registra el peso",
                    descripcion = "Ingresa el peso real de tu material."
                )

                Spacer(Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(24.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(
                                        VerdeSuave,
                                        RoundedCornerShape(14.dp)
                                    ),

                                contentAlignment = Alignment.Center
                            ) {

                                IconoReciclaje(
                                    tipo = "balanza",
                                    color = VerdeReciclaje,
                                    modifier = Modifier.size(27.dp)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {

                                Text(
                                    text = "Peso del material",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoReciclaje
                                )

                                Text(
                                    text = "Unidad de medida: kilogramos",
                                    fontSize = 11.sp,
                                    color = GrisReciclaje
                                )
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        OutlinedTextField(
                            value = estado.peso,

                            onValueChange =
                                recyclingViewModel::onPesoChange,

                            enabled = formularioActivo,

                            placeholder = {

                                Text(
                                    text = "0.00",
                                    fontSize = 24.sp
                                )
                            },

                            suffix = {

                                Text(
                                    text = "kg",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = VerdeReciclaje
                                )
                            },

                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            ),

                            singleLine = true,

                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),

                            isError = estado.errorPeso != null,

                            shape = RoundedCornerShape(16.dp),

                            modifier = Modifier.fillMaxWidth(),

                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerdeReciclaje,
                                unfocusedBorderColor = BordeReciclaje,
                                cursorColor = VerdeReciclaje,
                                focusedContainerColor = FondoReciclaje,
                                unfocusedContainerColor = FondoReciclaje
                            )
                        )

                        estado.errorPeso?.let { error ->

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = error,
                                color = RojoReciclaje,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Selección rápida",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = GrisReciclaje
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.spacedBy(6.dp)
                        ) {

                            listOf(
                                "0.5",
                                "1",
                                "2",
                                "5"
                            ).forEach { cantidad ->

                                val seleccionado =
                                    estado.peso
                                        .replace(",", ".")
                                        .toDoubleOrNull() ==
                                            cantidad.toDoubleOrNull()

                                Surface(
                                    onClick = {

                                        recyclingViewModel
                                            .onPesoChange(cantidad)
                                    },

                                    enabled = formularioActivo,

                                    modifier = Modifier.weight(1f),

                                    shape = RoundedCornerShape(12.dp),

                                    color = if (seleccionado) {
                                        VerdeReciclaje
                                    } else {
                                        VerdeSuave
                                    }
                                ) {

                                    Box(
                                        modifier = Modifier.padding(
                                            vertical = 12.dp
                                        ),

                                        contentAlignment =
                                            Alignment.Center
                                    ) {

                                        Text(
                                            text = "$cantidad kg",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,

                                            color = if (seleccionado) {
                                                Color.White
                                            } else {
                                                VerdeReciclaje
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // PASO 3: RECOMPENSA ESTIMADA
            // ==========================================

            Column {

                TituloPasoReciclaje(
                    numero = "03",
                    titulo = "Tu recompensa estimada",
                    descripcion = "Descubre cuánto vale tu esfuerzo."
                )

                Spacer(Modifier.height(14.dp))

                TarjetaPuntosReciclaje(
                    puntos = estado.puntosEstimados,

                    material =
                        estado.material?.nombre,

                    peso = estado.peso,

                    tarifa =
                        estado.material?.puntosPorKg
                )
            }

            // ==========================================
            // REGISTRO REAL EN SQLITE
            // ==========================================

            Button(
                onClick = {

                    mostrarError = false

                    recyclingViewModel.registrar(usuarioId)
                },

                enabled =
                    formularioActivo &&
                            estado.materiales.isNotEmpty(),

                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),

                shape = RoundedCornerShape(17.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeReciclaje,
                    contentColor = Color.White
                )
            ) {

                IconoReciclaje(
                    tipo = "check",
                    color = Color.White,
                    modifier = Modifier.size(23.dp)
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    text = when {

                        estado.isLoading ->
                            "Procesando..."

                        estado.registrado ->
                            "Reciclaje registrado"

                        else ->
                            "Registrar reciclaje"
                    },

                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Información complementaria.

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.Center,

                verticalAlignment = Alignment.CenterVertically
            ) {

                IconoReciclaje(
                    tipo = "escudo",
                    color = GrisReciclaje,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(Modifier.width(7.dp))

                Text(
                    text = "Tu operación quedará guardada en tu historial.",
                    fontSize = 10.sp,
                    color = GrisReciclaje
                )
            }

            TarjetaMensajeAmbiental()

            Text(
                text = "EcoRecicla Mobile · Versión 1.0",

                modifier = Modifier.fillMaxWidth(),

                textAlign = TextAlign.Center,

                fontSize = 10.sp,

                color = GrisReciclaje
            )

            Spacer(Modifier.height(8.dp))
        }
    }

    // ==================================================
    // DIÁLOGOS PERSONALIZADOS
    // ==================================================

    if (estado.isLoading) {

        EcoCargandoDialog(
            titulo = "Un momento...",
            mensaje = "Estamos procesando tu información."
        )

    } else if (
        estado.registrado &&
        estado.resultado != null
    ) {

        estado.resultado?.let { resultado ->

            ReciclajeExitosoDialog(
                resultado = resultado,

                onInicio = {

                    recyclingViewModel.nuevoReciclaje()

                    onBackClick()
                },

                onNuevoReciclaje = {

                    recyclingViewModel.nuevoReciclaje()
                }
            )
        }

    } else if (mostrarError) {

        EcoErrorDialog(
            titulo = "No pudimos completar la operación",

            mensaje = estado.mensaje.orEmpty(),

            onDismiss = {

                mostrarError = false
            }
        )
    }
}

// ======================================================
// ENCABEZADO PREMIUM
// ======================================================

@Composable
private fun EncabezadoReciclaje() {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(28.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            VerdeProfundo,
                            VerdeReciclaje,
                            VerdeMedio
                        )
                    )
                )
        ) {

            Canvas(
                modifier = Modifier.matchParentSize()
            ) {

                for (i in 1..4) {

                    drawCircle(
                        color = Color.White.copy(alpha = 0.06f),

                        radius = size.width * 0.12f * i,

                        center = Offset(
                            size.width * 0.95f,
                            size.height * 0.25f
                        ),

                        style = Stroke(2.dp.toPx())
                    )
                }
            }

            Row(
                modifier = Modifier.padding(22.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "UN FUTURO MÁS VERDE",
                        fontSize = 9.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB7F2C4)
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Dale una segunda vida",
                        fontSize = 26.sp,
                        lineHeight = 31.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Tus residuos pueden convertirse en grandes oportunidades.",
                        fontSize = 12.sp,
                        lineHeight = 19.sp,
                        color = Color(0xFFD4EBDD)
                    )
                }

                Spacer(Modifier.width(10.dp))

                IlustracionPlantaReciclaje(
                    modifier = Modifier.size(90.dp)
                )
            }
        }
    }
}

// ======================================================
// ILUSTRACIÓN AMBIENTAL ANIMADA
// ======================================================

@Composable
private fun IlustracionPlantaReciclaje(
    modifier: Modifier = Modifier
) {

    val transicion = rememberInfiniteTransition(
        label = "planta"
    )

    val movimiento by transicion.animateFloat(
        initialValue = -3f,

        targetValue = 3f,

        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = FastOutSlowInEasing
            ),

            repeatMode = RepeatMode.Reverse
        ),

        label = "movimientoPlanta"
    )

    Canvas(
        modifier = modifier.graphicsLayer {
            rotationZ = movimiento
        }
    ) {

        val w = size.width
        val h = size.height

        drawCircle(
            color = Color.White.copy(alpha = 0.09f),
            radius = w * 0.48f
        )

        // Tallo.

        drawLine(
            color = Color(0xFFB7F2C4),

            start = Offset(w * .48f, h * .88f),

            end = Offset(w * .48f, h * .17f),

            strokeWidth = w * .035f,

            cap = StrokeCap.Round
        )

        // Hoja izquierda.

        val izquierda = Path().apply {

            moveTo(w * .48f, h * .52f)

            cubicTo(
                w * .08f, h * .55f,
                w * .03f, h * .13f,
                w * .15f, h * .13f
            )

            cubicTo(
                w * .48f, h * .10f,
                w * .55f, h * .32f,
                w * .48f, h * .52f
            )

            close()
        }

        drawPath(
            path = izquierda,
            color = Color(0xFF86C99A)
        )

        // Hoja derecha.

        val derecha = Path().apply {

            moveTo(w * .48f, h * .75f)

            cubicTo(
                w * .40f, h * .30f,
                w * .78f, h * .27f,
                w * .91f, h * .30f
            )

            cubicTo(
                w * .96f, h * .65f,
                w * .68f, h * .80f,
                w * .48f, h * .75f
            )

            close()
        }

        drawPath(
            path = derecha,
            color = Color(0xFFC3E8CC)
        )

        drawLine(
            color = VerdeReciclaje,
            start = Offset(w * .48f, h * .52f),
            end = Offset(w * .22f, h * .23f),
            strokeWidth = w * .02f
        )

        drawLine(
            color = VerdeReciclaje,
            start = Offset(w * .48f, h * .75f),
            end = Offset(w * .78f, h * .40f),
            strokeWidth = w * .02f
        )
    }
}

// ======================================================
// INDICADOR DE PROGRESO
// ======================================================

@Composable
private fun IndicadorPasosReciclaje(
    materialSeleccionado: Boolean,
    pesoValido: Boolean
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        color = Color.White
    ) {

        Row(
            modifier = Modifier.padding(16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            PasoReciclaje(
                modifier = Modifier.weight(1f),
                numero = "1",
                titulo = "Material",
                activo = true,
                completado = materialSeleccionado
            )

            HorizontalDivider(
                modifier = Modifier.weight(.35f),

                color = if (materialSeleccionado) {
                    VerdeReciclaje
                } else {
                    BordeReciclaje
                }
            )

            PasoReciclaje(
                modifier = Modifier.weight(1f),
                numero = "2",
                titulo = "Peso",
                activo = materialSeleccionado,
                completado = materialSeleccionado && pesoValido
            )

            HorizontalDivider(
                modifier = Modifier.weight(.35f),

                color = if (
                    materialSeleccionado && pesoValido
                ) {
                    VerdeReciclaje
                } else {
                    BordeReciclaje
                }
            )

            PasoReciclaje(
                modifier = Modifier.weight(1f),
                numero = "3",
                titulo = "Puntos",
                activo = materialSeleccionado && pesoValido,
                completado = false
            )
        }
    }
}

@Composable
private fun PasoReciclaje(
    modifier: Modifier = Modifier,
    numero: String,
    titulo: String,
    activo: Boolean,
    completado: Boolean
) {

    Column(
        modifier = modifier,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    when {

                        completado -> VerdeReciclaje

                        activo -> VerdeSuave

                        else -> FondoReciclaje
                    },

                    CircleShape
                ),

            contentAlignment = Alignment.Center
        ) {

            if (completado) {

                IconoReciclaje(
                    tipo = "check",
                    color = Color.White,
                    modifier = Modifier.size(18.dp)
                )

            } else {

                Text(
                    text = numero,
                    fontWeight = FontWeight.Bold,

                    color = if (activo) {
                        VerdeReciclaje
                    } else {
                        GrisReciclaje
                    }
                )
            }
        }

        Spacer(Modifier.height(7.dp))

        Text(
            text = titulo,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,

            color = if (activo) {
                VerdeReciclaje
            } else {
                GrisReciclaje
            }
        )
    }
}

// ======================================================
// TÍTULO DE CADA PASO
// ======================================================

@Composable
private fun TituloPasoReciclaje(
    numero: String,
    titulo: String,
    descripcion: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = numero,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = VerdeMedio
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = titulo,
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextoReciclaje
        )
    }

    Spacer(Modifier.height(6.dp))

    Text(
        text = descripcion,
        fontSize = 12.sp,
        color = GrisReciclaje,
        lineHeight = 18.sp
    )
}

// ======================================================
// IDENTIDAD VISUAL DE LOS MATERIALES
// ======================================================

private data class EstiloMaterialReciclaje(
    val icono: String,
    val color: Color,
    val fondo: Color
)

private fun obtenerEstiloMaterial(
    nombre: String
): EstiloMaterialReciclaje {

    val texto = nombre.lowercase()

    return when {

        texto.contains("plástico") ||
                texto.contains("plastico") ||
                texto.contains("pet") -> {

            EstiloMaterialReciclaje(
                icono = "botella",
                color = Color(0xFF2563EB),
                fondo = Color(0xFFE8F0FA)
            )
        }

        texto.contains("papel") -> {

            EstiloMaterialReciclaje(
                icono = "papel",
                color = Color(0xFF64748B),
                fondo = Color(0xFFEEF2F6)
            )
        }

        texto.contains("cartón") ||
                texto.contains("carton") -> {

            EstiloMaterialReciclaje(
                icono = "caja",
                color = Color(0xFF95601C),
                fondo = Color(0xFFFFF1DB)
            )
        }

        texto.contains("vidrio") -> {

            EstiloMaterialReciclaje(
                icono = "vidrio",
                color = VerdeReciclaje,
                fondo = VerdeSuave
            )
        }

        texto.contains("metal") ||
                texto.contains("aluminio") ||
                texto.contains("lata") -> {

            EstiloMaterialReciclaje(
                icono = "lata",
                color = Color(0xFF475569),
                fondo = Color(0xFFE2E8F0)
            )
        }

        texto.contains("orgánico") ||
                texto.contains("organico") -> {

            EstiloMaterialReciclaje(
                icono = "hoja",
                color = Color(0xFF4D7C0F),
                fondo = Color(0xFFECFCCB)
            )
        }

        else -> {

            EstiloMaterialReciclaje(
                icono = "reciclar",
                color = VerdeReciclaje,
                fondo = VerdeSuave
            )
        }
    }
}

// ======================================================
// TARJETAS SELECCIONABLES DE MATERIALES
// ======================================================

@Composable
private fun TarjetaMaterialReciclaje(
    modifier: Modifier = Modifier,
    nombre: String,
    puntosPorKg: Number,
    seleccionado: Boolean,
    habilitado: Boolean,
    onClick: () -> Unit
) {

    val estilo = obtenerEstiloMaterial(nombre)

    val escala by animateFloatAsState(
        targetValue = if (seleccionado) 1.025f else 1f,

        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),

        label = "seleccionMaterial"
    )

    Surface(
        onClick = onClick,

        enabled = habilitado,

        modifier = modifier
            .heightIn(min = 145.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            },

        shape = RoundedCornerShape(22.dp),

        color = if (seleccionado) {
            VerdeSuave
        } else {
            Color.White
        },

        border = BorderStroke(
            width = if (seleccionado) 2.dp else 1.dp,

            color = if (seleccionado) {
                VerdeReciclaje
            } else {
                BordeReciclaje
            }
        ),

        shadowElevation =
            if (seleccionado) 3.dp else 0.dp
    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            estilo.fondo,
                            RoundedCornerShape(14.dp)
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    IconoReciclaje(
                        tipo = estilo.icono,
                        color = estilo.color,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(
                            if (seleccionado) {
                                VerdeReciclaje
                            } else {
                                Color.Transparent
                            },

                            CircleShape
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    IconoReciclaje(
                        tipo = if (seleccionado) {
                            "check"
                        } else {
                            "circulo"
                        },

                        color = if (seleccionado) {
                            Color.White
                        } else {
                            Color(0xFFC7D4CA)
                        },

                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(13.dp))

            Text(
                text = nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextoReciclaje
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "${formatearNumero(puntosPorKg)} puntos/kg",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = VerdeReciclaje
            )
        }
    }
}

// ======================================================
// TARJETA DE PUNTOS ESTIMADOS
// ======================================================

@Composable
private fun TarjetaPuntosReciclaje(
    puntos: Number,
    material: String?,
    peso: String,
    tarifa: Number?
) {

    // La animación es únicamente visual.
    // El cálculo real permanece en el ViewModel.

    val puntosAnimados by animateFloatAsState(
        targetValue = puntos.toFloat(),

        animationSpec = tween(
            durationMillis = 450,
            easing = FastOutSlowInEasing
        ),

        label = "puntosEstimados"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(28.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            VerdeProfundo,
                            VerdeReciclaje,
                            VerdeMedio
                        )
                    )
                )
        ) {

            Canvas(
                modifier = Modifier.matchParentSize()
            ) {

                for (i in 1..4) {

                    drawCircle(
                        color = Color.White.copy(alpha = .06f),

                        radius = size.width * .13f * i,

                        center = Offset(
                            size.width * .95f,
                            size.height * .10f
                        ),

                        style = Stroke(2.dp.toPx())
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Color.White.copy(alpha = .12f),
                            CircleShape
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    IconoReciclaje(
                        tipo = "estrella",
                        color = DoradoReciclaje,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.height(15.dp))

                Text(
                    text = "PUNTOS QUE PUEDES GANAR",
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC9E8D2)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "+${formatearNumero(puntosAnimados.toLong())}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "Puntos EcoRecicla",
                    fontSize = 13.sp,
                    color = Color(0xFFD4EBDD)
                )

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(
                    color = Color.White.copy(alpha = .20f)
                )

                Spacer(Modifier.height(16.dp))

                if (material != null && tarifa != null) {

                    Text(
                        text = material,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = "${peso.ifBlank { "0" }} kg · " +
                                "${formatearNumero(tarifa)} pts/kg",

                        fontSize = 12.sp,

                        color = Color(0xFFD4EBDD),

                        textAlign = TextAlign.Center
                    )

                } else {

                    Text(
                        text = "Selecciona un material e ingresa su peso para conocer tu recompensa.",

                        fontSize = 12.sp,

                        lineHeight = 18.sp,

                        color = Color(0xFFD4EBDD),

                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ======================================================
// MENSAJE AMBIENTAL
// ======================================================

@Composable
private fun TarjetaMensajeAmbiental() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFFE0F0E5),
                        Color(0xFFF0F7E9)
                    )
                ),

                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconoReciclaje(
                tipo = "hoja",
                color = VerdeReciclaje,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Cada kilogramo cuenta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VerdeProfundo
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Reciclar hoy es construir un mejor mañana.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = VerdeReciclaje
                )
            }
        }
    }
}

// ======================================================
// CONFIRMACIÓN ANIMADA DE RECICLAJE EXITOSO
// ======================================================

@Composable
private fun ReciclajeExitosoDialog(
    resultado: ResultadoReciclaje,
    onInicio: () -> Unit,
    onNuevoReciclaje: () -> Unit
) {

    val escala = remember {
        Animatable(0.65f)
    }

    LaunchedEffect(Unit) {

        escala.animateTo(
            targetValue = 1f,

            animationSpec = spring(
                dampingRatio =
                    Spring.DampingRatioMediumBouncy,

                stiffness =
                    Spring.StiffnessLow
            )
        )
    }

    Dialog(
        onDismissRequest = {},

        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {

        Surface(
            modifier = Modifier
                .widthIn(max = 430.dp)
                .fillMaxWidth(.92f)
                .graphicsLayer {
                    scaleX = escala.value
                    scaleY = escala.value
                },

            shape = RoundedCornerShape(30.dp),

            color = Color.White,

            shadowElevation = 12.dp
        ) {

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                // Confirmación visual.

                Box(
                    modifier = Modifier
                        .size(94.dp)
                        .background(
                            VerdeSuave,
                            CircleShape
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                VerdeReciclaje,
                                CircleShape
                            ),

                        contentAlignment = Alignment.Center
                    ) {

                        IconoReciclaje(
                            tipo = "check",
                            color = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "¡Lo hiciste increíble!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VerdeReciclaje,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Tu reciclaje se ha registrado correctamente. Gracias por transformar el planeta.",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = GrisReciclaje,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(22.dp))

                // Puntos realmente obtenidos.

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    VerdeProfundo,
                                    VerdeReciclaje,
                                    VerdeMedio
                                )
                            ),

                            shape = RoundedCornerShape(22.dp)
                        )
                        .padding(22.dp)
                ) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        IconoReciclaje(
                            tipo = "estrella",
                            color = DoradoReciclaje,
                            modifier = Modifier.size(34.dp)
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = "PUNTOS GANADOS",
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC9E8D2)
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            text = "+${formatearNumero(resultado.puntos)}",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Text(
                            text = "Añadidos a tu billetera Eco",
                            fontSize = 12.sp,
                            color = Color(0xFFD4EBDD)
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                Text(
                    text = "Comprobante de reciclaje",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoReciclaje,

                    modifier = Modifier.align(
                        Alignment.Start
                    )
                )

                Spacer(Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(18.dp),

                    color = FondoReciclaje
                ) {

                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {

                        FilaReciclaje(
                            icono = "reciclar",
                            titulo = "Material reciclado",
                            valor = resultado.material
                        )

                        HorizontalDivider(
                            color = BordeReciclaje
                        )

                        FilaReciclaje(
                            icono = "balanza",
                            titulo = "Peso registrado",

                            valor =
                                "${formatearNumero(resultado.peso)} kg"
                        )

                        HorizontalDivider(
                            color = BordeReciclaje
                        )

                        FilaReciclaje(
                            icono = "estrella",
                            titulo = "Puntos obtenidos",

                            valor =
                                "+${formatearNumero(resultado.puntos)} pts"
                        )

                        HorizontalDivider(
                            color = BordeReciclaje
                        )

                        FilaReciclaje(
                            icono = "check",
                            titulo = "Estado",
                            valor = "Completado"
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(15.dp),

                    color = VerdeSuave
                ) {

                    Row(
                        modifier = Modifier.padding(14.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        IconoReciclaje(
                            tipo = "hoja",
                            color = VerdeReciclaje,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "Tu esfuerzo ya forma parte de tu impacto ambiental.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = VerdeReciclaje
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                Button(
                    onClick = onInicio,

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),

                    shape = RoundedCornerShape(15.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeReciclaje
                    )
                ) {

                    Text(
                        text = "Volver al inicio",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onNuevoReciclaje,

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),

                    shape = RoundedCornerShape(15.dp),

                    border = BorderStroke(
                        1.dp,
                        VerdeReciclaje
                    ),

                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = VerdeReciclaje
                    )
                ) {

                    Text(
                        text = "Registrar otro reciclaje",
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "EcoRecicla · Recicla, gana y transforma",
                    fontSize = 10.sp,
                    color = GrisReciclaje,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ======================================================
// FILA REUTILIZABLE DEL COMPROBANTE
// ======================================================

@Composable
private fun FilaReciclaje(
    icono: String,
    titulo: String,
    valor: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    VerdeSuave,
                    RoundedCornerShape(12.dp)
                ),

            contentAlignment = Alignment.Center
        ) {

            IconoReciclaje(
                tipo = icono,
                color = VerdeReciclaje,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = titulo,
                fontSize = 11.sp,
                color = GrisReciclaje
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = valor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextoReciclaje
            )
        }
    }
}

// ======================================================
// SISTEMA DE ICONOS VECTORIALES
// Dibujados con Canvas.
// No necesita librerías externas de iconos.
// ======================================================

@Composable
private fun IconoReciclaje(
    tipo: String,
    color: Color,
    modifier: Modifier = Modifier
) {

    Canvas(modifier = modifier) {

        val w = size.width
        val h = size.height
        val grosor = w * .075f

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

            // Flecha de regreso.

            "atras" -> {

                linea(.85f, .50f, .15f, .50f)
                linea(.15f, .50f, .43f, .20f)
                linea(.15f, .50f, .43f, .80f)
            }

            // Confirmación.

            "check" -> {

                linea(.18f, .52f, .40f, .75f)
                linea(.40f, .75f, .85f, .25f)
            }

            // Círculo de selección.

            "circulo" -> {

                drawCircle(
                    color = color,

                    radius = w * .39f,

                    style = Stroke(grosor)
                )
            }

            // Botella de plástico.

            "botella" -> {

                val botella = Path().apply {

                    moveTo(w * .38f, h * .08f)

                    lineTo(w * .62f, h * .08f)

                    lineTo(w * .62f, h * .27f)

                    quadraticBezierTo(
                        w * .78f, h * .34f,
                        w * .78f, h * .48f
                    )

                    lineTo(w * .78f, h * .86f)

                    quadraticBezierTo(
                        w * .78f, h * .94f,
                        w * .68f, h * .94f
                    )

                    lineTo(w * .32f, h * .94f)

                    quadraticBezierTo(
                        w * .22f, h * .94f,
                        w * .22f, h * .86f
                    )

                    lineTo(w * .22f, h * .48f)

                    quadraticBezierTo(
                        w * .22f, h * .34f,
                        w * .38f, h * .27f
                    )

                    close()
                }

                contorno(botella)

                linea(.38f, .18f, .62f, .18f)
                linea(.22f, .48f, .78f, .48f)
                linea(.22f, .70f, .78f, .70f)
            }

            // Hoja de papel.

            "papel" -> {

                val papel = Path().apply {

                    moveTo(w * .20f, h * .08f)

                    lineTo(w * .62f, h * .08f)

                    lineTo(w * .84f, h * .30f)

                    lineTo(w * .84f, h * .92f)

                    lineTo(w * .20f, h * .92f)

                    close()
                }

                contorno(papel)

                linea(.62f, .08f, .62f, .30f)
                linea(.62f, .30f, .84f, .30f)

                linea(.34f, .46f, .70f, .46f)
                linea(.34f, .61f, .70f, .61f)
                linea(.34f, .76f, .60f, .76f)
            }

            // Caja de cartón.

            "caja" -> {

                val caja = Path().apply {

                    moveTo(w * .50f, h * .08f)

                    lineTo(w * .91f, h * .30f)

                    lineTo(w * .91f, h * .72f)

                    lineTo(w * .50f, h * .94f)

                    lineTo(w * .09f, h * .72f)

                    lineTo(w * .09f, h * .30f)

                    close()
                }

                contorno(caja)

                linea(.09f, .30f, .50f, .52f)
                linea(.50f, .52f, .91f, .30f)
                linea(.50f, .52f, .50f, .94f)
                linea(.29f, .19f, .70f, .41f)
            }

            // Recipiente de vidrio.

            "vidrio" -> {

                val vidrio = Path().apply {

                    moveTo(w * .32f, h * .10f)

                    lineTo(w * .68f, h * .10f)

                    lineTo(w * .68f, h * .28f)

                    quadraticBezierTo(
                        w * .82f, h * .40f,
                        w * .82f, h * .56f
                    )

                    lineTo(w * .82f, h * .87f)

                    lineTo(w * .18f, h * .87f)

                    lineTo(w * .18f, h * .56f)

                    quadraticBezierTo(
                        w * .18f, h * .40f,
                        w * .32f, h * .28f
                    )

                    close()
                }

                contorno(vidrio)

                linea(.32f, .20f, .68f, .20f)
                linea(.30f, .52f, .30f, .74f)
                linea(.30f, .74f, .40f, .74f)
            }

            // Lata metálica.

            "lata" -> {

                drawRoundRect(
                    color = color,

                    topLeft = Offset(
                        w * .23f,
                        h * .12f
                    ),

                    size = Size(
                        w * .54f,
                        h * .78f
                    ),

                    cornerRadius = CornerRadius(
                        w * .10f
                    ),

                    style = Stroke(grosor)
                )

                linea(.23f, .26f, .77f, .26f)
                linea(.23f, .76f, .77f, .76f)

                drawOval(
                    color = color,

                    topLeft = Offset(
                        w * .43f,
                        h * .15f
                    ),

                    size = Size(
                        w * .15f,
                        h * .06f
                    ),

                    style = Stroke(grosor * .6f)
                )
            }

            // Símbolo de reciclaje.

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

            // Hoja ambiental.

            "hoja" -> {

                val hoja = Path().apply {

                    moveTo(w * .20f, h * .80f)

                    cubicTo(
                        w * .05f, h * .20f,
                        w * .48f, h * .10f,
                        w * .88f, h * .12f
                    )

                    cubicTo(
                        w * .95f, h * .68f,
                        w * .65f, h * .93f,
                        w * .20f, h * .80f
                    )

                    close()
                }

                contorno(hoja)

                linea(.10f, .93f, .72f, .30f)
                linea(.38f, .65f, .30f, .38f)
                linea(.52f, .50f, .76f, .52f)
            }

            // Balanza de peso.

            "balanza" -> {

                drawRoundRect(
                    color = color,

                    topLeft = Offset(
                        w * .10f,
                        h * .10f
                    ),

                    size = Size(
                        w * .80f,
                        h * .80f
                    ),

                    cornerRadius = CornerRadius(
                        w * .15f
                    ),

                    style = Stroke(grosor)
                )

                drawArc(
                    color = color,

                    startAngle = 180f,

                    sweepAngle = 180f,

                    useCenter = false,

                    topLeft = Offset(
                        w * .27f,
                        h * .24f
                    ),

                    size = Size(
                        w * .46f,
                        h * .42f
                    ),

                    style = Stroke(grosor)
                )

                linea(.50f, .46f, .63f, .31f)
            }

            // Estrella de puntos.

            "estrella" -> {

                val estrella = Path()

                for (i in 0 until 10) {

                    val angulo =
                        Math.PI / 5.0 * i -
                                Math.PI / 2.0

                    val radio =
                        if (i % 2 == 0) {
                            w * .45f
                        } else {
                            w * .20f
                        }

                    val x =
                        w / 2 +
                                kotlin.math.cos(angulo).toFloat() * radio

                    val y =
                        h / 2 +
                                kotlin.math.sin(angulo).toFloat() * radio

                    if (i == 0) {
                        estrella.moveTo(x, y)
                    } else {
                        estrella.lineTo(x, y)
                    }
                }

                estrella.close()

                drawPath(
                    path = estrella,
                    color = color
                )
            }

            // Escudo informativo.

            "escudo" -> {

                val escudo = Path().apply {

                    moveTo(w * .50f, h * .08f)

                    lineTo(w * .85f, h * .22f)

                    lineTo(w * .85f, h * .52f)

                    quadraticBezierTo(
                        w * .80f, h * .77f,
                        w * .50f, h * .93f
                    )

                    quadraticBezierTo(
                        w * .20f, h * .77f,
                        w * .15f, h * .52f
                    )

                    lineTo(w * .15f, h * .22f)

                    close()
                }

                contorno(escudo)

                linea(.30f, .48f, .44f, .63f)
                linea(.44f, .63f, .70f, .35f)
            }
        }
    }
}

// ======================================================
// FIN DEL ARCHIVO
// ======================================================