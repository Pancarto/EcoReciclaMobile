package com.example.ecorecicla.mobile.data.repository

import android.content.Context
import com.example.ecorecicla.mobile.data.local.EcoReciclaDatabase
import com.example.ecorecicla.mobile.data.local.UsuarioLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Patrón Repository.
// Centraliza las operaciones de autenticación local.

class AuthRepository(context: Context) {

    private val database =
        EcoReciclaDatabase.obtenerInstancia(context)

    // Registrar usuario sin bloquear la interfaz.
    suspend fun registrar(
        nombre: String,
        correo: String,
        password: String
    ): Boolean = withContext(Dispatchers.IO) {

        database.registrarUsuario(
            nombre,
            correo,
            password
        ) != -1L
    }

    // Validar las credenciales almacenadas en SQLite.
    suspend fun login(
        correo: String,
        password: String
    ): UsuarioLocal? = withContext(Dispatchers.IO) {

        database.autenticarUsuario(correo, password)
    }

    // Consultar información del usuario.
    suspend fun obtenerUsuario(
        id: Long
    ): UsuarioLocal? = withContext(Dispatchers.IO) {

        database.obtenerUsuario(id)
    }
}