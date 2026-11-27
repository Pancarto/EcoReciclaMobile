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

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: EcoReciclaDatabase.kt
// MOTOR: SQLite
// VERSION: 2
// PATRON DE DISEÑO: Singleton
// ======================================================

// Modelo del usuario autenticado.

data class UsuarioLocal(
    val id: Long,
    val nombre: String,
    val correo: String,
    val puntos: Int
)

// ======================================================
// BASE DE DATOS PERSISTENTE
// ======================================================

class EcoReciclaDatabase private constructor(
    context: Context
) : SQLiteOpenHelper(
    context.applicationContext,
    "ecorecicla_mobile.db",
    null,
    2
) {

    // ==================================================
    // PATRON SINGLETON
    // Una sola instancia de la base de datos.
    // ==================================================

    companion object {

        const val VERSION_BASE_DATOS = 2

        const val ESTADO_PENDIENTE = "PENDIENTE"

        const val ESTADO_ENTREGADO = "ENTREGADO"

        const val ESTADO_ANTERIOR = "ANTERIOR"

        @Volatile
        private var instancia: EcoReciclaDatabase? = null

        fun obtenerInstancia(
            context: Context
        ): EcoReciclaDatabase {

            return instancia ?: synchronized(this) {

                instancia ?: EcoReciclaDatabase(
                    context
                ).also {

                    instancia = it
                }
            }
        }
    }

    // ==================================================
    // CONFIGURACION DE SQLITE
    // ==================================================

    override fun onConfigure(db: SQLiteDatabase) {

        super.onConfigure(db)

        // Activar integridad referencial.

        db.setForeignKeyConstraintsEnabled(true)
    }

    // ==================================================
    // CREACION INICIAL DE LA BASE DE DATOS
    // ==================================================

    override fun onCreate(db: SQLiteDatabase) {

        crearTablaUsuarios(db)

        crearTablaMateriales(db)

        crearTablaReciclajes(db)

        crearTablaRecompensas(db)

        crearTablaCanjes(db)

        crearIndices(db)

        insertarMaterialesIniciales(db)

        insertarRecompensasIniciales(db)
    }

    // ==================================================
    // TABLA DE USUARIOS
    // ==================================================

    private fun crearTablaUsuarios(
        db: SQLiteDatabase
    ) {

        db.execSQL(
            """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                salt TEXT NOT NULL,
                puntos INTEGER NOT NULL DEFAULT 0
                    CHECK(puntos >= 0)
            )
            """.trimIndent()
        )
    }

    // ==================================================
    // TABLA DE MATERIALES
    // ==================================================

    private fun crearTablaMateriales(
        db: SQLiteDatabase
    ) {

        db.execSQL(
            """
            CREATE TABLE materiales (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE,
                puntos_por_kg INTEGER NOT NULL
                    CHECK(puntos_por_kg > 0)
            )
            """.trimIndent()
        )
    }

    // ==================================================
    // TABLA DE RECICLAJES
    // ==================================================

    private fun crearTablaReciclajes(
        db: SQLiteDatabase
    ) {

        db.execSQL(
            """
            CREATE TABLE reciclajes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                material_id INTEGER NOT NULL,
                peso REAL NOT NULL CHECK(peso > 0),
                puntos_ganados INTEGER NOT NULL,
                fecha TEXT DEFAULT CURRENT_TIMESTAMP,

                FOREIGN KEY(usuario_id)
                    REFERENCES usuarios(id),

                FOREIGN KEY(material_id)
                    REFERENCES materiales(id)
            )
            """.trimIndent()
        )
    }

    // ==================================================
    // TABLA DE RECOMPENSAS
    // ==================================================

    private fun crearTablaRecompensas(
        db: SQLiteDatabase
    ) {

        db.execSQL(
            """
            CREATE TABLE recompensas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                costo_puntos INTEGER NOT NULL
                    CHECK(costo_puntos > 0),

                stock INTEGER NOT NULL DEFAULT 0
                    CHECK(stock >= 0)
            )
            """.trimIndent()
        )
    }

    // ==================================================
    // TABLA DE CANJES - VERSION 2
    // ==================================================

    private fun crearTablaCanjes(
        db: SQLiteDatabase
    ) {

        db.execSQL(
            """
            CREATE TABLE canjes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,

                usuario_id INTEGER NOT NULL,

                recompensa_id INTEGER NOT NULL,

                puntos_utilizados INTEGER NOT NULL,

                fecha TEXT DEFAULT CURRENT_TIMESTAMP,

                codigo_reclamacion TEXT,

                estado TEXT NOT NULL DEFAULT 'ANTERIOR'
                    CHECK(
                        estado IN (
                            'PENDIENTE',
                            'ENTREGADO',
                            'ANTERIOR'
                        )
                    ),

                fecha_entrega TEXT DEFAULT NULL,

                FOREIGN KEY(usuario_id)
                    REFERENCES usuarios(id),

                FOREIGN KEY(recompensa_id)
                    REFERENCES recompensas(id)
            )
            """.trimIndent()
        )
    }

    // ==================================================
    // INDICES DE LA BASE DE DATOS
    // Mejoran las consultas y evitan códigos duplicados.
    // ==================================================

    private fun crearIndices(
        db: SQLiteDatabase
    ) {

        // Impedir códigos de reclamación duplicados.

        db.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS
                idx_canjes_codigo_reclamacion
            ON canjes(codigo_reclamacion)
            """.trimIndent()
        )

        // Consultar premios de un usuario.

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS
                idx_canjes_usuario
            ON canjes(usuario_id)
            """.trimIndent()
        )

        // Consultar premios según su estado.

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS
                idx_canjes_usuario_estado
            ON canjes(usuario_id, estado)
            """.trimIndent()
        )

        // Consultar el historial de reciclajes.

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS
                idx_reciclajes_usuario
            ON reciclajes(usuario_id)
            """.trimIndent()
        )
    }

    // ==================================================
    // DATOS INICIALES DE MATERIALES
    // ==================================================

    private fun insertarMaterialesIniciales(
        db: SQLiteDatabase
    ) {

        db.execSQL(
            """
            INSERT INTO materiales (
                nombre,
                puntos_por_kg
            )
            VALUES
                ('Plástico', 100),
                ('Papel', 50),
                ('Cartón', 60),
                ('Vidrio', 80),
                ('Metal', 150)
            """.trimIndent()
        )
    }

    // ==================================================
    // DATOS INICIALES DE RECOMPENSAS
    // ==================================================

    private fun insertarRecompensasIniciales(
        db: SQLiteDatabase
    ) {

        db.execSQL(
            """
            INSERT INTO recompensas (
                nombre,
                descripcion,
                costo_puntos,
                stock
            )
            VALUES
                (
                    'Cuaderno ecológico',
                    'Cuaderno reciclado',
                    200,
                    20
                ),
                (
                    'Bolsa reutilizable',
                    'Bolsa ecológica',
                    300,
                    15
                ),
                (
                    'Botella reutilizable',
                    'Botella para agua',
                    500,
                    10
                ),
                (
                    'Kit ecológico',
                    'Kit de productos sostenibles',
                    1000,
                    5
                )
            """.trimIndent()
        )
    }

    // ==================================================
    // MIGRACIONES DE LA BASE DE DATOS
    // Conserva la información existente.
    // ==================================================

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        var versionActual = oldVersion

        // Migración desde la versión 1 hasta la versión 2.

        if (
            versionActual == 1 &&
            newVersion >= 2
        ) {

            migrarVersionUnoADos(db)

            versionActual = 2
        }

        // Evitar actualizaciones sin migración definida.

        check(versionActual == newVersion) {

            "No existe una migración desde " +
                    "$oldVersion hasta $newVersion"
        }
    }

    // ==================================================
    // MIGRACION 1 -> 2
    // No elimina tablas ni modifica los puntos existentes.
    // SQLiteOpenHelper ejecuta onUpgrade en una transacción.
    // ==================================================

    private fun migrarVersionUnoADos(
        db: SQLiteDatabase
    ) {

        // Incorporar código de reclamación.

        db.execSQL(
            """
            ALTER TABLE canjes
            ADD COLUMN codigo_reclamacion TEXT
            """.trimIndent()
        )

        // Incorporar estado de entrega.
        // Los canjes antiguos conservan el estado ANTERIOR.

        db.execSQL(
            """
            ALTER TABLE canjes
            ADD COLUMN estado TEXT NOT NULL
                DEFAULT 'ANTERIOR'
                CHECK(
                    estado IN (
                        'PENDIENTE',
                        'ENTREGADO',
                        'ANTERIOR'
                    )
                )
            """.trimIndent()
        )

        // Incorporar fecha de entrega física.

        db.execSQL(
            """
            ALTER TABLE canjes
            ADD COLUMN fecha_entrega TEXT DEFAULT NULL
            """.trimIndent()
        )

        // Crear índices sin eliminar información.

        crearIndices(db)
    }

    // ==================================================
    // GENERADOR DE CODIGOS DE RECLAMACION
    // Ejemplo: ECO#58321
    // ==================================================

    fun generarCodigoReclamacion(
        db: SQLiteDatabase
    ): String {

        // Debe ejecutarse dentro de la transacción del canje.

        check(db.inTransaction()) {

            "El código debe generarse dentro de una transacción"
        }

        val aleatorio = SecureRandom()

        // Intentar generar un código de cinco dígitos.

        repeat(100) {

            val numero = 10000 + aleatorio.nextInt(90000)

            val codigo = "ECO#$numero"

            val existe = db.rawQuery(
                """
                SELECT 1
                FROM canjes
                WHERE codigo_reclamacion = ?
                LIMIT 1
                """.trimIndent(),

                arrayOf(codigo)

            ).use { cursor ->

                cursor.moveToFirst()
            }

            if (!existe) {

                return codigo
            }
        }

        // Alternativa de ocho dígitos para evitar que
        // el sistema quede limitado a 90.000 códigos.

        repeat(100) {

            val numero =
                10000000 + aleatorio.nextInt(90000000)

            val codigo = "ECO#$numero"

            val existe = db.rawQuery(
                """
                SELECT 1
                FROM canjes
                WHERE codigo_reclamacion = ?
                LIMIT 1
                """.trimIndent(),

                arrayOf(codigo)

            ).use { cursor ->

                cursor.moveToFirst()
            }

            if (!existe) {

                return codigo
            }
        }

        // Si no se genera un código, el canje debe fallar.
        // La transacción permitirá revertir puntos y stock.

        throw IllegalStateException(
            "No se pudo generar el código de reclamación"
        )
    }

    // ==================================================
    // NORMALIZACION DEL CORREO ELECTRONICO
    // ==================================================

    private fun normalizarCorreo(
        correo: String
    ): String {

        return correo
            .trim()
            .lowercase(Locale.ROOT)
    }

    // ==================================================
    // SEGURIDAD: GENERACION DE SALT
    // ==================================================

    private fun generarSalt(): ByteArray {

        return ByteArray(16).also {

            SecureRandom().nextBytes(it)
        }
    }

    // ==================================================
    // SEGURIDAD: PROTECCION DE CONTRASEÑAS
    // PBKDF2WithHmacSHA256
    // 600.000 iteraciones
    // ==================================================

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

    // ==================================================
    // REGISTRO DE USUARIOS
    // ==================================================

    fun registrarUsuario(
        nombre: String,
        correo: String,
        password: String
    ): Long {

        require(nombre.isNotBlank())

        require(correo.isNotBlank())

        require(password.length >= 8)

        val salt = generarSalt()

        val hash = generarHash(
            password,
            salt
        )

        val valores = ContentValues().apply {

            put(
                "nombre",
                nombre.trim()
            )

            put(
                "correo",
                normalizarCorreo(correo)
            )

            put(
                "password_hash",

                Base64.encodeToString(
                    hash,
                    Base64.NO_WRAP
                )
            )

            put(
                "salt",

                Base64.encodeToString(
                    salt,
                    Base64.NO_WRAP
                )
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

    // ==================================================
    // AUTENTICACION DE USUARIOS
    // ==================================================

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
                    cursor.getColumnIndexOrThrow(
                        "password_hash"
                    )
                ),

                Base64.NO_WRAP
            )

            val hashIngresado = generarHash(
                password,
                salt
            )

            // Comparar las contraseñas protegidas.

            if (
                !MessageDigest.isEqual(
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

    // ==================================================
    // CONSULTAR USUARIO POR IDENTIFICADOR
    // ==================================================

    fun obtenerUsuario(
        id: Long
    ): UsuarioLocal? {

        readableDatabase.query(
            "usuarios",

            arrayOf(
                "id",
                "nombre",
                "correo",
                "puntos"
            ),

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

// ======================================================
// FIN DEL ARCHIVO
// ECORECICLA MOBILE - SQLITE VERSION 2
// ======================================================