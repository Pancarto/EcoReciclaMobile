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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ecorecicla.mobile.ui.components.EcoCerrarSesionDialog
import java.text.NumberFormat
import java.util.Locale

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: ProfileScreen.kt
// RESPONSABILIDAD: Perfil personal del usuario.
// ARQUITECTURA: MVVM y State Hoisting.
// ======================================================

// Identidad visual.

private val PVerde = Color(0xFF166534)
private val PVerdeOscuro = Color(0xFF103D29)
private val PVerdeMedio = Color(0xFF23834D)
private val PVerdeClaro = Color(0xFFE4F2E8)
private val PFondo = Color(0xFFF4F7F5)
private val PTexto = Color(0xFF172B21)
private val PGris = Color(0xFF718078)
private val PBorde = Color(0xFFE0E8E2)
private val PRojo = Color(0xFFB91C1C)
private val PRojoClaro = Color(0xFFFDECEC)
private val PDorado = Color(0xFFEBC76C)

// ======================================================
// FUNCIONES AUXILIARES
// ======================================================

// Formato numérico colombiano.

private fun formatoPerfil(numero: Number): String {

    return NumberFormat
        .getNumberInstance(Locale("es", "CO"))
        .apply {
            maximumFractionDigits = 2
        }
        .format(numero)
}

// Obtener iniciales del nombre del usuario.

private fun inicialesPerfil(nombre: String): String {

    return nombre.trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .ifBlank { "EC" }
}

// ======================================================
// PANTALLA PRINCIPAL
// ======================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(

    nombreUsuario: String,

    correo: String,

    puntos: Number,

    reciclajes: Number,

    kilogramos: Number,

    onBackClick: () -> Unit = {},

    onMyRewardsClick: () -> Unit = {},

    onHistoryClick: () -> Unit = {},

    onRewardsClick: () -> Unit = {},

    onRecyclingClick: () -> Unit = {},

    onLogoutClick: () -> Unit = {}

) {

    var confirmarSalida by remember(nombreUsuario) {
        mutableStateOf(false)
    }

    var mostrarAcercaDe by remember {
        mutableStateOf(false)
    }

    Scaffold(

        containerColor = PFondo,

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            text = "EcoRecicla",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PVerde
                        )

                        Text(
                            text = "MI ESPACIO PERSONAL",
                            fontSize = 9.sp,
                            letterSpacing = 0.6.sp,
                            color = PGris
                        )
                    }
                },

                navigationIcon = {

                    TextButton(
                        onClick = onBackClick
                    ) {

                        Text(
                            text = "‹",
                            fontSize = 34.sp,
                            color = PVerde
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

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),

            verticalArrangement =
                Arrangement.spacedBy(18.dp)

        ) {

            // Encabezado de la pantalla.

            Column {

                Text(
                    text = "TU IDENTIDAD SOSTENIBLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = PVerdeMedio
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    text = "Mi perfil",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PVerdeOscuro
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    text = "Conoce tu impacto y administra tu experiencia EcoRecicla.",
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = PGris
                )
            }

            // Tarjeta principal del usuario.

            TarjetaIdentidadPerfil(
                nombre = nombreUsuario
            )

            // Estadísticas reales.

            Text(
                text = "Mi impacto ambiental",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PTexto
            )

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(9.dp)

            ) {

                EstadisticaPerfil(

                    titulo = "Puntos",

                    valor = formatoPerfil(puntos),

                    icono = "★",

                    modifier = Modifier.weight(1f)
                )

                EstadisticaPerfil(

                    titulo = "Reciclajes",

                    valor = formatoPerfil(reciclajes),

                    icono = "♻",

                    modifier = Modifier.weight(1f)
                )

                EstadisticaPerfil(

                    titulo = "Kg reciclados",

                    valor = formatoPerfil(kilogramos),

                    icono = "🌱",

                    modifier = Modifier.weight(1f)
                )
            }

            // Información personal.

            InformacionPersonalPerfil(

                nombre = nombreUsuario,

                correo = correo
            )

            // Accesos rápidos.

            Text(
                text = "Mi cuenta",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PTexto
            )

            Surface(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(24.dp),

                color = Color.White,

                shadowElevation = 1.dp

            ) {

                Column(
                    modifier = Modifier.padding(10.dp)
                ) {

                    OpcionPerfil(

                        icono = "🎟️",

                        titulo = "Mis premios",

                        descripcion =
                            "Consulta tus códigos ECO#",

                        onClick = onMyRewardsClick
                    )

                    HorizontalDivider(
                        color = PBorde
                    )

                    OpcionPerfil(

                        icono = "◷",

                        titulo = "Mi actividad",

                        descripcion =
                            "Historial de movimientos",

                        onClick = onHistoryClick
                    )

                    HorizontalDivider(
                        color = PBorde
                    )

                    OpcionPerfil(

                        icono = "🎁",

                        titulo = "Recompensas",

                        descripcion =
                            "Descubre nuevos premios",

                        onClick = onRewardsClick
                    )

                    HorizontalDivider(
                        color = PBorde
                    )

                    OpcionPerfil(

                        icono = "♻",

                        titulo = "Registrar reciclaje",

                        descripcion =
                            "Continúa generando impacto",

                        onClick = onRecyclingClick
                    )

                    HorizontalDivider(
                        color = PBorde
                    )

                    OpcionPerfil(

                        icono = "ⓘ",

                        titulo = "Acerca de EcoRecicla",

                        descripcion =
                            "Conoce nuestro propósito",

                        onClick = {
                            mostrarAcercaDe = true
                        }
                    )
                }
            }

            // Mensaje motivacional.

            MensajeAmbientalPerfil()

            // Cierre de sesión.

            Surface(

                modifier = Modifier.fillMaxWidth(),

                color = PRojoClaro,

                shape = RoundedCornerShape(21.dp),

                onClick = {

                    confirmarSalida = true
                }

            ) {

                Row(

                    modifier = Modifier.padding(19.dp),

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Box(

                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color.White,
                                RoundedCornerShape(15.dp)
                            ),

                        contentAlignment = Alignment.Center

                    ) {

                        Text(
                            text = "↪",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = PRojo
                        )
                    }

                    Spacer(Modifier.width(13.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Cerrar sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PRojo
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            text = "Tus datos permanecerán guardados.",
                            fontSize = 11.sp,
                            lineHeight = 17.sp,
                            color = PRojo
                        )
                    }

                    Text(
                        text = "›",
                        fontSize = 29.sp,
                        color = PRojo
                    )
                }
            }

            // Información institucional.

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {

                Text(
                    text = "♻ EcoRecicla Mobile",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PVerde
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Fundación Universitaria San Mateo",
                    fontSize = 11.sp,
                    color = PGris,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    text = "Proyecto académico · Versión 1.0",
                    fontSize = 10.sp,
                    color = PGris
                )
            }
        }
    }

    // ==================================================
    // CONFIRMACIÓN DE CIERRE DE SESIÓN
    // Reutiliza el componente existente.
    // ==================================================

    if (confirmarSalida) {

        EcoCerrarSesionDialog(

            nombre = nombreUsuario,

            onCancelar = {

                confirmarSalida = false
            },

            onConfirmar = {

                confirmarSalida = false

                onLogoutClick()
            }
        )
    }

    // Información acerca del proyecto.

    if (mostrarAcercaDe) {

        AcercaDeEcoReciclaDialog(

            onCerrar = {

                mostrarAcercaDe = false
            }
        )
    }
}

// ======================================================
// TARJETA PRINCIPAL DEL USUARIO
// ======================================================

@Composable
private fun TarjetaIdentidadPerfil(

    nombre: String

) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        PVerdeOscuro,
                        PVerde,
                        PVerdeMedio
                    )
                ),
                RoundedCornerShape(29.dp)
            )

    ) {

        // Decoración ambiental.

        Canvas(

            modifier = Modifier.matchParentSize()

        ) {

            drawCircle(

                color = Color.White.copy(alpha = 0.05f),

                radius = size.width * 0.36f,

                center = androidx.compose.ui.geometry.Offset(
                    size.width * 0.95f,
                    size.height * 0.10f
                )
            )

            drawCircle(

                color = Color.White.copy(alpha = 0.05f),

                radius = size.width * 0.30f,

                center = androidx.compose.ui.geometry.Offset(
                    size.width * 0.05f,
                    size.height * 0.95f
                )
            )
        }

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(27.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            // Avatar con iniciales reales.

            Box(

                modifier = Modifier
                    .size(102.dp)
                    .background(
                        Color.White.copy(alpha = 0.18f),
                        CircleShape
                    ),

                contentAlignment = Alignment.Center

            ) {

                Box(

                    modifier = Modifier
                        .size(85.dp)
                        .background(
                            Color.White,
                            CircleShape
                        ),

                    contentAlignment = Alignment.Center

                ) {

                    Text(

                        text = inicialesPerfil(nombre),

                        fontSize = 31.sp,

                        fontWeight = FontWeight.ExtraBold,

                        color = PVerde
                    )
                }

                Box(

                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(29.dp)
                        .background(
                            PDorado,
                            CircleShape
                        ),

                    contentAlignment = Alignment.Center

                ) {

                    Text(
                        text = "✓",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PVerdeOscuro
                    )
                }
            }

            Spacer(Modifier.height(17.dp))

            Text(

                text = nombre,

                fontSize = 24.sp,

                lineHeight = 30.sp,

                fontWeight = FontWeight.ExtraBold,

                color = Color.White,

                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text = "Miembro de la comunidad EcoRecicla",
                fontSize = 12.sp,
                color = Color(0xFFCBEDD3),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(17.dp))

            Surface(

                color = Color.White.copy(alpha = 0.15f),

                shape = RoundedCornerShape(30.dp)

            ) {

                Text(

                    text = "🌱 Agente del cambio",

                    modifier = Modifier.padding(
                        horizontal = 17.dp,
                        vertical = 9.dp
                    ),

                    fontSize = 12.sp,

                    fontWeight = FontWeight.Bold,

                    color = Color.White
                )
            }
        }
    }
}

// ======================================================
// TARJETAS DE ESTADÍSTICAS
// ======================================================

@Composable
private fun EstadisticaPerfil(

    titulo: String,

    valor: String,

    icono: String,

    modifier: Modifier = Modifier

) {

    Surface(

        modifier = modifier,

        color = Color.White,

        shape = RoundedCornerShape(21.dp),

        shadowElevation = 1.dp

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 17.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Box(

                modifier = Modifier
                    .size(43.dp)
                    .background(
                        PVerdeClaro,
                        RoundedCornerShape(13.dp)
                    ),

                contentAlignment = Alignment.Center

            ) {

                Text(
                    text = icono,
                    fontSize = 25.sp,
                    color = PVerde
                )
            }

            Spacer(Modifier.height(11.dp))

            Text(

                text = valor,

                fontSize = 23.sp,

                fontWeight = FontWeight.ExtraBold,

                color = PVerde,

                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(5.dp))

            Text(

                text = titulo,

                fontSize = 10.sp,

                color = PGris,

                textAlign = TextAlign.Center
            )
        }
    }
}

// ======================================================
// INFORMACIÓN PERSONAL
// ======================================================

@Composable
private fun InformacionPersonalPerfil(

    nombre: String,

    correo: String

) {

    Surface(

        modifier = Modifier.fillMaxWidth(),

        color = Color.White,

        shape = RoundedCornerShape(24.dp),

        shadowElevation = 1.dp

    ) {

        Column(
            modifier = Modifier.padding(19.dp)
        ) {

            Text(
                text = "Información personal",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PTexto
            )

            Spacer(Modifier.height(17.dp))

            FilaInformacionPerfil(

                icono = "👤",

                titulo = "Nombre completo",

                valor = nombre
            )

            Spacer(Modifier.height(13.dp))

            HorizontalDivider(
                color = PBorde
            )

            Spacer(Modifier.height(13.dp))

            FilaInformacionPerfil(

                icono = "✉",

                titulo = "Correo electrónico",

                valor = correo
            )

            Spacer(Modifier.height(13.dp))

            HorizontalDivider(
                color = PBorde
            )

            Spacer(Modifier.height(13.dp))

            FilaInformacionPerfil(

                icono = "♻",

                titulo = "Tipo de cuenta",

                valor = "Usuario EcoRecicla"
            )
        }
    }
}

@Composable
private fun FilaInformacionPerfil(

    icono: String,

    titulo: String,

    valor: String

) {

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically

    ) {

        Box(

            modifier = Modifier
                .size(44.dp)
                .background(
                    PVerdeClaro,
                    RoundedCornerShape(13.dp)
                ),

            contentAlignment = Alignment.Center

        ) {

            Text(
                text = icono,
                fontSize = 23.sp,
                color = PVerde
            )
        }

        Spacer(Modifier.width(13.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = titulo,
                fontSize = 11.sp,
                color = PGris
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = valor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PTexto
            )
        }
    }
}

// ======================================================
// OPCIONES DE NAVEGACIÓN
// ======================================================

@Composable
private fun OpcionPerfil(

    icono: String,

    titulo: String,

    descripcion: String,

    onClick: () -> Unit

) {

    Surface(

        onClick = onClick,

        modifier = Modifier.fillMaxWidth(),

        color = Color.White,

        shape = RoundedCornerShape(15.dp)

    ) {

        Row(

            modifier = Modifier.padding(12.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Box(

                modifier = Modifier
                    .size(47.dp)
                    .background(
                        PVerdeClaro,
                        RoundedCornerShape(14.dp)
                    ),

                contentAlignment = Alignment.Center

            ) {

                Text(
                    text = icono,
                    fontSize = 25.sp,
                    color = PVerde
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = titulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PTexto
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    text = descripcion,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = PGris
                )
            }

            Spacer(Modifier.width(7.dp))

            Text(
                text = "›",
                fontSize = 28.sp,
                color = PVerde
            )
        }
    }
}

// ======================================================
// MENSAJE AMBIENTAL
// ======================================================

@Composable
private fun MensajeAmbientalPerfil() {

    Surface(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        color = PVerdeClaro

    ) {

        Row(

            modifier = Modifier.padding(19.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Text(
                text = "🌎",
                fontSize = 43.sp
            )

            Spacer(Modifier.width(15.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "El cambio comienza contigo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PVerde
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    text = "Cada material que reciclas contribuye a construir una comunidad más sostenible.",
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                    color = PTexto
                )
            }
        }
    }
}

// ======================================================
// VENTANA ACERCA DE ECORECICLA
// ======================================================

@Composable
private fun AcercaDeEcoReciclaDialog(

    onCerrar: () -> Unit

) {

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
                    .heightIn(max = 700.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(25.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {

                Box(

                    modifier = Modifier
                        .size(91.dp)
                        .background(
                            PVerdeClaro,
                            CircleShape
                        ),

                    contentAlignment = Alignment.Center

                ) {

                    Text(
                        text = "♻",
                        fontSize = 53.sp,
                        color = PVerde
                    )
                }

                Spacer(Modifier.height(17.dp))

                Text(
                    text = "EcoRecicla",
                    fontSize = 29.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PVerde
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    text = "Recicla, gana y transforma.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = PGris
                )

                Spacer(Modifier.height(20.dp))

                Text(

                    text = "EcoRecicla es una iniciativa tecnológica que promueve el reciclaje mediante un sistema de incentivos y recompensas, motivando a las personas a participar activamente en el cuidado del medio ambiente.",

                    fontSize = 13.sp,

                    lineHeight = 21.sp,

                    color = PTexto,

                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(18.dp))

                Surface(

                    modifier = Modifier.fillMaxWidth(),

                    color = PFondo,

                    shape = RoundedCornerShape(18.dp)

                ) {

                    Column(
                        modifier = Modifier.padding(17.dp)
                    ) {

                        FilaAcercaDe(
                            titulo = "Aplicación",
                            valor = "EcoRecicla Mobile"
                        )

                        HorizontalDivider(
                            color = PBorde
                        )

                        FilaAcercaDe(
                            titulo = "Versión",
                            valor = "1.0"
                        )

                        HorizontalDivider(
                            color = PBorde
                        )

                        FilaAcercaDe(
                            titulo = "Tecnología",
                            valor = "Kotlin y Jetpack Compose"
                        )

                        HorizontalDivider(
                            color = PBorde
                        )

                        FilaAcercaDe(
                            titulo = "Base de datos",
                            valor = "SQLite"
                        )

                        HorizontalDivider(
                            color = PBorde
                        )

                        FilaAcercaDe(
                            titulo = "Arquitectura",
                            valor = "MVVM"
                        )

                        HorizontalDivider(
                            color = PBorde
                        )

                        FilaAcercaDe(
                            titulo = "Institución del piloto académico",
                            valor = "Fundación Universitaria San Mateo"
                        )
                    }
                }

                Spacer(Modifier.height(19.dp))

                Text(

                    text = "Desarrollado como proyecto académico de Ingeniería de Sistemas, con el propósito de promover hábitos sostenibles.",

                    fontSize = 12.sp,

                    lineHeight = 19.sp,

                    color = PGris,

                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(21.dp))

                Button(

                    onClick = onCerrar,

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),

                    shape = RoundedCornerShape(15.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = PVerde
                    )

                ) {

                    Text(
                        text = "Continuar",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaAcercaDe(

    titulo: String,

    valor: String

) {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)

    ) {

        Text(
            text = titulo,
            fontSize = 11.sp,
            color = PGris
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PVerde
        )
    }
}

// ======================================================
// FIN DEL ARCHIVO
// ======================================================