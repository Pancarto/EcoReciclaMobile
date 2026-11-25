package com.example.ecorecicla.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecorecicla.mobile.viewmodel.RecyclingViewModel

// Identidad visual EcoRecicla.
private val VerdeReciclaje = Color(0xFF166534)
private val VerdeSuave = Color(0xFFE5F2E8)
private val FondoReciclaje = Color(0xFFF2F7F3)
private val TextoReciclaje = Color(0xFF17251B)

// Pantalla funcional de reciclaje.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecyclingScreen(
    usuarioId: Long,
    onBackClick: () -> Unit = {},
    recyclingViewModel: RecyclingViewModel = viewModel()
) {

    // Observer: observar el estado del ViewModel.
    val estado by recyclingViewModel.uiState.collectAsState()

    var expandido by remember {
        mutableStateOf(false)
    }

    val formularioActivo =
        !estado.isLoading && !estado.registrado

    Scaffold(

        containerColor = FondoReciclaje,

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "Registrar reciclaje",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
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
                            color = VerdeReciclaje
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = VerdeReciclaje
                )
            )
        }

    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)

        ) {

            // Encabezado.
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(
                        VerdeReciclaje,
                        RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    "♻",
                    fontSize = 44.sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(
                "¡Recicla y gana!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextoReciclaje,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Registra tus materiales y contribuye al cuidado del planeta.",
                fontSize = 13.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(26.dp))

            // Formulario.
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Text(
                        "Información del material",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeReciclaje
                    )

                    Spacer(Modifier.height(22.dp))

                    Text(
                        "Tipo de material",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(8.dp))

                    // Materiales consultados desde SQLite.
                    ExposedDropdownMenuBox(
                        expanded = expandido,
                        onExpandedChange = {

                            if (formularioActivo) {
                                expandido = !expandido
                            }
                        }
                    ) {

                        OutlinedTextField(

                            value = estado.material?.nombre ?: "",

                            onValueChange = {},

                            readOnly = true,

                            enabled = formularioActivo,

                            singleLine = true,

                            placeholder = {
                                Text("Selecciona un material")
                            },

                            trailingIcon = {

                                ExposedDropdownMenuDefaults
                                    .TrailingIcon(
                                        expanded = expandido
                                    )
                            },

                            isError =
                                estado.errorMaterial != null,

                            shape = RoundedCornerShape(14.dp),

                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),

                            colors =
                                OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor =
                                        VerdeReciclaje
                                )
                        )

                        ExposedDropdownMenu(
                            expanded = expandido,
                            onDismissRequest = {
                                expandido = false
                            }
                        ) {

                            estado.materiales.forEach { material ->

                                DropdownMenuItem(

                                    text = {

                                        Column {

                                            Text(material.nombre)

                                            Text(
                                                "${material.puntosPorKg} puntos/kg",
                                                fontSize = 12.sp,
                                                color = VerdeReciclaje
                                            )
                                        }
                                    },

                                    onClick = {

                                        recyclingViewModel
                                            .seleccionarMaterial(
                                                material
                                            )

                                        expandido = false
                                    }
                                )
                            }
                        }
                    }

                    estado.errorMaterial?.let {

                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.height(22.dp))

                    Text(
                        "Peso del material",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(

                        value = estado.peso,

                        onValueChange =
                            recyclingViewModel::onPesoChange,

                        enabled = formularioActivo,

                        placeholder = {
                            Text("Ejemplo: 2.5")
                        },

                        suffix = {
                            Text("kg")
                        },

                        singleLine = true,

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),

                        isError = estado.errorPeso != null,

                        shape = RoundedCornerShape(14.dp),

                        modifier = Modifier.fillMaxWidth(),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VerdeReciclaje,
                            cursorColor = VerdeReciclaje
                        )
                    )

                    estado.errorPeso?.let {

                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Cálculo dinámico de puntos.
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = VerdeSuave
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                "★ Puntos por reciclaje",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdeReciclaje
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "${estado.puntosEstimados} puntos",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdeReciclaje
                            )

                            Text(
                                estado.material?.let {
                                    "${it.puntosPorKg} puntos por kilogramo"
                                } ?: "Selecciona un material e ingresa el peso.",
                                fontSize = 13.sp,
                                color = TextoReciclaje
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Registro real en la base de datos.
                    Button(

                        onClick = {

                            recyclingViewModel.registrar(
                                usuarioId
                            )
                        },

                        enabled = formularioActivo,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape = RoundedCornerShape(14.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeReciclaje,
                            contentColor = Color.White
                        )

                    ) {

                        if (estado.isLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )

                            Spacer(Modifier.width(10.dp))

                            Text("Procesando...")

                        } else {

                            Text(
                                if (estado.registrado)
                                    "Reciclaje registrado"
                                else
                                    "Registrar reciclaje",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Confirmación real de almacenamiento.
                    estado.mensaje?.let { mensaje ->

                        Spacer(Modifier.height(14.dp))

                        Text(
                            mensaje,
                            fontSize = 13.sp,
                            color = if (estado.registrado)
                                VerdeReciclaje
                            else
                                MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (
                        estado.materiales.isEmpty() &&
                        !estado.isLoading
                    ) {

                        TextButton(
                            onClick = {
                                recyclingViewModel.cargarMateriales()
                            }
                        ) {
                            Text("Reintentar cargar materiales")
                        }
                    }

                    // Acciones después del registro.
                    if (estado.registrado) {

                        Spacer(Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onBackClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Volver al inicio")
                        }

                        TextButton(
                            onClick = {
                                recyclingViewModel.nuevoReciclaje()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Registrar otro material")
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // Conciencia ambiental.
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = VerdeSuave
                )
            ) {

                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text("🌱", fontSize = 34.sp)

                    Spacer(Modifier.width(14.dp))

                    Column {

                        Text(
                            "Cada kilogramo cuenta",
                            fontWeight = FontWeight.Bold,
                            color = VerdeReciclaje,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            "Reciclar hoy es construir un mejor mañana.",
                            fontSize = 12.sp,
                            color = TextoReciclaje
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                "EcoRecicla Mobile · V1.0",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )
        }
    }
}