package com.example.ecorecicla.mobile.data.remote

// ======================================================
// PROYECTO: ECORECICLA MOBILE
// ARCHIVO: ApiServiceFactory.kt
// PATRÓN DE DISEÑO: FACTORY METHOD
// ======================================================

// Clase abstracta que define el método de creación.
abstract class ServiceFactory {

    abstract fun createService(): ApiService
}

// Fábrica concreta encargada de crear el servicio REST.
class ApiServiceFactory : ServiceFactory() {

    override fun createService(): ApiService {

        // Reutiliza la instancia única de Retrofit
        // implementada mediante el patrón Singleton.
        return RetrofitClient.retrofit.create(
            ApiService::class.java
        )
    }
}