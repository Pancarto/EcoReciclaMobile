package com.example.ecorecicla.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: DashboardScreen.kt
// RESPONSABILIDAD: Pantalla principal del usuario.
// ======================================================

// Identidad visual de EcoRecicla.

private val VerdePrincipal = Color(0xFF166534)
private val VerdeClaro = Color(0xFFE5F2E8)
private val FondoDashboard = Color(0xFFF2F7F3)
private val TextoOscuro = Color(0xFF17251B)

@Composable
fun DashboardScreen(

    // Datos provisionales para pruebas visuales.
    nombreUsuario: String = "Usuario de prueba",
    puntos: Int = 0,
    kilogramos: Double = 0.0,
    reciclajes: Int = 0,

    // Eventos preparados para Navigation Compose.
    onRecyclingClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onPointsClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}

) {

    Scaffold(

        containerColor = FondoDashboard,

        // ==============================================
        // BARRA DE NAVEGACIÓN INFERIOR
        // ==============================================

        bottomBar = {

            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 4.dp
            ) {

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Text("⌂", fontSize = 26.sp)
                    },
                    label = {
                        Text("Inicio")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = VerdeClaro,
                        selectedIconColor = VerdePrincipal,
                        selectedTextColor = VerdePrincipal
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onRecyclingClick,
                    icon = {
                        Text("♻", fontSize = 24.sp)
                    },
                    label = {
                        Text("Reciclar")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onRewardsClick,
                    icon = {
                        Text("🎁", fontSize = 22.sp)
                    },
                    label = {
                        Text("Premios")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,
                    icon = {
                        Text("👤", fontSize = 22.sp)
                    },
                    label = {
                        Text("Perfil")
                    }
                )
            }
        }

    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)

        ) {

            // ==========================================
            // ENCABEZADO PRINCIPAL
            // ==========================================

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically

            ) {

                Box(

                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            VerdePrincipal,
                            RoundedCornerShape(14.dp)
                        ),

                    contentAlignment = Alignment.Center

                ) {

                    Text(
                        text = "♻",
                        color = Color.White,
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "EcoRecicla",
                        color = VerdePrincipal,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Recicla · Gana · Transforma",
                        color = Color.DarkGray,
                        fontSize = 11.sp
                    )
                }

                TextButton(
                    onClick = onProfileClick
                ) {
                    Text("👤", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ==========================================
            // SALUDO PERSONALIZADO
            // ==========================================

            Text(
                text = "¡Hola, $nombreUsuario!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Hoy es un buen día para cuidar el planeta.",
                fontSize = 13.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // TARJETA DE PUNTOS ACUMULADOS
            // ==========================================

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(24.dp),

                colors = CardDefaults.cardColors(
                    containerColor = VerdePrincipal
                )

            ) {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(
                        text = "Tu saldo disponible",
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {

                        Text(
                            text = puntos.toString(),
                            color = Color.White,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "puntos",
                            color = Color.White,
                            fontSize = 17.sp,
                            modifier = Modifier.padding(bottom = 7.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cada material reciclado suma.",
                        color = Color(0xFFD1FAE5),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(

                        onClick = onRewardsClick,

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = VerdePrincipal
                        ),

                        shape = RoundedCornerShape(12.dp)

                    ) {

                        Text(
                            text = "Ver recompensas",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ==========================================
            // ESTADÍSTICAS DEL USUARIO
            // ==========================================

            Text(
                text = "Tu impacto ambiental",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(12.dp)

            ) {

                StatisticCard(
                    modifier = Modifier.weight(1f),
                    icono = "🌱",
                    valor = "$kilogramos kg",
                    titulo = "Material reciclado"
                )

                StatisticCard(
                    modifier = Modifier.weight(1f),
                    icono = "♻",
                    valor = reciclajes.toString(),
                    titulo = "Reciclajes realizados"
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ==========================================
            // ACCESOS RÁPIDOS
            // ==========================================

            Text(
                text = "¿Qué quieres hacer hoy?",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(12.dp)

            ) {

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icono = "♻",
                    titulo = "Reciclar",
                    descripcion = "Registra tus materiales",
                    onClick = onRecyclingClick
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icono = "🎁",
                    titulo = "Recompensas",
                    descripcion = "Canjea tus puntos",
                    onClick = onRewardsClick
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(12.dp)

            ) {

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icono = "★",
                    titulo = "Mis puntos",
                    descripcion = "Consulta tu saldo",
                    onClick = onPointsClick
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icono = "▤",
                    titulo = "Historial",
                    descripcion = "Consulta tus canjes",
                    onClick = onHistoryClick
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // MENSAJE DE CONCIENCIA AMBIENTAL
            // ==========================================

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(18.dp),

                colors = CardDefaults.cardColors(
                    containerColor = VerdeClaro
                )

            ) {

                Row(

                    modifier = Modifier.padding(18.dp),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(
                        text = "🌎",
                        fontSize = 34.sp
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {

                        Text(
                            text = "Pequeñas acciones, grandes cambios",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = VerdePrincipal
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Juntos construimos un futuro más sostenible.",
                            fontSize = 12.sp,
                            color = TextoOscuro
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "EcoRecicla Mobile · V1.0",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )
        }
    }
}

// ======================================================
// COMPONENTE REUTILIZABLE: TARJETA DE ESTADÍSTICAS
// ======================================================

@Composable
private fun StatisticCard(

    modifier: Modifier = Modifier,
    icono: String,
    valor: String,
    titulo: String

) {

    Card(

        modifier = modifier,

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            Text(
                text = icono,
                fontSize = 27.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = valor,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = VerdePrincipal
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = titulo,
                fontSize = 11.sp,
                color = Color.DarkGray
            )
        }
    }
}

// ======================================================
// COMPONENTE REUTILIZABLE: TARJETA DE ACCESO RÁPIDO
// ======================================================

@Composable
private fun QuickActionCard(

    modifier: Modifier = Modifier,
    icono: String,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit

) {

    Card(

        onClick = onClick,

        modifier = modifier.height(142.dp),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )

    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Box(

                modifier = Modifier
                    .size(40.dp)
                    .background(
                        VerdeClaro,
                        RoundedCornerShape(12.dp)
                    ),

                contentAlignment = Alignment.Center

            ) {

                Text(
                    text = icono,
                    fontSize = 23.sp,
                    color = VerdePrincipal
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = descripcion,
                fontSize = 11.sp,
                color = Color.DarkGray
            )
        }
    }
}
// ======================================================
// VISTA PREVIA DEL DASHBOARD
// RESPONSABILIDAD: Pruebas visuales en Android Studio.
// No modifica el flujo de autenticación.
// ======================================================

@androidx.compose.ui.tooling.preview.Preview(
    name = "Dashboard EcoRecicla",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DashboardScreenPreview() {

    com.example.ecorecicla.mobile.ui.theme.EcoReciclaMobileTheme {

        DashboardScreen(
            nombreUsuario = "Usuario de prueba",
            puntos = 1250,
            kilogramos = 18.5,
            reciclajes = 12
        )
    }
}
// ======================================================
// CONTENEDOR DEL DASHBOARD
// ARQUITECTURA: MVVM
// PATRÓN DE COMPORTAMIENTO: OBSERVER
// RESPONSABILIDAD: Conectar el ViewModel con la interfaz.
// ======================================================

@Composable
fun DashboardRoute(

    dashboardViewModel:
    com.example.ecorecicla.mobile.viewmodel.DashboardViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel(),

    onRecyclingClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onPointsClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}

) {

    // Observación reactiva del estado.
    val uiState = dashboardViewModel.uiState.collectAsState().value

    // Enviar los datos y eventos a la interfaz gráfica.
    DashboardScreen(

        nombreUsuario = uiState.nombreUsuario,
        puntos = uiState.puntos,
        kilogramos = uiState.kilogramos,
        reciclajes = uiState.reciclajes,

        onRecyclingClick = onRecyclingClick,
        onRewardsClick = onRewardsClick,
        onPointsClick = onPointsClick,
        onHistoryClick = onHistoryClick,
        onProfileClick = onProfileClick
    )
}