package com.example.ecorecicla.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecorecicla.mobile.viewmodel.RewardsViewModel

private val VerdeHistorial = Color(0xFF166534)
private val FondoHistorial = Color(0xFFF2F7F3)
private val RojoHistorial = Color(0xFFB91C1C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    usuarioId: Long,
    onBackClick: () -> Unit = {},
    rewardsViewModel: RewardsViewModel = viewModel()
) {

    val estado by rewardsViewModel.uiState.collectAsState()

    var filtro by remember {
        mutableStateOf("Todos")
    }

    LaunchedEffect(usuarioId) {

        rewardsViewModel.cargarHistorial(usuarioId)
    }

    val movimientos = estado.movimientos.filter {

        filtro == "Todos" || it.tipo == filtro
    }

    Scaffold(

        containerColor = FondoHistorial,

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        "Mi historial",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    TextButton(onClick = onBackClick) {

                        Text(
                            "‹",
                            fontSize = 34.sp,
                            color = VerdeHistorial
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = VerdeHistorial
                )
            )
        }

    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding = PaddingValues(20.dp),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)

        ) {

            item {

                Text(
                    "Tu actividad sostenible",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeHistorial
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Consulta tus reciclajes y recompensas canjeadas.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            // Filtros de movimientos.
            item {

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    listOf(
                        "Todos",
                        "Reciclaje",
                        "Canje"
                    ).forEach { opcion ->

                        FilterChip(

                            selected = filtro == opcion,

                            onClick = {
                                filtro = opcion
                            },

                            label = {
                                Text(opcion)
                            }
                        )
                    }
                }
            }

            if (estado.isLoading) {

                item {

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        CircularProgressIndicator(
                            color = VerdeHistorial
                        )
                    }
                }

            } else if (estado.error != null) {

                item {

                    Text(
                        estado.error.orEmpty(),
                        color = RojoHistorial
                    )

                    TextButton(
                        onClick = {
                            rewardsViewModel
                                .cargarHistorial(usuarioId)
                        }
                    ) {
                        Text("Reintentar")
                    }
                }

            } else if (movimientos.isEmpty()) {

                item {

                    Card(

                        modifier = Modifier.fillMaxWidth(),

                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )

                    ) {

                        Column(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally

                        ) {

                            Text("📋", fontSize = 42.sp)

                            Spacer(Modifier.height(12.dp))

                            Text(
                                "No hay movimientos",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Tus operaciones aparecerán aquí.",
                                fontSize = 13.sp
                            )
                        }
                    }
                }

            } else {

                items(movimientos) { movimiento ->

                    val esReciclaje =
                        movimiento.tipo == "Reciclaje"

                    Card(

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(18.dp),

                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),

                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )

                    ) {

                        Row(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),

                            verticalAlignment =
                                Alignment.CenterVertically

                        ) {

                            Text(
                                if (esReciclaje) "♻️" else "🎁",
                                fontSize = 30.sp
                            )

                            Spacer(Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    movimiento.tipo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                Text(
                                    movimiento.descripcion,
                                    fontSize = 13.sp
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    "${movimiento.fecha} UTC",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }

                            Text(

                                if (esReciclaje)
                                    "+${movimiento.puntos}"
                                else
                                    "${movimiento.puntos}",

                                color = if (esReciclaje)
                                    VerdeHistorial
                                else
                                    RojoHistorial,

                                fontSize = 20.sp,

                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}