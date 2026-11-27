package com.example.ecorecicla.mobile.navigation

import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ecorecicla.mobile.data.local.UsuarioLocal
import com.example.ecorecicla.mobile.data.repository.AuthRepository
import com.example.ecorecicla.mobile.data.repository.RecyclingRepository
import com.example.ecorecicla.mobile.data.repository.EstadisticasReciclaje
import com.example.ecorecicla.mobile.ui.components.*
import com.example.ecorecicla.mobile.ui.screens.*
import com.example.ecorecicla.mobile.viewmodel.AuthViewModel
import com.example.ecorecicla.mobile.viewmodel.RegisterViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: AppNavigation.kt
// RESPONSABILIDAD: Navegación y gestión de sesión.
// ARQUITECTURA: MVVM.
// PATRONES: Observer, Repository y State Hoisting.
// ======================================================

// Estado observable de la sesión.

data class SesionUiState(
    val usuario: UsuarioLocal? = null,
    val verificando: Boolean = true,
    val cerrandoSesion: Boolean = false,
    val mostrandoDespedida: Boolean = false,
    val error: String? = null,
    val varianteBienvenida: Int = 0,
    val nombreBienvenida: String? = null,
    val varianteDespedida: Int = 0,
    val nombreDespedida: String? = null
)

// ======================================================
// VIEWMODEL DE SESIÓN
// ======================================================

class SesionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        AuthRepository(application)

    private val preferenciasVisuales =
        application.getSharedPreferences(
            "ecorecicla_bienvenida",
            Context.MODE_PRIVATE
        )

    private val _estado =
        MutableStateFlow(SesionUiState())

    val estado = _estado.asStateFlow()

    init {
        restaurarSesion()
    }

    // Seleccionar una ilustración diferente a la anterior.

    private fun seleccionarVariante(
        clave: String
    ): Int {

        val anterior =
            preferenciasVisuales.getInt(clave, -1)

        val nueva = (0..5)
            .filter { it != anterior }
            .random(Random.Default)

        preferenciasVisuales.edit()
            .putInt(clave, nueva)
            .apply()

        return nueva
    }

    // ==================================================
    // RECUPERAR SESIÓN AUTOMÁTICAMENTE
    // ==================================================

    fun restaurarSesion() {

        if (
            _estado.value.cerrandoSesion ||
            _estado.value.mostrandoDespedida
        ) return

        val variante =
            seleccionarVariante("ultima_ilustracion")

        _estado.update {
            it.copy(
                verificando = true,
                error = null,
                varianteBienvenida = variante,
                nombreBienvenida = null
            )
        }

        viewModelScope.launch {

            try {

                val usuario =
                    repository.recuperarSesion()

                _estado.update {
                    it.copy(
                        nombreBienvenida = usuario?.nombre
                    )
                }

                delay(DURACION_BIENVENIDA)

                _estado.value = SesionUiState(
                    usuario = usuario,
                    verificando = false
                )

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _estado.value = SesionUiState(
                    verificando = false,
                    error = "No se pudo verificar la sesión guardada."
                )
            }
        }
    }

    // ==================================================
    // INICIO DE SESIÓN MANUAL
    // ==================================================

    fun iniciarSesion(usuario: UsuarioLocal) {

        if (
            _estado.value.verificando ||
            _estado.value.cerrandoSesion ||
            _estado.value.mostrandoDespedida
        ) return

        val variante =
            seleccionarVariante("ultima_ilustracion")

        _estado.value = SesionUiState(
            usuario = usuario,
            verificando = true,
            varianteBienvenida = variante,
            nombreBienvenida = usuario.nombre
        )

        viewModelScope.launch {

            delay(DURACION_BIENVENIDA)

            _estado.value = SesionUiState(
                usuario = usuario,
                verificando = false
            )
        }
    }

    // ==================================================
    // CERRAR SESIÓN
    // No elimina puntos, reciclajes ni recompensas.
    // ==================================================

    fun cerrarSesion() {

        val actual = _estado.value

        if (
            actual.cerrandoSesion ||
            actual.mostrandoDespedida ||
            actual.verificando
        ) return

        val nombre = actual.usuario?.nombre

        val teniaUsuario = actual.usuario != null

        _estado.update {
            it.copy(
                cerrandoSesion = true,
                error = null
            )
        }

        viewModelScope.launch {

            try {

                val eliminada =
                    repository.eliminarSesion()

                if (eliminada) {

                    // Mostrar despedida personalizada.

                    if (teniaUsuario) {

                        val variante =
                            seleccionarVariante(
                                "ultima_despedida"
                            )

                        _estado.value = SesionUiState(
                            usuario = null,
                            verificando = false,
                            mostrandoDespedida = true,
                            varianteDespedida = variante,
                            nombreDespedida = nombre
                        )

                        delay(DURACION_DESPEDIDA)
                    }

                    // Regresar al inicio de sesión.

                    _estado.value = SesionUiState(
                        usuario = null,
                        verificando = false
                    )

                } else {

                    _estado.update {
                        it.copy(
                            cerrandoSesion = false,
                            error = "No se pudo cerrar la sesión."
                        )
                    }
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _estado.update {
                    it.copy(
                        cerrandoSesion = false,
                        mostrandoDespedida = false,
                        error = "Ocurrió un error al cerrar la sesión."
                    )
                }
            }
        }
    }

    // Limpiar mensajes de error.

    fun limpiarError() {

        _estado.update {
            it.copy(error = null)
        }
    }
}

// ======================================================
// NAVEGACIÓN PRINCIPAL
// ======================================================

@Composable
fun AppNavigation() {

    val context = LocalContext.current

    val repository = remember {
        AuthRepository(context.applicationContext)
    }

    val recyclingRepository = remember {
        RecyclingRepository(context.applicationContext)
    }

    val sesionViewModel: SesionViewModel =
        viewModel()

    val sesion by
    sesionViewModel.estado.collectAsState()

    val usuario = sesion.usuario

    // ==================================================
    // BIENVENIDA ANIMADA
    // ==================================================

    if (sesion.verificando) {

        WelcomeScreen(
            variante = sesion.varianteBienvenida,
            nombreUsuario = sesion.nombreBienvenida
        )

        return
    }

    // ==================================================
    // DESPEDIDA ANIMADA
    // ==================================================

    if (sesion.mostrandoDespedida) {

        FarewellScreen(
            nombreUsuario = sesion.nombreDespedida,
            variante = sesion.varianteDespedida
        )

        return
    }

    // ==================================================
    // ERROR AL RECUPERAR SESIÓN
    // ==================================================

    if (usuario == null && sesion.error != null) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background
                ),
            contentAlignment = Alignment.Center
        ) {

            EcoErrorDialog(

                titulo = "No pudimos recuperar tu sesión",

                mensaje = sesion.error.orEmpty(),

                onDismiss = {
                    sesionViewModel.cerrarSesion()
                },

                onReintentar = {
                    sesionViewModel.restaurarSesion()
                }
            )
        }

        return
    }

    // ==================================================
    // NAVEGACIÓN INDEPENDIENTE PARA CADA USUARIO
    // ==================================================

    key(usuario?.id) {

        val navController = rememberNavController()

        val entradaActual by
        navController.currentBackStackEntryAsState()

        val rutaActual =
            entradaActual?.destination?.route

        // Identificar la pestaña activa.

        val pestanaActual = when (rutaActual) {

            Routes.Dashboard.route ->
                EcoTab.INICIO

            Routes.Recycling.route ->
                EcoTab.RECICLAR

            Routes.Rewards.route,
            Routes.MyRewards.route ->
                EcoTab.PREMIOS

            Routes.History.route ->
                EcoTab.HISTORIAL

            Routes.Profile.route ->
                EcoTab.PERFIL

            else -> null
        }

        // Navegación hacia pantallas secundarias.

        fun navegar(ruta: String) {

            if (
                navController.currentDestination?.route == ruta
            ) return

            navController.navigate(ruta) {

                launchSingleTop = true
            }
        }

        // Regresar al dashboard sin duplicarlo.

        fun volverInicio() {

            if (
                navController.currentDestination?.route ==
                Routes.Dashboard.route
            ) return

            navController.navigate(
                Routes.Dashboard.route
            ) {

                popUpTo(Routes.Dashboard.route) {
                    inclusive = false
                }

                launchSingleTop = true
            }
        }

        // Navegar entre pestañas principales.
        // Evita acumular pantallas innecesarias.

        fun navegarPestana(tab: EcoTab) {

            val destino = when (tab) {

                EcoTab.INICIO ->
                    Routes.Dashboard.route

                EcoTab.RECICLAR ->
                    Routes.Recycling.route

                EcoTab.PREMIOS ->
                    Routes.Rewards.route

                EcoTab.HISTORIAL ->
                    Routes.History.route

                EcoTab.PERFIL ->
                    Routes.Profile.route
            }

            if (
                navController.currentDestination?.route ==
                destino
            ) return

            navController.navigate(destino) {

                popUpTo(Routes.Dashboard.route) {
                    inclusive = false
                }

                launchSingleTop = true
            }
        }

        // ==================================================
        // CONTROL DE VISIBILIDAD DE LA BARRA INFERIOR
        //
        // El dashboard conserva temporalmente su barra
        // original para evitar mostrar dos barras.
        // ==================================================

        val mostrarBarra =
            usuario != null &&
                    pestanaActual != null &&
                    rutaActual != Routes.Dashboard.route &&
                    !sesion.cerrandoSesion

        Scaffold(

            containerColor =
                MaterialTheme.colorScheme.background,

            contentWindowInsets =
                WindowInsets(0, 0, 0, 0),

            bottomBar = {

                if (mostrarBarra) {

                    pestanaActual?.let { tab ->

                        EcoBottomBar(

                            selectedTab = tab,

                            onTabSelected = { nueva ->

                                navegarPestana(nueva)
                            }
                        )
                    }
                }
            }

        ) { paddingPrincipal ->

            NavHost(

                navController = navController,

                startDestination =
                    if (usuario != null)
                        Routes.Dashboard.route
                    else
                        Routes.Login.route,

                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingPrincipal)
                    .consumeWindowInsets(paddingPrincipal)

            ) {

                // ======================================
                // LOGIN
                // ======================================

                composable(Routes.Login.route) {

                    val authViewModel: AuthViewModel =
                        viewModel()

                    val estado by
                    authViewModel.uiState.collectAsState()

                    var errorCuenta by remember {
                        mutableStateOf<String?>(null)
                    }

                    LaunchedEffect(
                        estado.isAuthenticated,
                        estado.usuarioId
                    ) {

                        val id = estado.usuarioId

                        if (
                            estado.isAuthenticated &&
                            id != null
                        ) {

                            try {

                                val cuenta =
                                    repository.obtenerUsuario(id)

                                if (cuenta != null) {

                                    sesionViewModel.iniciarSesion(
                                        cuenta
                                    )

                                } else {

                                    repository.eliminarSesion()

                                    authViewModel.clearState()

                                    errorCuenta =
                                        "No se encontró la cuenta."
                                }

                            } catch (e: CancellationException) {

                                throw e

                            } catch (e: Exception) {

                                authViewModel.clearState()

                                errorCuenta =
                                    "No se pudo consultar la cuenta."
                            }
                        }
                    }

                    LoginScreen(

                        authViewModel = authViewModel,

                        onRegister = {

                            navegar(Routes.Register.route)
                        }
                    )

                    errorCuenta?.let { error ->

                        EcoErrorDialog(

                            titulo = "Inicio de sesión",

                            mensaje = error,

                            onDismiss = {

                                errorCuenta = null
                            }
                        )
                    }
                }

                // ======================================
                // REGISTRO
                // ======================================

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

                // ======================================
                // DASHBOARD PRINCIPAL
                // ======================================

                composable(Routes.Dashboard.route) {

                    val cuenta = usuario

                    if (cuenta == null) {

                        LaunchedEffect(Unit) {

                            navController.navigate(
                                Routes.Login.route
                            ) {

                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }

                                launchSingleTop = true
                            }
                        }

                    } else {

                        var estadisticas by remember(cuenta.id) {

                            mutableStateOf<EstadisticasReciclaje?>(
                                null
                            )
                        }

                        var error by remember {

                            mutableStateOf<String?>(null)
                        }

                        var reintento by remember {

                            mutableIntStateOf(0)
                        }

                        var confirmarSalida by remember {

                            mutableStateOf(false)
                        }

                        // Consultar estadísticas reales de SQLite.

                        LaunchedEffect(cuenta.id, reintento) {

                            error = null

                            try {

                                estadisticas =
                                    recyclingRepository
                                        .obtenerEstadisticas(
                                            cuenta.id
                                        )

                            } catch (e: CancellationException) {

                                throw e

                            } catch (e: Exception) {

                                error =
                                    "No se pudieron cargar tus estadísticas."
                            }
                        }

                        val datos = estadisticas

                        when {

                            error != null -> {

                                EcoErrorDialog(

                                    titulo =
                                        "No pudimos cargar tus datos",

                                    mensaje = error.orEmpty(),

                                    onDismiss = {

                                        error = null

                                        confirmarSalida = true
                                    },

                                    onReintentar = {

                                        reintento++
                                    }
                                )
                            }

                            datos == null -> {

                                if (!confirmarSalida) {

                                    EcoCargandoDialog(

                                        titulo =
                                            "Preparando tu espacio",

                                        mensaje =
                                            "Estamos consultando tu impacto ambiental."
                                    )
                                }
                            }

                            else -> {

                                DashboardScreen(

                                    nombreUsuario = cuenta.nombre,

                                    puntos = datos.puntos,

                                    kilogramos = datos.kilogramos,

                                    reciclajes = datos.reciclajes,

                                    onRecyclingClick = {

                                        navegarPestana(
                                            EcoTab.RECICLAR
                                        )
                                    },

                                    onRewardsClick = {

                                        navegarPestana(
                                            EcoTab.PREMIOS
                                        )
                                    },

                                    onPointsClick = {

                                        navegarPestana(
                                            EcoTab.HISTORIAL
                                        )
                                    },

                                    onHistoryClick = {

                                        navegarPestana(
                                            EcoTab.HISTORIAL
                                        )
                                    },

                                    onProfileClick = {

                                        navegarPestana(
                                            EcoTab.PERFIL
                                        )
                                    },

                                    onMyRewardsClick = {
                                        navegar(Routes.MyRewards.route)
                                    }

                                )
                            }
                        }

                        // Permitir cerrar sesión si ocurre
                        // un error al cargar el dashboard.

                        if (
                            confirmarSalida &&
                            !sesion.cerrandoSesion
                        ) {

                            EcoCerrarSesionDialog(

                                nombre = cuenta.nombre,

                                onCancelar = {

                                    confirmarSalida = false

                                    reintento++
                                },

                                onConfirmar = {

                                    confirmarSalida = false

                                    sesionViewModel.cerrarSesion()
                                }
                            )
                        }
                    }
                }

                // ======================================
                // REGISTRAR RECICLAJE
                // ======================================

                composable(Routes.Recycling.route) {

                    usuario?.let { cuenta ->

                        RecyclingScreen(

                            usuarioId = cuenta.id,

                            onBackClick = {

                                volverInicio()
                            }
                        )
                    }
                }

                // ======================================
                // CATÁLOGO DE RECOMPENSAS
                // ======================================

                composable(Routes.Rewards.route) {

                    usuario?.let { cuenta ->

                        RewardsScreen(

                            usuarioId = cuenta.id,

                            onBackClick = {

                                volverInicio()
                            },

                            onMyRewardsClick = {

                                navegar(
                                    Routes.MyRewards.route
                                )
                            }
                        )
                    }
                }

                // ======================================
                // MIS PREMIOS
                // Códigos ECO# y reclamaciones físicas.
                // ======================================

                composable(Routes.MyRewards.route) {

                    usuario?.let { cuenta ->

                        MyRewardsScreen(

                            usuarioId = cuenta.id,

                            onBackClick = {

                                if (!navController.popBackStack()) {

                                    volverInicio()
                                }
                            },

                            onExploreClick = {

                                val regreso =
                                    navController.popBackStack(
                                        Routes.Rewards.route,
                                        inclusive = false
                                    )

                                if (!regreso) {

                                    navegarPestana(
                                        EcoTab.PREMIOS
                                    )
                                }
                            }
                        )
                    }
                }

                // ======================================
                // HISTORIAL DE MOVIMIENTOS
                // ======================================

                composable(Routes.History.route) {

                    usuario?.let { cuenta ->

                        HistoryScreen(

                            usuarioId = cuenta.id,

                            onBackClick = {

                                volverInicio()
                            },

                            onMyRewardsClick = {

                                navegar(
                                    Routes.MyRewards.route
                                )
                            }
                        )
                    }
                }

                // ======================================
                // NUEVO PERFIL INDEPENDIENTE
                // ======================================

                composable(Routes.Profile.route) {

                    usuario?.let { cuenta ->

                        var datos by remember(cuenta.id) {

                            mutableStateOf<EstadisticasReciclaje?>(
                                null
                            )
                        }

                        var error by remember {

                            mutableStateOf<String?>(null)
                        }

                        var reintento by remember {

                            mutableIntStateOf(0)
                        }

                        // Consultar información actualizada.
                        // No utilizar estadísticas inventadas.

                        LaunchedEffect(cuenta.id, reintento) {

                            error = null

                            try {

                                datos =
                                    recyclingRepository
                                        .obtenerEstadisticas(
                                            cuenta.id
                                        )

                            } catch (e: CancellationException) {

                                throw e

                            } catch (e: Exception) {

                                error =
                                    "No pudimos consultar tu perfil."
                            }
                        }

                        val estadisticas = datos

                        when {

                            error != null -> {

                                Box(

                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            MaterialTheme
                                                .colorScheme
                                                .background
                                        ),

                                    contentAlignment =
                                        Alignment.Center

                                ) {

                                    EcoErrorDialog(

                                        titulo =
                                            "No pudimos cargar tu perfil",

                                        mensaje =
                                            error.orEmpty(),

                                        onDismiss = {

                                            volverInicio()
                                        },

                                        onReintentar = {

                                            reintento++
                                        }
                                    )
                                }
                            }

                            estadisticas == null -> {

                                Box(

                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            MaterialTheme
                                                .colorScheme
                                                .background
                                        ),

                                    contentAlignment =
                                        Alignment.Center

                                ) {

                                    EcoCargandoDialog(

                                        titulo =
                                            "Consultando tu perfil",

                                        mensaje =
                                            "Estamos preparando tus datos."
                                    )
                                }
                            }

                            else -> {

                                // Nuevo perfil profesional.

                                ProfileScreen(

                                    nombreUsuario =
                                        cuenta.nombre,

                                    correo =
                                        cuenta.correo,

                                    puntos =
                                        estadisticas.puntos,

                                    reciclajes =
                                        estadisticas.reciclajes,

                                    kilogramos =
                                        estadisticas.kilogramos,

                                    onBackClick = {

                                        volverInicio()
                                    },

                                    onMyRewardsClick = {

                                        navegar(
                                            Routes.MyRewards.route
                                        )
                                    },

                                    onHistoryClick = {

                                        navegarPestana(
                                            EcoTab.HISTORIAL
                                        )
                                    },

                                    onRewardsClick = {

                                        navegarPestana(
                                            EcoTab.PREMIOS
                                        )
                                    },

                                    onRecyclingClick = {

                                        navegarPestana(
                                            EcoTab.RECICLAR
                                        )
                                    },

                                    onLogoutClick = {

                                        sesionViewModel.cerrarSesion()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ==================================================
    // ESTADOS GLOBALES DE CIERRE DE SESIÓN
    // ==================================================

    if (sesion.cerrandoSesion) {

        EcoCargandoDialog(

            titulo = "Cerrando tu sesión",

            mensaje =
                "Estamos protegiendo tu información."
        )
    }

    // Error durante el cierre de sesión.

    if (
        usuario != null &&
        sesion.error != null &&
        !sesion.cerrandoSesion
    ) {

        EcoErrorDialog(

            titulo =
                "No pudimos cerrar tu sesión",

            mensaje =
                sesion.error.orEmpty(),

            onDismiss = {

                sesionViewModel.limpiarError()
            },

            onReintentar = {

                sesionViewModel.cerrarSesion()
            }
        )
    }
}

// ======================================================
// FIN DEL ARCHIVO
// ECORECICLA MOBILE
// ======================================================