package com.example.ecorecicla.mobile.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecorecicla.mobile.data.repository.RecompensaLocal
import com.example.ecorecicla.mobile.viewmodel.RewardsViewModel
import com.example.ecorecicla.mobile.ui.components.EcoCargandoDialog
import com.example.ecorecicla.mobile.ui.components.EcoErrorDialog
import java.text.NumberFormat
import java.util.Locale

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: RewardsScreen.kt
// RESPONSABILIDAD: Catálogo y canje de recompensas.
// ARQUITECTURA: MVVM.
// PATRONES: Observer y Repository.
// ======================================================

private val RVerde = Color(0xFF166534)
private val RVerdeOscuro = Color(0xFF103D29)
private val RVerdeMedio = Color(0xFF23834D)
private val RVerdeClaro = Color(0xFFE4F2E8)
private val RFondo = Color(0xFFF4F7F5)
private val RTexto = Color(0xFF172B21)
private val RGris = Color(0xFF718078)
private val RDorado = Color(0xFFEBC76C)
private val RNaranja = Color(0xFF94620C)
private val RAmarillo = Color(0xFFFFF1DB)
private val RRojo = Color(0xFFB91C1C)
private val RBorde = Color(0xFFE0E8E2)

// Formato colombiano de números.

private fun formatoRecompensa(numero: Number): String {

    return NumberFormat
        .getNumberInstance(Locale("es", "CO"))
        .format(numero)
}

// Identificar visualmente cada recompensa.

private fun iconoRecompensa(nombre: String): String {

    val texto = nombre.lowercase()

    return when {

        "cuaderno" in texto -> "📒"

        "bolsa" in texto -> "🛍️"

        "botella" in texto -> "💧"

        "kit" in texto -> "🌱"

        else -> "🎁"
    }
}

// ======================================================
// PANTALLA PRINCIPAL
// ======================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(

    usuarioId: Long,

    onBackClick: () -> Unit = {},

    rewardsViewModel: RewardsViewModel = viewModel(),

    onMyRewardsClick: (() -> Unit)? = null

) {

    val estado by
    rewardsViewModel.uiState.collectAsState()

    var seleccionada by remember(usuarioId) {

        mutableStateOf<RecompensaLocal?>(null)
    }

    var mostrarMisPremios by remember(usuarioId) {

        mutableStateOf(false)
    }

    var errorCerrado by remember {

        mutableStateOf<String?>(null)
    }

    // Consultar los datos reales del usuario.

    LaunchedEffect(usuarioId) {

        rewardsViewModel.cargarCatalogo(usuarioId)
    }

    LaunchedEffect(estado.error) {

        if (estado.error == null) {

            errorCerrado = null
        }
    }

    // Evitar abandonar una operación en curso.

    BackHandler(
        enabled = estado.isLoading ||
                estado.resultadoCanje != null
    ) {
        // Esperar o cerrar el comprobante.
    }

    // Navegación alternativa para pruebas independientes.

    if (mostrarMisPremios) {

        MyRewardsScreen(

            usuarioId = usuarioId,

            onBackClick = {

                mostrarMisPremios = false

                rewardsViewModel.cargarCatalogo(usuarioId)
            },

            onExploreClick = {

                mostrarMisPremios = false

                rewardsViewModel.cargarCatalogo(usuarioId)
            }
        )

        return
    }

    val abrirMisPremios: () -> Unit = {

        if (onMyRewardsClick != null) {

            onMyRewardsClick()

        } else {

            mostrarMisPremios = true
        }
    }

    Scaffold(

        containerColor = RFondo,

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            "EcoRecicla",
                            color = RVerde,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            "RECOMPENSAS SOSTENIBLES",
                            color = RGris,
                            fontSize = 9.sp
                        )
                    }
                },

                navigationIcon = {

                    TextButton(
                        onClick = onBackClick,
                        enabled = !estado.isLoading
                    ) {

                        Text(
                            "‹",
                            fontSize = 34.sp,
                            color = RVerde
                        )
                    }
                },

                actions = {

                    TextButton(

                        enabled = !estado.isLoading,

                        onClick = {

                            rewardsViewModel
                                .cargarCatalogo(usuarioId)
                        }
                    ) {

                        Text(
                            "↻",
                            fontSize = 28.sp,
                            color = RVerde
                        )
                    }
                },

                colors = TopAppBarDefaults
                    .topAppBarColors(
                        containerColor = Color.White
                    )
            )
        }

    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding = PaddingValues(18.dp),

            verticalArrangement =
                Arrangement.spacedBy(17.dp)

        ) {

            // Encabezado.

            item {

                Column {

                    Text(
                        "TU ESFUERZO TIENE VALOR",
                        color = RVerdeMedio,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(Modifier.height(7.dp))

                    Text(
                        "Recompensas",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RVerdeOscuro
                    )

                    Spacer(Modifier.height(7.dp))

                    Text(
                        "Transforma tus puntos en premios y sigue construyendo un futuro sostenible.",
                        color = RGris,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            // Saldo disponible.

            item {

                TarjetaSaldoRewards(
                    saldo = estado.saldo,
                    cargando = estado.isLoading
                )
            }

            // Acceso directo a reclamaciones.

            item {

                AccesoMisPremios(
                    onClick = abrirMisPremios
                )
            }

            // Información del punto físico.

            item {

                Surface(

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(17.dp),

                    color = RVerdeClaro

                ) {

                    Row(

                        modifier = Modifier.padding(15.dp),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Text(
                            "📍",
                            fontSize = 25.sp
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                "Punto físico de entrega",
                                fontWeight = FontWeight.Bold,
                                color = RVerde,
                                fontSize = 13.sp
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                "Papelería de la Fundación Universitaria San Mateo.",
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = RTexto
                            )
                        }
                    }
                }
            }

            // Encabezado del catálogo.

            item {

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Text(
                        "Explora los premios",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 21.sp,
                        color = RTexto
                    )

                    Surface(

                        color = RVerdeClaro,

                        shape = RoundedCornerShape(20.dp)

                    ) {

                        Text(
                            "${estado.recompensas.size} premios",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 7.dp
                            ),
                            color = RVerde,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Error de consulta.

            if (estado.error != null) {

                item {

                    Surface(

                        modifier = Modifier.fillMaxWidth(),

                        color = Color(0xFFFDECEC),

                        shape = RoundedCornerShape(18.dp)

                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                estado.error.orEmpty(),
                                color = RRojo,
                                fontWeight = FontWeight.Medium
                            )

                            TextButton(

                                onClick = {

                                    errorCerrado = null

                                    rewardsViewModel
                                        .cargarCatalogo(usuarioId)
                                }

                            ) {

                                Text(
                                    "Actualizar catálogo",
                                    color = RVerde
                                )
                            }
                        }
                    }
                }
            }

            // Estado vacío.

            if (
                estado.recompensas.isEmpty() &&
                !estado.isLoading &&
                estado.error == null
            ) {

                item {

                    Surface(

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(24.dp),

                        color = Color.White

                    ) {

                        Column(

                            modifier = Modifier.padding(28.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally

                        ) {

                            Text(
                                "🎁",
                                fontSize = 48.sp
                            )

                            Spacer(Modifier.height(15.dp))

                            Text(
                                "Próximamente más premios",
                                fontSize = 20.sp,
                                color = RVerde,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Vuelve pronto para descubrir nuevas recompensas.",
                                color = RGris,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Catálogo de dos columnas.

            val filas =
                estado.recompensas.chunked(2)

            items(filas.size) { indice ->

                val fila = filas[indice]

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)

                ) {

                    fila.forEach { recompensa ->

                        TarjetaRecompensa(

                            recompensa = recompensa,

                            saldo = estado.saldo,

                            habilitada =
                                !estado.isLoading &&
                                        estado.error == null &&
                                        estado.resultadoCanje == null,

                            modifier = Modifier.weight(1f),

                            onClick = {

                                seleccionada = recompensa
                            }
                        )
                    }

                    if (fila.size == 1) {

                        Spacer(Modifier.weight(1f))
                    }
                }
            }

            // Pie de página.

            item {

                Text(
                    "♻ Recicla, gana y transforma.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    color = RVerde,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }
    }

    // ==================================================
    // CONTROL CENTRALIZADO DE VENTANAS
    // ==================================================

    val resultado = estado.resultadoCanje

    when {

        estado.isLoading -> {

            EcoCargandoDialog(
                titulo = "Un momento...",
                mensaje = "Estamos procesando tus recompensas."
            )
        }

        resultado != null -> {

            VentanaCanjeRealizado(

                nombre = resultado.nombre,

                puntos = resultado.puntosUtilizados,

                saldo = resultado.saldoRestante,

                onMisPremios = {

                    rewardsViewModel.cerrarResultadoCanje()

                    abrirMisPremios()
                },

                onExplorar = {

                    rewardsViewModel.cerrarResultadoCanje()

                    rewardsViewModel.cargarCatalogo(usuarioId)
                },

                onInicio = {

                    rewardsViewModel.cerrarResultadoCanje()

                    onBackClick()
                }
            )
        }

        estado.error != null &&
                estado.error != errorCerrado -> {

            EcoErrorDialog(

                titulo = "No pudimos continuar",

                mensaje = estado.error.orEmpty(),

                onDismiss = {

                    errorCerrado = estado.error
                },

                onReintentar = {

                    errorCerrado = null

                    // No repetir automáticamente el canje.

                    rewardsViewModel.cargarCatalogo(usuarioId)
                }
            )
        }

        seleccionada != null -> {

            val premio = seleccionada!!

            VentanaConfirmarRecompensa(

                recompensa = premio,

                saldo = estado.saldo,

                onCancelar = {

                    seleccionada = null
                },

                onConfirmar = {

                    seleccionada = null

                    rewardsViewModel.canjear(
                        usuarioId,
                        premio.id
                    )
                }
            )
        }
    }
}

// ======================================================
// TARJETA DE SALDO
// ======================================================

@Composable
private fun TarjetaSaldoRewards(

    saldo: Int,

    cargando: Boolean

) {

    val saldoAnimado by animateIntAsState(

        targetValue = saldo,

        animationSpec = tween(700),

        label = "Saldo de puntos"
    )

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        RVerdeOscuro,
                        RVerde,
                        RVerdeMedio
                    )
                ),
                RoundedCornerShape(27.dp)
            )
            .padding(23.dp)

    ) {

        Column {

            Text(
                "MI SALDO DISPONIBLE",
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = Color(0xFFCBEDD3),
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {

                Text(

                    if (cargando)
                        "—"
                    else
                        formatoRecompensa(saldoAnimado),

                    fontSize = 42.sp,

                    color = Color.White,

                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "PTS",
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = RDorado,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.18f)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Tus acciones verdes tienen recompensa.",
                fontSize = 12.sp,
                color = Color.White
            )
        }

        Text(
            "♻",
            modifier = Modifier.align(Alignment.TopEnd),
            color = Color.White.copy(alpha = 0.17f),
            fontSize = 65.sp
        )
    }
}

// ======================================================
// ACCESO A MIS PREMIOS
// ======================================================

@Composable
private fun AccesoMisPremios(

    onClick: () -> Unit

) {

    Card(

        onClick = onClick,

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(23.dp),

        colors = CardDefaults.cardColors(
            containerColor = RAmarillo
        )

    ) {

        Row(

            modifier = Modifier.padding(18.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Box(

                modifier = Modifier
                    .size(58.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(17.dp)
                    ),

                contentAlignment = Alignment.Center

            ) {

                Text(
                    "🎟️",
                    fontSize = 30.sp
                )
            }

            Spacer(Modifier.width(13.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "Mis premios",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RNaranja
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    "Consulta y reclama tus canjes con el código ECO#.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = RNaranja
                )
            }

            Text(
                "›",
                fontSize = 32.sp,
                color = RNaranja
            )
        }
    }
}

// ======================================================
// TARJETA INDIVIDUAL DE RECOMPENSA
// ======================================================

@Composable
private fun TarjetaRecompensa(

    recompensa: RecompensaLocal,

    saldo: Int,

    habilitada: Boolean,

    modifier: Modifier = Modifier,

    onClick: () -> Unit

) {

    val puedeCanjear =
        habilitada &&
                recompensa.stock > 0 &&
                saldo >= recompensa.costoPuntos

    Card(

        modifier = modifier,

        shape = RoundedCornerShape(23.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )

    ) {

        Column(
            modifier = Modifier.padding(13.dp)
        ) {

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                RVerdeClaro,
                                Color(0xFFF0F8F2)
                            )
                        ),
                        RoundedCornerShape(17.dp)
                    ),

                contentAlignment = Alignment.Center

            ) {

                Text(
                    iconoRecompensa(recompensa.nombre),
                    fontSize = 55.sp
                )

                Surface(

                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(7.dp),

                    shape = RoundedCornerShape(12.dp),

                    color = Color.White

                ) {

                    Text(
                        if (recompensa.stock > 0)
                            "${recompensa.stock} disp."
                        else
                            "Agotado",

                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 5.dp
                        ),

                        fontSize = 9.sp,

                        fontWeight = FontWeight.Bold,

                        color = if (recompensa.stock > 0)
                            RVerde
                        else
                            RRojo
                    )
                }
            }

            Spacer(Modifier.height(13.dp))

            Text(

                recompensa.nombre,

                modifier = Modifier.heightIn(min = 43.dp),

                fontSize = 15.sp,

                lineHeight = 20.sp,

                fontWeight = FontWeight.ExtraBold,

                color = RTexto
            )

            Spacer(Modifier.height(5.dp))

            Text(

                recompensa.descripcion,

                modifier = Modifier.heightIn(min = 45.dp),

                fontSize = 11.sp,

                lineHeight = 16.sp,

                color = RGris
            )

            Spacer(Modifier.height(14.dp))

            Text(
                "${formatoRecompensa(recompensa.costoPuntos)} pts",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RVerde
            )

            Spacer(Modifier.height(12.dp))

            Button(

                onClick = onClick,

                enabled = puedeCanjear,

                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 45.dp),

                contentPadding = PaddingValues(7.dp),

                shape = RoundedCornerShape(13.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = RVerde
                )

            ) {

                Text(

                    when {

                        recompensa.stock <= 0 ->
                            "Agotado"

                        saldo < recompensa.costoPuntos ->
                            "Faltan puntos"

                        else ->
                            "Canjear"
                    },

                    fontSize = 12.sp,

                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ======================================================
// CONFIRMACIÓN DE CANJE
// ======================================================

@Composable
private fun VentanaConfirmarRecompensa(

    recompensa: RecompensaLocal,

    saldo: Int,

    onCancelar: () -> Unit,

    onConfirmar: () -> Unit

) {

    VentanaRewardsBase(
        onDismiss = onCancelar
    ) {

        Text(
            iconoRecompensa(recompensa.nombre),
            fontSize = 58.sp
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Confirma tu canje",
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RVerde,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Estás a un paso de obtener tu recompensa.",
            fontSize = 13.sp,
            color = RGris,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        Surface(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(19.dp),

            color = RFondo

        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                FilaRewards(
                    "Recompensa",
                    recompensa.nombre
                )

                HorizontalDivider(color = RBorde)

                FilaRewards(
                    "Saldo actual",
                    "${formatoRecompensa(saldo)} pts"
                )

                HorizontalDivider(color = RBorde)

                FilaRewards(
                    "Costo del premio",
                    "−${formatoRecompensa(recompensa.costoPuntos)} pts",
                    RRojo
                )

                HorizontalDivider(color = RBorde)

                FilaRewards(
                    "Saldo restante",
                    "${formatoRecompensa(saldo - recompensa.costoPuntos)} pts"
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Surface(

            color = RAmarillo,

            shape = RoundedCornerShape(15.dp)

        ) {

            Text(
                "🎟️ Una vez confirmado, consulta tu código en Mis premios y preséntalo en la papelería de la universidad.",
                modifier = Modifier.padding(15.dp),
                fontSize = 12.sp,
                lineHeight = 19.sp,
                color = RNaranja
            )
        }

        Spacer(Modifier.height(22.dp))

        BotonRewards(

            texto = "Confirmar canje",

            habilitado =
                saldo >= recompensa.costoPuntos &&
                        recompensa.stock > 0,

            onClick = onConfirmar
        )

        Spacer(Modifier.height(10.dp))

        BotonSecundarioRewards(
            texto = "Cancelar",
            onClick = onCancelar
        )
    }
}

// ======================================================
// COMPROBANTE EXITOSO
// ======================================================

@Composable
private fun VentanaCanjeRealizado(

    nombre: String,

    puntos: Int,

    saldo: Int?,

    onMisPremios: () -> Unit,

    onExplorar: () -> Unit,

    onInicio: () -> Unit

) {

    VentanaRewardsBase(

        onDismiss = {},

        permitirCerrar = false

    ) {

        Box(

            modifier = Modifier
                .size(87.dp)
                .background(
                    RVerdeClaro,
                    CircleShape
                ),

            contentAlignment = Alignment.Center

        ) {

            Text(
                "✓",
                fontSize = 50.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RVerde
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            "¡Canje exitoso!",
            fontSize = 27.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RVerde,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Tu recompensa ha sido registrada correctamente.",
            color = RGris,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(21.dp))

        Surface(

            modifier = Modifier.fillMaxWidth(),

            color = RFondo,

            shape = RoundedCornerShape(20.dp)

        ) {

            Column(
                modifier = Modifier.padding(17.dp)
            ) {

                FilaRewards(
                    "Recompensa",
                    nombre
                )

                HorizontalDivider(color = RBorde)

                FilaRewards(
                    "Puntos utilizados",
                    "−${formatoRecompensa(puntos)} pts",
                    RRojo
                )

                HorizontalDivider(color = RBorde)

                FilaRewards(
                    "Saldo disponible",
                    saldo?.let {
                        "${formatoRecompensa(it)} pts"
                    } ?: "Pendiente de actualizar"
                )

                HorizontalDivider(color = RBorde)

                FilaRewards(
                    "Estado del canje",
                    "Registrado correctamente"
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Surface(

            modifier = Modifier.fillMaxWidth(),

            color = RAmarillo,

            shape = RoundedCornerShape(19.dp)

        ) {

            Column(

                modifier = Modifier.padding(19.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {

                Text(
                    "🎟️",
                    fontSize = 40.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Reclama tu premio",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RNaranja
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    "Tu código de reclamación se consulta en Mis premios.",
                    color = RNaranja,
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        BotonRewards(
            texto = "Ver mi código de reclamación",
            onClick = onMisPremios
        )

        Spacer(Modifier.height(10.dp))

        BotonSecundarioRewards(
            texto = "Seguir explorando",
            onClick = onExplorar
        )

        TextButton(onClick = onInicio) {

            Text(
                "Volver al inicio",
                color = RVerde
            )
        }
    }
}

// ======================================================
// COMPONENTES REUTILIZABLES
// ======================================================

@Composable
private fun VentanaRewardsBase(

    onDismiss: () -> Unit,

    permitirCerrar: Boolean = true,

    contenido: @Composable ColumnScope.() -> Unit

) {

    val escala = remember {
        Animatable(0.90f)
    }

    LaunchedEffect(Unit) {

        escala.animateTo(

            targetValue = 1f,

            animationSpec = spring(
                dampingRatio = 0.65f
            )
        )
    }

    Dialog(

        onDismissRequest = onDismiss,

        properties = DialogProperties(

            usePlatformDefaultWidth = false,

            dismissOnBackPress = permitirCerrar,

            dismissOnClickOutside = permitirCerrar
        )

    ) {

        Surface(

            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(0.92f)
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
                    .heightIn(max = 720.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                content = contenido
            )
        }
    }
}

@Composable
private fun FilaRewards(

    titulo: String,

    valor: String,

    color: Color = RVerde

) {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)

    ) {

        Text(
            titulo,
            color = RGris,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(4.dp))

        Text(
            valor,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BotonRewards(

    texto: String,

    habilitado: Boolean = true,

    onClick: () -> Unit

) {

    Button(

        onClick = onClick,

        enabled = habilitado,

        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),

        shape = RoundedCornerShape(15.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = RVerde
        )

    ) {

        Text(
            texto,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BotonSecundarioRewards(

    texto: String,

    onClick: () -> Unit

) {

    OutlinedButton(

        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp),

        shape = RoundedCornerShape(15.dp),

        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = RVerde
        )

    ) {

        Text(
            texto,
            fontWeight = FontWeight.Bold
        )
    }
}

// ======================================================
// FIN DEL ARCHIVO
// ======================================================