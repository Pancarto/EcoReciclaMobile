package com.example.ecorecicla.mobile.ui.screens

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecorecicla.mobile.data.local.EcoReciclaDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: MyRewardsScreen.kt
// RESPONSABILIDAD: Consultar códigos de reclamación.
// ARQUITECTURA: MVVM.
// PATRONES: Repository y Observer.
// ======================================================

private val MPVerde = Color(0xFF166534)
private val MPVerdeOscuro = Color(0xFF103D29)
private val MPVerdeMedio = Color(0xFF23834D)
private val MPVerdeClaro = Color(0xFFE4F2E8)
private val MPFondo = Color(0xFFF4F7F5)
private val MPTexto = Color(0xFF172B21)
private val MPGris = Color(0xFF718078)
private val MPDorado = Color(0xFFEBC76C)
private val MPNaranja = Color(0xFF94620C)
private val MPAmarillo = Color(0xFFFFF1DB)
private val MPBorde = Color(0xFFE0E8E2)
private val MPRojo = Color(0xFFB91C1C)

// ======================================================
// MODELO DE COMPROBANTE
// ======================================================

data class PremioReclamacionUi(

    val id: Long,

    val nombre: String,

    val puntos: Int,

    val fecha: String,

    val codigo: String?,

    val estado: String,

    val fechaEntrega: String? = null
)

// ======================================================
// REPOSITORIO DE CONSULTA
//
// Consulta exclusivamente los canjes del usuario.
// No genera nuevos códigos.
// No descuenta puntos.
// No modifica el estado de entrega.
// ======================================================

private class MisPremiosConsultaRepository(

    context: Context

) {

    private val database =
        EcoReciclaDatabase.obtenerInstancia(
            context.applicationContext
        )

    suspend fun consultar(

        usuarioId: Long

    ): List<PremioReclamacionUi> =
        withContext(Dispatchers.IO) {

            val premios =
                mutableListOf<PremioReclamacionUi>()

            database.readableDatabase.rawQuery(

                """
                SELECT
                    c.id,
                    r.nombre,
                    c.puntos_utilizados,
                    c.fecha,
                    c.codigo_reclamacion,
                    c.estado,
                    c.fecha_entrega
                FROM canjes c
                INNER JOIN recompensas r
                    ON r.id = c.recompensa_id
                WHERE c.usuario_id = ?
                ORDER BY c.fecha DESC, c.id DESC
                """.trimIndent(),

                arrayOf(usuarioId.toString())

            ).use { cursor ->

                while (cursor.moveToNext()) {

                    premios.add(

                        PremioReclamacionUi(

                            id = cursor.getLong(0),

                            nombre = cursor.getString(1),

                            puntos = cursor.getInt(2),

                            fecha = cursor.getString(3),

                            codigo =
                                if (cursor.isNull(4))
                                    null
                                else
                                    cursor.getString(4),

                            estado = cursor.getString(5),

                            fechaEntrega =
                                if (cursor.isNull(6))
                                    null
                                else
                                    cursor.getString(6)
                        )
                    )
                }
            }

            premios
        }
}

// ======================================================
// ESTADO OBSERVABLE
// ======================================================

data class MisPremiosUiState(

    val premios: List<PremioReclamacionUi> =
        emptyList(),

    val cargando: Boolean = true,

    val error: String? = null
)

// ======================================================
// VIEWMODEL
// ======================================================

class MisPremiosConsultaViewModel(

    application: Application

) : AndroidViewModel(application) {

    private val repository =
        MisPremiosConsultaRepository(application)

    private val _estado =
        MutableStateFlow(MisPremiosUiState())

    val estado = _estado.asStateFlow()

    private var consulta: Job? = null

    fun cargar(usuarioId: Long) {

        // Cancelar consultas anteriores.

        consulta?.cancel()

        // Limpiar datos anteriores para evitar que
        // aparezcan comprobantes de otra cuenta.

        _estado.value = MisPremiosUiState(
            cargando = true
        )

        consulta = viewModelScope.launch {

            try {

                val premios =
                    repository.consultar(usuarioId)

                _estado.value = MisPremiosUiState(

                    premios = premios,

                    cargando = false
                )

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _estado.value = MisPremiosUiState(

                    cargando = false,

                    error =
                        "No pudimos consultar tus recompensas. Intenta nuevamente."
                )
            }
        }
    }
}

// ======================================================
// FUNCIONES AUXILIARES
// ======================================================

private fun formatoMisPremios(numero: Number): String {

    return NumberFormat
        .getNumberInstance(Locale("es", "CO"))
        .format(numero)
}

private fun premioPendiente(

    premio: PremioReclamacionUi

): Boolean {

    return premio.estado.equals(
        "PENDIENTE",
        ignoreCase = true
    )
}

private fun premioEntregado(

    premio: PremioReclamacionUi

): Boolean {

    return premio.estado.equals(
        "ENTREGADO",
        ignoreCase = true
    )
}

private fun iconoMisPremios(nombre: String): String {

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
fun MyRewardsScreen(

    usuarioId: Long,

    onBackClick: () -> Unit = {},

    onExploreClick: () -> Unit = {},

    consultaViewModel: MisPremiosConsultaViewModel =
        viewModel()

) {

    val estado by
    consultaViewModel.estado.collectAsState()

    var filtro by rememberSaveable(usuarioId) {

        mutableStateOf("Todos")
    }

    var busqueda by rememberSaveable(usuarioId) {

        mutableStateOf("")
    }

    val snackbarHostState = remember {

        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(usuarioId) {

        consultaViewModel.cargar(usuarioId)
    }

    // Estadísticas reales.

    val pendientes = estado.premios.count {

        premioPendiente(it)
    }

    val entregados = estado.premios.count {

        premioEntregado(it)
    }

    val anteriores = estado.premios.size -
            pendientes - entregados

    // Filtrar comprobantes.

    val filtrados = estado.premios.filter { premio ->

        val coincideEstado = when (filtro) {

            "Pendientes" -> premioPendiente(premio)

            "Entregados" -> premioEntregado(premio)

            "Anteriores" ->
                !premioPendiente(premio) &&
                        !premioEntregado(premio)

            else -> true
        }

        val texto = busqueda.trim()

        val coincideBusqueda =
            texto.isBlank() ||

                    premio.nombre.contains(
                        texto,
                        ignoreCase = true
                    ) ||

                    premio.codigo.orEmpty().contains(
                        texto,
                        ignoreCase = true
                    ) ||

                    premio.fecha.contains(
                        texto,
                        ignoreCase = true
                    )

        coincideEstado && coincideBusqueda
    }

    Scaffold(

        containerColor = MPFondo,

        snackbarHost = {

            SnackbarHost(
                hostState = snackbarHostState
            )
        },

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            "EcoRecicla",
                            fontSize = 22.sp,
                            color = MPVerde,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            "RECLAMACIÓN DE PREMIOS",
                            fontSize = 9.sp,
                            color = MPGris
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
                            color = MPVerde
                        )
                    }
                },

                actions = {

                    TextButton(

                        enabled = !estado.cargando,

                        onClick = {

                            consultaViewModel.cargar(usuarioId)
                        }

                    ) {

                        Text(
                            "↻",
                            fontSize = 28.sp,
                            color = MPVerde
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
                Arrangement.spacedBy(16.dp)

        ) {

            // Encabezado.

            item {

                Column {

                    Text(
                        "TUS LOGROS SOSTENIBLES",
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = MPVerdeMedio
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Mis premios",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MPVerdeOscuro
                    )

                    Spacer(Modifier.height(7.dp))

                    Text(
                        "Consulta tus recompensas y presenta tu código en el punto físico de entrega.",
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = MPGris
                    )
                }
            }

            // Resumen general.

            item {

                ResumenMisPremios(

                    pendientes = pendientes,

                    entregados = entregados,

                    anteriores = anteriores,

                    disponible =
                        !estado.cargando &&
                                estado.error == null
                )
            }

            // Instrucciones para reclamar.

            item {

                InstruccionesReclamacion()
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
                            "Buscar premio o código ECO#",
                            fontSize = 12.sp
                        )
                    },

                    leadingIcon = {

                        Text(
                            "⌕",
                            fontSize = 26.sp,
                            color = MPVerde
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
                                    fontSize = 24.sp,
                                    color = MPVerde
                                )
                            }
                        }
                    },

                    shape = RoundedCornerShape(17.dp),

                    colors = OutlinedTextFieldDefaults.colors(

                        focusedBorderColor = MPVerde,

                        unfocusedBorderColor = MPBorde,

                        focusedContainerColor = Color.White,

                        unfocusedContainerColor = Color.White
                    )
                )
            }

            // Filtros.

            item {

                Column {

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(7.dp)

                    ) {

                        listOf(
                            "Todos",
                            "Pendientes",
                            "Entregados"
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
                                        fontSize = 11.sp
                                    )
                                },

                                colors = FilterChipDefaults
                                    .filterChipColors(

                                        selectedContainerColor =
                                            MPVerdeClaro,

                                        selectedLabelColor =
                                            MPVerde
                                    )
                            )
                        }
                    }

                    if (anteriores > 0) {

                        FilterChip(

                            selected = filtro == "Anteriores",

                            onClick = {

                                filtro = "Anteriores"
                            },

                            label = {

                                Text(
                                    "Canjes anteriores ($anteriores)",
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }

            // Estado de la consulta.

            when {

                estado.cargando -> {

                    item {

                        Box(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),

                            contentAlignment = Alignment.Center

                        ) {

                            CircularProgressIndicator(
                                color = MPVerde
                            )
                        }
                    }
                }

                estado.error != null -> {

                    item {

                        ErrorConsultaMisPremios(

                            mensaje = estado.error.orEmpty(),

                            onReintentar = {

                                consultaViewModel.cargar(usuarioId)
                            }
                        )
                    }
                }

                filtrados.isEmpty() -> {

                    item {

                        MisPremiosVacio(

                            sinPremios = estado.premios.isEmpty(),

                            onExplorar = onExploreClick,

                            onLimpiar = {

                                filtro = "Todos"

                                busqueda = ""
                            }
                        )
                    }
                }

                else -> {

                    items(

                        items = filtrados,

                        key = { it.id }

                    ) { premio ->

                        TarjetaPremioReclamacion(

                            premio = premio,

                            onCopiado = {

                                scope.launch {

                                    snackbarHostState.showSnackbar(
                                        "Código copiado correctamente"
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // Acceso al catálogo.

            item {

                OutlinedButton(

                    onClick = onExploreClick,

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),

                    shape = RoundedCornerShape(15.dp),

                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MPVerde
                    )

                ) {

                    Text(
                        "Explorar más recompensas",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {

                Text(
                    "EcoRecicla · Tus acciones hacen la diferencia.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    fontSize = 11.sp,
                    color = MPGris,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ======================================================
// RESUMEN DE PREMIOS
// ======================================================

@Composable
private fun ResumenMisPremios(

    pendientes: Int,

    entregados: Int,

    anteriores: Int,

    disponible: Boolean

) {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        MPVerdeOscuro,
                        MPVerde,
                        MPVerdeMedio
                    )
                ),
                RoundedCornerShape(27.dp)
            )
            .padding(23.dp)

    ) {

        Text(
            "MIS RECOMPENSAS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFFCBEDD3)
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "Por reclamar",
                    fontSize = 13.sp,
                    color = Color.White
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    if (disponible)
                        pendientes.toString()
                    else
                        "—",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MPDorado
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "Entregados",
                    fontSize = 13.sp,
                    color = Color.White
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    if (disponible)
                        entregados.toString()
                    else
                        "—",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(17.dp))

        HorizontalDivider(
            color = Color.White.copy(alpha = 0.2f)
        )

        Spacer(Modifier.height(12.dp))

        Text(

            if (!disponible)
                "Consultando tus recompensas..."
            else if (anteriores > 0)
                "$anteriores canjes anteriores sin código."
            else
                "Gracias por contribuir al cuidado del planeta.",

            fontSize = 12.sp,

            color = Color.White
        )
    }
}

// ======================================================
// INSTRUCCIONES DE ENTREGA
// ======================================================

@Composable
private fun InstruccionesReclamacion() {

    Surface(

        modifier = Modifier.fillMaxWidth(),

        color = MPAmarillo,

        shape = RoundedCornerShape(23.dp)

    ) {

        Column(
            modifier = Modifier.padding(19.dp)
        ) {

            Row(

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Text(
                    "📍",
                    fontSize = 28.sp
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    "¿Cómo reclamar mi premio?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MPNaranja
                )
            }

            Spacer(Modifier.height(18.dp))

            PasoReclamacion(
                numero = "1",
                titulo = "Consulta tu comprobante",
                descripcion = "Localiza una recompensa pendiente."
            )

            Spacer(Modifier.height(13.dp))

            PasoReclamacion(
                numero = "2",
                titulo = "Copia tu código ECO#",
                descripcion = "Este identifica tu canje registrado."
            )

            Spacer(Modifier.height(13.dp))

            PasoReclamacion(
                numero = "3",
                titulo = "Acércate a la papelería",
                descripcion = "Presenta el comprobante al personal encargado."
            )

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(
                color = MPNaranja.copy(alpha = 0.16f)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Fundación Universitaria San Mateo",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MPNaranja
            )
        }
    }
}

@Composable
private fun PasoReclamacion(

    numero: String,

    titulo: String,

    descripcion: String

) {

    Row(

        verticalAlignment = Alignment.Top

    ) {

        Box(

            modifier = Modifier
                .size(32.dp)
                .background(
                    MPNaranja,
                    RoundedCornerShape(11.dp)
                ),

            contentAlignment = Alignment.Center

        ) {

            Text(
                numero,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.width(11.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                titulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MPNaranja
            )

            Spacer(Modifier.height(4.dp))

            Text(
                descripcion,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = MPTexto
            )
        }
    }
}

// ======================================================
// TARJETA DE COMPROBANTE
// ======================================================

@Composable
private fun TarjetaPremioReclamacion(

    premio: PremioReclamacionUi,

    onCopiado: () -> Unit

) {

    val context = LocalContext.current

    val pendiente = premioPendiente(premio)

    val entregado = premioEntregado(premio)

    val codigoDisponible =
        !premio.codigo.isNullOrBlank()

    val estadoTexto = when {

        pendiente -> "Pendiente de entrega"

        entregado -> "Entregado"

        premio.estado.equals(
            "ANTERIOR",
            ignoreCase = true
        ) -> "Canje anterior"

        else -> premio.estado
    }

    val colorEstado = when {

        pendiente -> MPNaranja

        entregado -> MPVerde

        else -> MPGris
    }

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(25.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )

    ) {

        Column(
            modifier = Modifier.padding(19.dp)
        ) {

            // Identidad del premio.

            Row(

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Box(

                    modifier = Modifier
                        .size(65.dp)
                        .background(
                            MPVerdeClaro,
                            RoundedCornerShape(18.dp)
                        ),

                    contentAlignment = Alignment.Center

                ) {

                    Text(
                        iconoMisPremios(premio.nombre),
                        fontSize = 37.sp
                    )
                }

                Spacer(Modifier.width(13.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        premio.nombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MPTexto
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        "${formatoMisPremios(premio.puntos)} puntos utilizados",
                        fontSize = 12.sp,
                        color = MPGris
                    )

                    Spacer(Modifier.height(7.dp))

                    Surface(

                        shape = RoundedCornerShape(12.dp),

                        color =
                            if (pendiente)
                                MPAmarillo
                            else
                                MPVerdeClaro

                    ) {

                        Text(
                            estadoTexto,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorEstado
                        )
                    }
                }
            }

            Spacer(Modifier.height(17.dp))

            HorizontalDivider(color = MPBorde)

            Spacer(Modifier.height(17.dp))

            // Código persistente de SQLite.

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MPFondo,
                        RoundedCornerShape(19.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFBCD9C2),
                        shape = RoundedCornerShape(19.dp)
                    )
                    .padding(20.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {

                Text(
                    "CÓDIGO DE RECLAMACIÓN",
                    fontSize = 10.sp,
                    letterSpacing = 0.7.sp,
                    color = MPGris,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(13.dp))

                Text(

                    text = premio.codigo
                        ?.takeIf { it.isNotBlank() }
                        ?: "Sin código",

                    fontSize =
                        if (codigoDisponible)
                            29.sp
                        else
                            21.sp,

                    fontFamily = FontFamily.Monospace,

                    fontWeight = FontWeight.ExtraBold,

                    color =
                        if (codigoDisponible)
                            MPVerde
                        else
                            MPGris,

                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                Text(

                    when {

                        entregado ->
                            "Este premio figura como entregado."

                        pendiente && codigoDisponible ->
                            "Presenta este código al personal encargado."

                        pendiente ->
                            "Este canje no tiene un código disponible."

                        else ->
                            "Registro histórico de recompensas."
                    },

                    fontSize = 11.sp,

                    lineHeight = 17.sp,

                    textAlign = TextAlign.Center,

                    color = MPGris
                )
            }

            Spacer(Modifier.height(16.dp))

            // Copiar exclusivamente códigos pendientes.

            if (pendiente && codigoDisponible) {

                Button(

                    onClick = {

                        val codigo =
                            premio.codigo.orEmpty()

                        if (codigo.isNotBlank()) {

                            val clipboard =
                                context.getSystemService(
                                    Context.CLIPBOARD_SERVICE
                                ) as ClipboardManager

                            clipboard.setPrimaryClip(

                                ClipData.newPlainText(
                                    "Código EcoRecicla",
                                    codigo
                                )
                            )

                            onCopiado()
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),

                    shape = RoundedCornerShape(15.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = MPVerde
                    )

                ) {

                    Text(
                        "▣  Copiar código",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(17.dp))

            // Fecha registrada.

            Text(
                "FECHA DEL CANJE (UTC)",
                fontSize = 10.sp,
                color = MPGris,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(5.dp))

            Text(
                premio.fecha,
                fontSize = 13.sp,
                color = MPTexto,
                fontWeight = FontWeight.Bold
            )

            // Fecha de entrega, únicamente si existe.

            if (
                entregado &&
                !premio.fechaEntrega.isNullOrBlank()
            ) {

                Spacer(Modifier.height(12.dp))

                Text(
                    "FECHA DE ENTREGA (UTC)",
                    fontSize = 10.sp,
                    color = MPGris
                )

                Text(
                    premio.fechaEntrega.orEmpty(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MPVerde
                )
            }

            // Ubicación física.

            if (pendiente) {

                Spacer(Modifier.height(15.dp))

                Surface(

                    modifier = Modifier.fillMaxWidth(),

                    color = MPAmarillo,

                    shape = RoundedCornerShape(15.dp)

                ) {

                    Row(

                        modifier = Modifier.padding(13.dp),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Text(
                            "📍",
                            fontSize = 23.sp
                        )

                        Spacer(Modifier.width(10.dp))

                        Column {

                            Text(
                                "Punto físico de entrega",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MPNaranja
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                "Papelería de la universidad.",
                                fontSize = 11.sp,
                                color = MPNaranja
                            )
                        }
                    }
                }
            }
        }
    }
}

// ======================================================
// ESTADO VACÍO
// ======================================================

@Composable
private fun MisPremiosVacio(

    sinPremios: Boolean,

    onExplorar: () -> Unit,

    onLimpiar: () -> Unit

) {

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

            Box(

                modifier = Modifier
                    .size(92.dp)
                    .background(
                        MPVerdeClaro,
                        RoundedCornerShape(27.dp)
                    ),

                contentAlignment = Alignment.Center

            ) {

                Text(
                    if (sinPremios)
                        "🎁"
                    else
                        "⌕",
                    fontSize = 48.sp,
                    color = MPVerde
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(

                if (sinPremios)
                    "Tu primer premio te espera"
                else
                    "No encontramos premios",

                fontSize = 21.sp,

                fontWeight = FontWeight.ExtraBold,

                color = MPVerde,

                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(

                if (sinPremios)
                    "Canjea tus puntos y aquí encontrarás tus comprobantes de reclamación."
                else
                    "Prueba con otro filtro o término de búsqueda.",

                fontSize = 13.sp,

                lineHeight = 20.sp,

                color = MPGris,

                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(19.dp))

            Button(

                onClick =
                    if (sinPremios)
                        onExplorar
                    else
                        onLimpiar,

                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),

                shape = RoundedCornerShape(15.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = MPVerde
                )

            ) {

                Text(

                    if (sinPremios)
                        "Explorar recompensas"
                    else
                        "Limpiar filtros",

                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ======================================================
// ERROR DE CONSULTA
// ======================================================

@Composable
private fun ErrorConsultaMisPremios(

    mensaje: String,

    onReintentar: () -> Unit

) {

    Surface(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(23.dp),

        color = Color(0xFFFDECEC)

    ) {

        Column(

            modifier = Modifier.padding(23.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Text(
                "!",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MPRojo
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "No pudimos cargar tus premios",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MPRojo,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                mensaje,
                fontSize = 13.sp,
                color = MPRojo,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(15.dp))

            Button(

                onClick = onReintentar,

                colors = ButtonDefaults.buttonColors(
                    containerColor = MPVerde
                ),

                shape = RoundedCornerShape(14.dp)

            ) {

                Text("Intentar nuevamente")
            }
        }
    }
}

// ======================================================
// FIN DEL ARCHIVO
// ECORECICLA MOBILE
// ======================================================