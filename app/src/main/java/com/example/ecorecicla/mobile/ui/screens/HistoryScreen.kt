package com.example.ecorecicla.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecorecicla.mobile.viewmodel.RewardsViewModel
import com.example.ecorecicla.mobile.data.repository.MovimientoLocal
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

// ======================================================
// ECORECICLA MOBILE
// HISTORIAL DE MOVIMIENTOS
// Arquitectura MVVM
// Patrón Observer
// Persistencia SQLite
// ======================================================

private val HVerde = Color(0xFF166534)
private val HVerdeOscuro = Color(0xFF103D29)
private val HVerdeMedio = Color(0xFF23834D)
private val HVerdeClaro = Color(0xFFE4F2E8)
private val HFondo = Color(0xFFF4F7F5)
private val HTexto = Color(0xFF172B21)
private val HGris = Color(0xFF718078)
private val HBorde = Color(0xFFE0E8E2)
private val HRojo = Color(0xFFB91C1C)
private val HRojoClaro = Color(0xFFFDECEC)
private val HNaranja = Color(0xFF94620C)
private val HAmarillo = Color(0xFFFFF1DB)

// Formato de números.

private fun puntosHistorial(numero: Number): String {

    return NumberFormat
        .getNumberInstance(Locale("es", "CO"))
        .format(numero)
}

// Identificar reciclajes.

private fun esReciclaje(
    movimiento: MovimientoLocal
): Boolean {

    return movimiento.tipo.equals(
        "Reciclaje",
        ignoreCase = true
    )
}

// ======================================================
// PANTALLA PRINCIPAL
// ======================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(

    usuarioId: Long,

    onBackClick: () -> Unit = {},

    rewardsViewModel: RewardsViewModel = viewModel(),

    onMyRewardsClick: (() -> Unit)? = null

) {

    val estado by
    rewardsViewModel.uiState.collectAsState()

    var filtro by rememberSaveable(usuarioId) {
        mutableStateOf("Todos")
    }

    var busqueda by rememberSaveable(usuarioId) {
        mutableStateOf("")
    }

    var seleccionado by remember(usuarioId) {
        mutableStateOf<MovimientoLocal?>(null)
    }

    var mostrarPremios by remember(usuarioId) {
        mutableStateOf(false)
    }

    LaunchedEffect(usuarioId) {

        rewardsViewModel.cargarHistorial(usuarioId)
    }

    // Alternativa provisional si no existe navegación.

    if (mostrarPremios) {

        MyRewardsScreen(
            usuarioId = usuarioId,
            onBackClick = {
                mostrarPremios = false
                rewardsViewModel.cargarHistorial(usuarioId)
            },
            onExploreClick = {
                mostrarPremios = false
                rewardsViewModel.cargarHistorial(usuarioId)
            }
        )

        return
    }

    val abrirPremios: () -> Unit = {

        seleccionado = null

        if (onMyRewardsClick != null) {

            onMyRewardsClick()

        } else {

            mostrarPremios = true
        }
    }

    // Datos reales consultados desde SQLite.

    val movimientos = estado.movimientos

    val reciclajes = movimientos.filter {
        esReciclaje(it)
    }

    val canjes = movimientos.filter {
        it.tipo.equals("Canje", ignoreCase = true)
    }

    val ganados = reciclajes.sumOf {
        it.puntos.toLong()
    }

    val utilizados = canjes.sumOf {
        abs(it.puntos.toLong())
    }

    // Filtrar por tipo y texto.

    val filtrados = movimientos.filter { movimiento ->

        val coincideTipo = when (filtro) {

            "Reciclajes" -> esReciclaje(movimiento)

            "Canjes" -> movimiento.tipo.equals(
                "Canje",
                ignoreCase = true
            )

            else -> true
        }

        val texto = busqueda.trim()

        val coincideBusqueda =
            texto.isBlank() ||
                    movimiento.descripcion.contains(
                        texto,
                        ignoreCase = true
                    ) ||
                    movimiento.fecha.contains(
                        texto,
                        ignoreCase = true
                    ) ||
                    movimiento.tipo.contains(
                        texto,
                        ignoreCase = true
                    )

        coincideTipo && coincideBusqueda
    }

    // Agrupar cronológicamente por fecha.

    val agrupados = filtrados.groupBy {

        it.fecha
            .trim()
            .substringBefore("T")
            .substringBefore(" ")
    }

    Scaffold(

        containerColor = HFondo,

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            "EcoRecicla",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HVerde
                        )

                        Text(
                            "TU IMPACTO AMBIENTAL",
                            fontSize = 9.sp,
                            color = HGris
                        )
                    }
                },

                navigationIcon = {

                    TextButton(
                        onClick = onBackClick
                    ) {

                        Text(
                            "‹",
                            fontSize = 34.sp,
                            color = HVerde
                        )
                    }
                },

                actions = {

                    TextButton(

                        enabled = !estado.isLoading,

                        onClick = {

                            rewardsViewModel
                                .cargarHistorial(usuarioId)
                        }

                    ) {

                        Text(
                            "↻",
                            fontSize = 28.sp,
                            color = HVerde
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
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
                Arrangement.spacedBy(14.dp)

        ) {

            // Encabezado.

            item {

                Column {

                    Text(
                        "ACTIVIDAD ECORECICLA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = HVerdeMedio
                    )

                    Spacer(Modifier.height(7.dp))

                    Text(
                        "Mi historial",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HVerdeOscuro
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        "Cada acción cuenta. Consulta tu recorrido sostenible.",
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = HGris
                    )
                }
            }

            // Resumen de puntos.

            item {

                ResumenHistorial(

                    ganados = ganados,

                    utilizados = utilizados,

                    total = movimientos.size,

                    disponible =
                        !estado.isLoading &&
                                estado.error == null
                )
            }

            // Acceso a premios pendientes.

            item {

                AccesoPremiosHistorial(

                    cantidad = estado.cantidadPendientes,

                    habilitado = !estado.isLoading,

                    onClick = abrirPremios
                )
            }

            // Buscador.

            item {

                OutlinedTextField(

                    value = busqueda,

                    onValueChange = {
                        busqueda = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    placeholder = {

                        Text(
                            "Buscar movimiento",
                            fontSize = 13.sp
                        )
                    },

                    leadingIcon = {

                        Text(
                            "⌕",
                            fontSize = 26.sp,
                            color = HVerde
                        )
                    },

                    trailingIcon = {

                        if (busqueda.isNotEmpty()) {

                            TextButton(
                                onClick = {
                                    busqueda = ""
                                }
                            ) {

                                Text(
                                    "×",
                                    fontSize = 24.sp
                                )
                            }
                        }
                    },

                    shape = RoundedCornerShape(17.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HVerde,
                        unfocusedBorderColor = HBorde,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = HVerde
                    )
                )
            }

            // Filtros.

            item {

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)

                ) {

                    listOf(
                        "Todos",
                        "Reciclajes",
                        "Canjes"
                    ).forEach { opcion ->

                        FilterChip(

                            selected = filtro == opcion,

                            onClick = {
                                filtro = opcion
                            },

                            modifier = Modifier.weight(1f),

                            label = {

                                Text(
                                    opcion,
                                    fontSize = 12.sp
                                )
                            },

                            colors = FilterChipDefaults
                                .filterChipColors(

                                    selectedContainerColor =
                                        HVerdeClaro,

                                    selectedLabelColor =
                                        HVerde
                                )
                        )
                    }
                }
            }

            // Contadores.

            item {

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)

                ) {

                    ContadorHistorial(
                        titulo = "Reciclajes",
                        cantidad = reciclajes.size,
                        icono = "♻",
                        modifier = Modifier.weight(1f)
                    )

                    ContadorHistorial(
                        titulo = "Canjes",
                        cantidad = canjes.size,
                        icono = "🎁",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Listado de movimientos.

            when {

                estado.isLoading -> {

                    item {

                        Box(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),

                            contentAlignment = Alignment.Center

                        ) {

                            CircularProgressIndicator(
                                color = HVerde
                            )
                        }
                    }
                }

                estado.error != null -> {

                    item {

                        Surface(

                            modifier = Modifier.fillMaxWidth(),

                            shape = RoundedCornerShape(20.dp),

                            color = HRojoClaro

                        ) {

                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {

                                Text(
                                    "No pudimos cargar tu historial",
                                    fontWeight = FontWeight.Bold,
                                    color = HRojo
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    estado.error.orEmpty(),
                                    color = HRojo,
                                    fontSize = 13.sp
                                )

                                TextButton(

                                    onClick = {

                                        rewardsViewModel
                                            .cargarHistorial(usuarioId)
                                    }

                                ) {

                                    Text(
                                        "Reintentar",
                                        color = HVerde
                                    )
                                }
                            }
                        }
                    }
                }

                filtrados.isEmpty() -> {

                    item {

                        HistorialVacio(
                            sinMovimientos = movimientos.isEmpty(),
                            onLimpiar = {
                                filtro = "Todos"
                                busqueda = ""
                            }
                        )
                    }
                }

                else -> {

                    agrupados.forEach { (fecha, lista) ->

                        item {

                            Row(

                                verticalAlignment =
                                    Alignment.CenterVertically

                            ) {

                                Text(
                                    fecha,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HGris
                                )

                                Spacer(Modifier.width(12.dp))

                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = HBorde
                                )
                            }
                        }

                        items(lista) { movimiento ->

                            TarjetaMovimientoHistorial(

                                movimiento = movimiento,

                                onClick = {
                                    seleccionado = movimiento
                                }
                            )
                        }
                    }
                }
            }

            item {

                Text(

                    "♻ Cada pequeño cambio construye un gran futuro.",

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp),

                    fontSize = 12.sp,

                    color = HVerde,

                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // Comprobante individual.

    seleccionado?.let { movimiento ->

        ComprobanteMovimientoHistorial(

            movimiento = movimiento,

            onCerrar = {
                seleccionado = null
            },

            onMisPremios = abrirPremios
        )
    }
}

// ======================================================
// RESUMEN PRINCIPAL
// ======================================================

@Composable
private fun ResumenHistorial(

    ganados: Long,

    utilizados: Long,

    total: Int,

    disponible: Boolean

) {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        HVerdeOscuro,
                        HVerde,
                        HVerdeMedio
                    )
                ),
                RoundedCornerShape(27.dp)
            )
            .padding(22.dp)

    ) {

        Text(
            "RESUMEN DE TU ACTIVIDAD",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp,
            color = Color(0xFFBFE4C8)
        )

        Spacer(Modifier.height(17.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "Puntos ganados",
                    fontSize = 12.sp,
                    color = Color.White
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    if (disponible)
                        "+${puntosHistorial(ganados)}"
                    else
                        "—",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "Puntos canjeados",
                    fontSize = 12.sp,
                    color = Color.White
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    if (disponible)
                        "−${puntosHistorial(utilizados)}"
                    else
                        "—",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFF4D68A)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(
            color = Color.White.copy(alpha = 0.2f)
        )

        Spacer(Modifier.height(13.dp))

        Text(
            if (disponible)
                "$total movimientos registrados"
            else
                "Consultando movimientos",
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

// ======================================================
// ACCESO A MIS PREMIOS
// ======================================================

@Composable
private fun AccesoPremiosHistorial(

    cantidad: Int,

    habilitado: Boolean,

    onClick: () -> Unit

) {

    Card(

        onClick = onClick,

        enabled = habilitado,

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = HAmarillo
        )

    ) {

        Row(

            modifier = Modifier.padding(17.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Box(

                modifier = Modifier
                    .size(49.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(14.dp)
                    ),

                contentAlignment = Alignment.Center

            ) {

                Text(
                    "🎟️",
                    fontSize = 27.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "Mis premios por reclamar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HNaranja
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    "Consulta tus códigos ECO#",
                    fontSize = 12.sp,
                    color = HNaranja
                )
            }

            if (cantidad > 0) {

                Surface(
                    shape = CircleShape,
                    color = HVerde
                ) {

                    Text(
                        cantidad.toString(),
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Text(
                "›",
                fontSize = 28.sp,
                color = HNaranja
            )
        }
    }
}

// ======================================================
// CONTADORES
// ======================================================

@Composable
private fun ContadorHistorial(

    titulo: String,

    cantidad: Int,

    icono: String,

    modifier: Modifier = Modifier

) {

    Surface(

        modifier = modifier,

        shape = RoundedCornerShape(19.dp),

        color = Color.White

    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Text(
                icono,
                fontSize = 25.sp,
                color = HVerde
            )

            Spacer(Modifier.height(9.dp))

            Text(
                cantidad.toString(),
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HVerde
            )

            Text(
                titulo,
                fontSize = 12.sp,
                color = HGris
            )
        }
    }
}

// ======================================================
// TARJETA DE MOVIMIENTO
// ======================================================

@Composable
private fun TarjetaMovimientoHistorial(

    movimiento: MovimientoLocal,

    onClick: () -> Unit

) {

    val reciclaje = esReciclaje(movimiento)

    val color = if (reciclaje)
        HVerde
    else
        HRojo

    Card(

        onClick = onClick,

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )

    ) {

        Row(

            modifier = Modifier.padding(15.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Box(

                modifier = Modifier
                    .size(49.dp)
                    .background(
                        if (reciclaje)
                            HVerdeClaro
                        else
                            HAmarillo,
                        RoundedCornerShape(15.dp)
                    ),

                contentAlignment = Alignment.Center

            ) {

                Text(
                    if (reciclaje) "♻" else "🎁",
                    fontSize = 27.sp,
                    color = HVerde
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    movimiento.descripcion,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HTexto
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    movimiento.tipo,
                    fontSize = 12.sp,
                    color = HGris
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "${movimiento.fecha} UTC",
                    fontSize = 10.sp,
                    color = HGris
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(

                    text = (
                            if (reciclaje) "+" else "−"
                            ) + puntosHistorial(
                        abs(movimiento.puntos.toLong())
                    ),

                    fontSize = 19.sp,

                    fontWeight = FontWeight.ExtraBold,

                    color = color
                )

                Text(
                    "puntos",
                    fontSize = 11.sp,
                    color = HGris
                )
            }
        }
    }
}

// ======================================================
// COMPROBANTE INDIVIDUAL
// ======================================================

@Composable
private fun ComprobanteMovimientoHistorial(

    movimiento: MovimientoLocal,

    onCerrar: () -> Unit,

    onMisPremios: () -> Unit

) {

    val reciclaje = esReciclaje(movimiento)

    Dialog(

        onDismissRequest = onCerrar,

        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )

    ) {

        Surface(

            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(0.92f),

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
                    Alignment.CenterHorizontally

            ) {

                Box(

                    modifier = Modifier
                        .size(85.dp)
                        .background(
                            HVerdeClaro,
                            CircleShape
                        ),

                    contentAlignment = Alignment.Center

                ) {

                    Text(
                        if (reciclaje) "♻" else "🎁",
                        fontSize = 43.sp,
                        color = HVerde
                    )
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    if (reciclaje)
                        "Comprobante de reciclaje"
                    else
                        "Comprobante de canje",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HVerde,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Movimiento registrado en EcoRecicla",
                    fontSize = 12.sp,
                    color = HGris,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                Surface(

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(20.dp),

                    color = HFondo

                ) {

                    Column(
                        modifier = Modifier.padding(17.dp)
                    ) {

                        FilaDetalleHistorial(
                            titulo = "Tipo de movimiento",
                            valor = movimiento.tipo
                        )

                        HorizontalDivider(color = HBorde)

                        FilaDetalleHistorial(
                            titulo = "Descripción",
                            valor = movimiento.descripcion
                        )

                        HorizontalDivider(color = HBorde)

                        FilaDetalleHistorial(

                            titulo = if (reciclaje)
                                "Puntos ganados"
                            else
                                "Puntos utilizados",

                            valor = (
                                    if (reciclaje) "+" else "−"
                                    ) + puntosHistorial(
                                abs(movimiento.puntos.toLong())
                            ) + " pts",

                            color = if (reciclaje)
                                HVerde
                            else
                                HRojo
                        )

                        HorizontalDivider(color = HBorde)

                        FilaDetalleHistorial(
                            titulo = "Fecha de registro (UTC)",
                            valor = movimiento.fecha
                        )

                        HorizontalDivider(color = HBorde)

                        FilaDetalleHistorial(
                            titulo = "Estado del movimiento",
                            valor = "Registrado"
                        )
                    }
                }

                // Información para canjes.

                if (!reciclaje) {

                    Spacer(Modifier.height(18.dp))

                    Surface(

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(18.dp),

                        color = HAmarillo

                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                "🎟️ Reclamación física",
                                fontWeight = FontWeight.Bold,
                                color = HNaranja,
                                fontSize = 15.sp
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Consulta Mis premios para verificar si este canje tiene un código de reclamación disponible.",
                                fontSize = 12.sp,
                                lineHeight = 19.sp,
                                color = HNaranja
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Punto de entrega: papelería de la Fundación Universitaria San Mateo.",
                                fontSize = 12.sp,
                                lineHeight = 19.sp,
                                color = HNaranja
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Button(

                        onClick = onMisPremios,

                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),

                        shape = RoundedCornerShape(15.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = HVerde
                        )

                    ) {

                        Text(
                            "Consultar mis códigos ECO#",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(

                    onClick = onCerrar,

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),

                    shape = RoundedCornerShape(15.dp),

                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = HVerde
                    )

                ) {

                    Text(
                        "Cerrar comprobante",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ======================================================
// FILA DE INFORMACIÓN
// ======================================================

@Composable
private fun FilaDetalleHistorial(

    titulo: String,

    valor: String,

    color: Color = HVerde

) {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)

    ) {

        Text(
            titulo,
            fontSize = 12.sp,
            color = HGris
        )

        Spacer(Modifier.height(5.dp))

        Text(
            valor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// ======================================================
// HISTORIAL VACÍO
// ======================================================

@Composable
private fun HistorialVacio(

    sinMovimientos: Boolean,

    onLimpiar: () -> Unit

) {

    Surface(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(23.dp),

        color = Color.White

    ) {

        Column(

            modifier = Modifier.padding(26.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Box(

                modifier = Modifier
                    .size(85.dp)
                    .background(
                        HVerdeClaro,
                        CircleShape
                    ),

                contentAlignment = Alignment.Center

            ) {

                Text(
                    if (sinMovimientos) "♻" else "⌕",
                    fontSize = 42.sp,
                    color = HVerde
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(
                if (sinMovimientos)
                    "Tu historia comienza aquí"
                else
                    "No encontramos movimientos",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HVerde,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                if (sinMovimientos)
                    "Registra tu primer reciclaje y comienza a generar un impacto positivo."
                else
                    "Prueba con otro filtro o término de búsqueda.",
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = HGris,
                textAlign = TextAlign.Center
            )

            if (!sinMovimientos) {

                Spacer(Modifier.height(15.dp))

                TextButton(
                    onClick = onLimpiar
                ) {

                    Text(
                        "Limpiar filtros",
                        color = HVerde,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ======================================================
// FIN DEL ARCHIVO
// ECORECICLA MOBILE
// ======================================================