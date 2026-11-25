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
import com.example.ecorecicla.mobile.data.repository.RecompensaLocal
import com.example.ecorecicla.mobile.viewmodel.RewardsViewModel

private val VerdePremios = Color(0xFF166534)
private val FondoPremios = Color(0xFFF2F7F3)
private val VerdeClaroPremios = Color(0xFFE5F2E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    usuarioId: Long,
    onBackClick: () -> Unit = {},
    rewardsViewModel: RewardsViewModel = viewModel()
) {

    val estado by rewardsViewModel.uiState.collectAsState()

    var seleccionada by remember {
        mutableStateOf<RecompensaLocal?>(null)
    }

    LaunchedEffect(usuarioId) {
        rewardsViewModel.cargarCatalogo(usuarioId)
    }

    Scaffold(

        containerColor = FondoPremios,

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "Recompensas",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    TextButton(
                        onClick = onBackClick,
                        enabled = !estado.isLoading
                    ) {
                        Text(
                            "‹",
                            fontSize = 34.sp,
                            color = VerdePremios
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = VerdePremios
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
                Arrangement.spacedBy(16.dp)

        ) {

            item {

                Text(
                    "🎁 Premia tu esfuerzo",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdePremios
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Convierte tus acciones sostenibles en recompensas.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            // Saldo real.
            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = VerdePremios
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {

                        Text(
                            "Tu saldo disponible",
                            color = Color.White,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "${estado.saldo} puntos",
                            color = Color.White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
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
                            color = VerdePremios
                        )
                    }
                }
            }

            estado.mensaje?.let { mensaje ->

                item {

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = VerdeClaroPremios
                        )
                    ) {

                        Text(
                            mensaje,
                            modifier = Modifier.padding(18.dp),
                            color = VerdePremios,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            estado.error?.let { error ->

                item {

                    Column {

                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error
                        )

                        TextButton(
                            onClick = {
                                rewardsViewModel
                                    .cargarCatalogo(usuarioId)
                            }
                        ) {
                            Text("Actualizar catálogo")
                        }
                    }
                }
            }

            item {

                Text(
                    "Catálogo de recompensas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Catálogo consultado desde SQLite.
            items(
                items = estado.recompensas,
                key = { it.id }
            ) { recompensa ->

                val puedeCanjear =
                    estado.saldo >= recompensa.costoPuntos &&
                            recompensa.stock > 0 &&
                            !estado.isLoading &&
                            estado.error == null

                Card(

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(20.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )

                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                when (recompensa.id) {
                                    1L -> "📓"
                                    2L -> "🛍️"
                                    3L -> "💧"
                                    else -> "🌱"
                                },
                                fontSize = 38.sp
                            )

                            Spacer(Modifier.width(16.dp))

                            Column {

                                Text(
                                    recompensa.nombre,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    recompensa.descripcion,
                                    fontSize = 13.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        Text(
                            "${recompensa.costoPuntos} puntos",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = VerdePremios
                        )

                        Text(
                            "Disponibles: ${recompensa.stock}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(

                            onClick = {
                                seleccionada = recompensa
                            },

                            enabled = puedeCanjear,

                            modifier = Modifier.fillMaxWidth(),

                            shape = RoundedCornerShape(12.dp),

                            colors = ButtonDefaults.buttonColors(
                                containerColor = VerdePremios
                            )

                        ) {

                            Text(
                                when {
                                    recompensa.stock == 0 ->
                                        "Agotado"

                                    estado.saldo <
                                            recompensa.costoPuntos ->
                                        "Puntos insuficientes"

                                    else -> "Canjear recompensa"
                                }
                            )
                        }
                    }
                }
            }

            item {

                Text(
                    "EcoRecicla Mobile · V1.0",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }

    // Confirmación antes de descontar puntos.
    seleccionada?.let { recompensa ->

        AlertDialog(

            onDismissRequest = {
                seleccionada = null
            },

            title = {
                Text("Confirmar canje")
            },

            text = {

                Column {

                    Text(
                        "¿Deseas canjear ${recompensa.nombre}?"
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Costo: ${recompensa.costoPuntos} puntos"
                    )

                    Text(
                        "Saldo restante: ${
                            estado.saldo - recompensa.costoPuntos
                        } puntos"
                    )
                }
            },

            confirmButton = {

                Button(

                    onClick = {

                        seleccionada = null

                        rewardsViewModel.canjear(
                            usuarioId,
                            recompensa.id
                        )
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdePremios
                    )

                ) {
                    Text("Confirmar")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        seleccionada = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}