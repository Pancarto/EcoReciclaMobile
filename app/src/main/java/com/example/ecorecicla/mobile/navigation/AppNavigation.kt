package com.example.ecorecicla.mobile.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecorecicla.mobile.data.local.UsuarioLocal
import com.example.ecorecicla.mobile.data.repository.AuthRepository
import com.example.ecorecicla.mobile.data.repository.RecyclingRepository
import com.example.ecorecicla.mobile.data.repository.EstadisticasReciclaje
import com.example.ecorecicla.mobile.ui.screens.*
import com.example.ecorecicla.mobile.viewmodel.AuthViewModel
import com.example.ecorecicla.mobile.viewmodel.RegisterViewModel
import kotlinx.coroutines.CancellationException

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val context = LocalContext.current

    val repository = remember {
        AuthRepository(context.applicationContext)
    }

    val recyclingRepository = remember {
        RecyclingRepository(context.applicationContext)
    }

    // Usuario autenticado durante la sesión.
    var usuario by remember {
        mutableStateOf<UsuarioLocal?>(null)
    }

    // Cerrar sesión y limpiar la navegación.
    fun cerrarSesion() {

        usuario = null

        navController.navigate(Routes.Login.route) {

            popUpTo(navController.graph.id) {
                inclusive = true
            }

            launchSingleTop = true
        }
    }

    NavHost(

        navController = navController,

        startDestination = Routes.Login.route

    ) {

        // INICIO DE SESIÓN.
        composable(Routes.Login.route) {

            val authViewModel: AuthViewModel = viewModel()

            val estado by authViewModel.uiState.collectAsState()

            LaunchedEffect(
                estado.isAuthenticated,
                estado.usuarioId
            ) {

                val id = estado.usuarioId

                if (estado.isAuthenticated && id != null) {

                    val cuenta =
                        repository.obtenerUsuario(id)

                    if (cuenta != null) {

                        usuario = cuenta

                        navController.navigate(
                            Routes.Dashboard.route
                        ) {

                            popUpTo(Routes.Login.route) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                }
            }

            LoginScreen(

                authViewModel = authViewModel,

                onRegister = {

                    navController.navigate(
                        Routes.Register.route
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // REGISTRO DE USUARIOS.
        composable(Routes.Register.route) {

            val registerViewModel: RegisterViewModel =
                viewModel()

            val estado by
            registerViewModel.uiState.collectAsState()

            LaunchedEffect(estado.isRegistered) {

                if (estado.isRegistered) {

                    navController.popBackStack()
                }
            }

            RegisterScreen(

                registerViewModel = registerViewModel,

                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // DASHBOARD PRINCIPAL.
        composable(Routes.Dashboard.route) {

            val cuenta = usuario

            if (cuenta == null) {

                LaunchedEffect(Unit) {
                    cerrarSesion()
                }

            } else {

                var estadisticas by remember(cuenta.id) {
                    mutableStateOf<EstadisticasReciclaje?>(null)
                }

                var error by remember {
                    mutableStateOf<String?>(null)
                }

                var reintento by remember {
                    mutableIntStateOf(0)
                }

                var mostrarPerfil by remember {
                    mutableStateOf(false)
                }

                // Consultar datos reales desde SQLite.
                LaunchedEffect(cuenta.id, reintento) {

                    error = null

                    try {

                        estadisticas =
                            recyclingRepository
                                .obtenerEstadisticas(cuenta.id)

                    } catch (e: CancellationException) {

                        throw e

                    } catch (e: Exception) {

                        error =
                            "No se pudieron cargar las estadísticas"
                    }
                }

                val datos = estadisticas

                if (error != null) {

                    Column(

                        modifier = Modifier.fillMaxSize(),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.Center

                    ) {

                        Text(error.orEmpty())

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                reintento++
                            }
                        ) {
                            Text("Reintentar")
                        }
                    }

                } else if (datos == null) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                } else {

                    DashboardScreen(

                        nombreUsuario = cuenta.nombre,

                        puntos = datos.puntos,

                        kilogramos = datos.kilogramos,

                        reciclajes = datos.reciclajes,

                        onRecyclingClick = {

                            navController.navigate(
                                Routes.Recycling.route
                            ) {
                                launchSingleTop = true
                            }
                        },

                        onRewardsClick = {

                            navController.navigate(
                                Routes.Rewards.route
                            ) {
                                launchSingleTop = true
                            }
                        },

                        onPointsClick = {

                            navController.navigate(
                                Routes.History.route
                            ) {
                                launchSingleTop = true
                            }
                        },

                        onHistoryClick = {

                            navController.navigate(
                                Routes.History.route
                            ) {
                                launchSingleTop = true
                            }
                        },

                        onProfileClick = {
                            mostrarPerfil = true
                        }
                    )
                }

                // Perfil y cierre de sesión.
                if (mostrarPerfil) {

                    AlertDialog(

                        onDismissRequest = {
                            mostrarPerfil = false
                        },

                        title = {
                            Text("Mi perfil")
                        },

                        text = {

                            Column {

                                Text("Nombre: ${cuenta.nombre}")

                                Spacer(Modifier.height(10.dp))

                                Text("Correo: ${cuenta.correo}")

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    "Saldo: ${datos?.puntos ?: 0} puntos"
                                )

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    "Reciclajes: ${datos?.reciclajes ?: 0}"
                                )

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    "Material reciclado: ${
                                        datos?.kilogramos ?: 0.0
                                    } kg"
                                )
                            }
                        },

                        confirmButton = {

                            TextButton(

                                onClick = {

                                    mostrarPerfil = false

                                    cerrarSesion()
                                }

                            ) {
                                Text("Cerrar sesión")
                            }
                        },

                        dismissButton = {

                            TextButton(
                                onClick = {
                                    mostrarPerfil = false
                                }
                            ) {
                                Text("Volver")
                            }
                        }
                    )
                }
            }
        }

        // REGISTRO DE RECICLAJE.
        composable(Routes.Recycling.route) {

            val cuenta = usuario

            if (cuenta == null) {

                LaunchedEffect(Unit) {
                    cerrarSesion()
                }

            } else {

                RecyclingScreen(

                    usuarioId = cuenta.id,

                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // CATÁLOGO Y CANJES.
        composable(Routes.Rewards.route) {

            val cuenta = usuario

            if (cuenta == null) {

                LaunchedEffect(Unit) {
                    cerrarSesion()
                }

            } else {

                RewardsScreen(

                    usuarioId = cuenta.id,

                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // HISTORIAL DE OPERACIONES.
        composable(Routes.History.route) {

            val cuenta = usuario

            if (cuenta == null) {

                LaunchedEffect(Unit) {
                    cerrarSesion()
                }

            } else {

                HistoryScreen(

                    usuarioId = cuenta.id,

                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}