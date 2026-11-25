package com.example.ecorecicla.mobile.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Modelo del usuario autenticado.
data class UsuarioLocal(
    val id: Long,
    val nombre: String,
    val correo: String,
    val puntos: Int
)

// Base de datos persistente de EcoRecicla Mobile.
class EcoReciclaDatabase private constructor(
    context: Context
) : SQLiteOpenHelper(
    context.applicationContext,
    "ecorecicla_mobile.db",
    null,
    1
) {

    // Patrón Singleton.
    companion object {

        @Volatile
        private var instancia: EcoReciclaDatabase? = null

        fun obtenerInstancia(context: Context): EcoReciclaDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: EcoReciclaDatabase(context).also {
                    instancia = it
                }
            }
        }
    }

    // Activar integridad referencial.
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {

        // TABLA DE USUARIOS.
        db.execSQL("""
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                salt TEXT NOT NULL,
                puntos INTEGER NOT NULL DEFAULT 0
                    CHECK(puntos >= 0)
            )
        """.trimIndent())

        // TABLA DE MATERIALES.
        db.execSQL("""
            CREATE TABLE materiales (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE,
                puntos_por_kg INTEGER NOT NULL
                    CHECK(puntos_por_kg > 0)
            )
        """.trimIndent())

        // TABLA DE RECICLAJES.
        db.execSQL("""
            CREATE TABLE reciclajes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                material_id INTEGER NOT NULL,
                peso REAL NOT NULL CHECK(peso > 0),
                puntos_ganados INTEGER NOT NULL,
                fecha TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id),
                FOREIGN KEY(material_id) REFERENCES materiales(id)
            )
        """.trimIndent())

        // TABLA DE RECOMPENSAS.
        db.execSQL("""
            CREATE TABLE recompensas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                costo_puntos INTEGER NOT NULL
                    CHECK(costo_puntos > 0),
                stock INTEGER NOT NULL DEFAULT 0
                    CHECK(stock >= 0)
            )
        """.trimIndent())

        // TABLA DE CANJES.
        db.execSQL("""
            CREATE TABLE canjes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                recompensa_id INTEGER NOT NULL,
                puntos_utilizados INTEGER NOT NULL,
                fecha TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id),
                FOREIGN KEY(recompensa_id) REFERENCES recompensas(id)
            )
        """.trimIndent())

        // Datos iniciales del catálogo de materiales.
        db.execSQL("""
            INSERT INTO materiales (nombre, puntos_por_kg)
            VALUES
            ('Plástico', 100),
            ('Papel', 50),
            ('Cartón', 60),
            ('Vidrio', 80),
            ('Metal', 150)
        """.trimIndent())

        // Datos iniciales de recompensas.
        db.execSQL("""
            INSERT INTO recompensas
            (nombre, descripcion, costo_puntos, stock)
            VALUES
            ('Cuaderno ecológico', 'Cuaderno reciclado', 200, 20),
            ('Bolsa reutilizable', 'Bolsa ecológica', 300, 15),
            ('Botella reutilizable', 'Botella para agua', 500, 10),
            ('Kit ecológico', 'Kit de productos sostenibles', 1000, 5)
        """.trimIndent())
    }

    // Las futuras versiones deberán incorporar migraciones.
    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        throw IllegalStateException(
            "Migración de base de datos no implementada"
        )
    }

    // Normalización del correo electrónico.
    private fun normalizarCorreo(correo: String): String {
        return correo.trim().lowercase(Locale.ROOT)
    }

    // Generar salt aleatorio para cada contraseña.
    private fun generarSalt(): ByteArray {
        return ByteArray(16).also {
            SecureRandom().nextBytes(it)
        }
    }

    // Proteger contraseñas mediante PBKDF2.
    private fun generarHash(
        password: String,
        salt: ByteArray
    ): ByteArray {

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            600000,
            256
        )

        return try {
            SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .encoded
        } finally {
            spec.clearPassword()
        }
    }

    // Registrar un usuario permanentemente.
    fun registrarUsuario(
        nombre: String,
        correo: String,
        password: String
    ): Long {

        require(nombre.isNotBlank())
        require(correo.isNotBlank())
        require(password.length >= 8)

        val salt = generarSalt()
        val hash = generarHash(password, salt)

        val valores = ContentValues().apply {

            put("nombre", nombre.trim())
            put("correo", normalizarCorreo(correo))

            put(
                "password_hash",
                Base64.encodeToString(hash, Base64.NO_WRAP)
            )

            put(
                "salt",
                Base64.encodeToString(salt, Base64.NO_WRAP)
            )

            put("puntos", 0)
        }

        return writableDatabase.insertWithOnConflict(
            "usuarios",
            null,
            valores,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    // Autenticar mediante correo y contraseña.
    fun autenticarUsuario(
        correo: String,
        password: String
    ): UsuarioLocal? {

        readableDatabase.query(
            "usuarios",
            null,
            "correo = ?",
            arrayOf(normalizarCorreo(correo)),
            null,
            null,
            null
        ).use { cursor ->

            if (!cursor.moveToFirst()) {
                return null
            }

            val salt = Base64.decode(
                cursor.getString(
                    cursor.getColumnIndexOrThrow("salt")
                ),
                Base64.NO_WRAP
            )

            val hashGuardado = Base64.decode(
                cursor.getString(
                    cursor.getColumnIndexOrThrow("password_hash")
                ),
                Base64.NO_WRAP
            )

            val hashIngresado = generarHash(password, salt)

            if (!MessageDigest.isEqual(
                    hashGuardado,
                    hashIngresado
                )
            ) {
                return null
            }

            return UsuarioLocal(
                id = cursor.getLong(
                    cursor.getColumnIndexOrThrow("id")
                ),
                nombre = cursor.getString(
                    cursor.getColumnIndexOrThrow("nombre")
                ),
                correo = cursor.getString(
                    cursor.getColumnIndexOrThrow("correo")
                ),
                puntos = cursor.getInt(
                    cursor.getColumnIndexOrThrow("puntos")
                )
            )
        }
    }

    // Consultar un usuario por su identificador.
    fun obtenerUsuario(id: Long): UsuarioLocal? {

        readableDatabase.query(
            "usuarios",
            arrayOf("id", "nombre", "correo", "puntos"),
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use { cursor ->

            if (!cursor.moveToFirst()) {
                return null
            }

            return UsuarioLocal(
                id = cursor.getLong(0),
                nombre = cursor.getString(1),
                correo = cursor.getString(2),
                puntos = cursor.getInt(3)
            )
        }
    }
}