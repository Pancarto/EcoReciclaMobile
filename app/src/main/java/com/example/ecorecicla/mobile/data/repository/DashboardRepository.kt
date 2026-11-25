package com.example.ecorecicla.mobile.data.repository

import com.example.ecorecicla.mobile.data.remote.ApiService
import com.example.ecorecicla.mobile.data.remote.ApiServiceFactory

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: DashboardRepository.kt
// PATRÓN DE DISEÑO: REPOSITORY
// RESPONSABILIDAD: Gestionar el acceso a los datos
// correspondientes al Dashboard del usuario.
// ======================================================

// ======================================================
// MODELO DE DATOS DEL DASHBOARD
// ======================================================

data class DashboardData(

    // Nombre del usuario.
    val nombreUsuario: String,

    // Saldo disponible de puntos.
    val puntos: Int,

    // Total de material reciclado en kilogramos.
    val kilogramos: Double,

    // Cantidad de reciclajes realizados.
    val reciclajes: Int
)

// ======================================================
// REPOSITORIO DEL DASHBOARD
// ======================================================

class DashboardRepository(

    // Inyección de dependencias.
    // Utiliza Factory Method para obtener ApiService.

    private val apiService: ApiService =
        ApiServiceFactory().createService()

) {

    // ==================================================
    // OBTENER DATOS DE DEMOSTRACIÓN
    // ==================================================

    fun obtenerDatosDemostracion(): DashboardData {

        // Datos ficticios utilizados exclusivamente
        // durante las pruebas de desarrollo.

        return DashboardData(

            nombreUsuario = "Usuario de prueba",
            puntos = 1250,
            kilogramos = 18.5,
            reciclajes = 12
        )
    }

    // ==================================================
    // FUTURA INTEGRACIÓN CON LA API REST
    // ==================================================

    // Este repositorio será responsable de consultar:
    //
    // 1. Información del usuario autenticado.
    // 2. Saldo disponible de puntos.
    // 3. Total de materiales reciclados.
    // 4. Cantidad de reciclajes realizados.
    //
    // Las consultas HTTP serán implementadas cuando
    // estén disponibles los endpoints del servidor.
    //
    // El ViewModel no accederá directamente a Retrofit
    // ni a la base de datos.
}