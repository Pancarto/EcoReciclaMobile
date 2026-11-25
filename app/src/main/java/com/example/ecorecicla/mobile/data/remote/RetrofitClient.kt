package com.example.ecorecicla.mobile.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: RetrofitClient.kt
// PATRÓN DE DISEÑO: SINGLETON
// ======================================================

object RetrofitClient {

    // Dirección base provisional del servidor.
    // Posteriormente se reemplazará por la URL real
    // de la API de EcoRecicla.
    private const val BASE_URL = "https://example.com/"

    // Configuración del cliente HTTP.
    private val httpClient: OkHttpClient by lazy {

        OkHttpClient.Builder()

            // Tiempo máximo para establecer conexión.
            .connectTimeout(30, TimeUnit.SECONDS)

            // Tiempo máximo para recibir información.
            .readTimeout(30, TimeUnit.SECONDS)

            // Tiempo máximo para enviar información.
            .writeTimeout(30, TimeUnit.SECONDS)

            .build()
    }

    // Instancia única de Retrofit.
    // Se inicializa únicamente cuando es necesaria.
    val retrofit: Retrofit by lazy {

        Retrofit.Builder()

            // Dirección del servidor.
            .baseUrl(BASE_URL)

            // Cliente encargado de las peticiones HTTP.
            .client(httpClient)

            // Conversión de datos JSON a objetos Kotlin.
            .addConverterFactory(
                GsonConverterFactory.create()
            )

            .build()
    }
}