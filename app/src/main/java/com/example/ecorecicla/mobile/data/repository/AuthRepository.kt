package com.example.ecorecicla.mobile.data.repository

import android.content.Context
import com.example.ecorecicla.mobile.data.local.EcoReciclaDatabase
import com.example.ecorecicla.mobile.data.local.UsuarioLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Patrón Repository.
// Centraliza la autenticación y persistencia de sesión.
class AuthRepository(context: Context) {

    private val appContext = context.applicationContext

    private val database =
        EcoReciclaDatabase.obtenerInstancia(appContext)

    // Preferencias privadas de EcoRecicla.
    private val preferencias =
        appContext.getSharedPreferences(
            "ecorecicla_sesion",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val CLAVE_USUARIO = "usuario_id"
    }

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

    // Guardar únicamente el identificador del usuario.
    // Nunca almacenar la contraseña en SharedPreferences.
    suspend fun guardarSesion(
        usuarioId: Long
    ): Boolean = withContext(Dispatchers.IO) {

        preferencias.edit()
            .putLong(CLAVE_USUARIO, usuarioId)
            .commit()
    }

    // Recuperar la sesión guardada y comprobar
    // que el usuario todavía exista en SQLite.
    suspend fun recuperarSesion(): UsuarioLocal? =
        withContext(Dispatchers.IO) {

            val usuarioId = preferencias.getLong(
                CLAVE_USUARIO,
                -1L
            )

            if (usuarioId <= 0L) {
                return@withContext null
            }

            val usuario =
                database.obtenerUsuario(usuarioId)

            // Eliminar sesiones de cuentas inexistentes.
            if (usuario == null) {
                preferencias.edit()
                    .remove(CLAVE_USUARIO)
                    .commit()
            }

            usuario
        }

    // Eliminar la sesión recordada.
    // No elimina usuarios, puntos ni reciclajes.
    suspend fun eliminarSesion(): Boolean =
        withContext(Dispatchers.IO) {

            preferencias.edit()
                .remove(CLAVE_USUARIO)
                .commit()
        }
}