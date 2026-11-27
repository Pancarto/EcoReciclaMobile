package com.example.ecorecicla.mobile.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.ecorecicla.mobile.data.local.EcoReciclaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ======================================================
// ECORECICLA MOBILE
// ARCHIVO: RewardsRepository.kt
// RESPONSABILIDAD: Gestión de recompensas y canjes.
// PATRÓN DE DISEÑO: Repository.
// PERSISTENCIA: SQLite.
// ======================================================

// ======================================================
// MODELO DE RECOMPENSA
// ======================================================

data class RecompensaLocal(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val costoPuntos: Int,
    val stock: Int
)

// ======================================================
// MODELO DEL HISTORIAL DE MOVIMIENTOS
// ======================================================

data class MovimientoLocal(
    val tipo: String,
    val descripcion: String,
    val puntos: Int,
    val fecha: String,

    // Información adicional para los canjes.
    // Los valores predeterminados conservan compatibilidad
    // con las pantallas anteriores.

    val canjeId: Long? = null,
    val codigoReclamacion: String? = null,
    val estado: String? = null,
    val fechaEntrega: String? = null
)

// ======================================================
// MODELO DEL CATÁLOGO
// ======================================================

data class CatalogoLocal(
    val saldo: Int,
    val recompensas: List<RecompensaLocal>
)

// ======================================================
// MODELO DE COMPROBANTE DE CANJE
// ======================================================

data class CanjeLocal(
    val id: Long,
    val usuarioId: Long,
    val recompensaId: Long,
    val nombre: String,
    val descripcion: String,
    val puntosUtilizados: Int,
    val codigoReclamacion: String?,
    val estado: String,
    val fecha: String,
    val fechaEntrega: String?
) {

    // Punto físico definido para el piloto universitario.

    val puntoEntrega: String
        get() = "Papelería de la universidad"

    // Determinar si el premio está pendiente.

    val pendiente: Boolean
        get() =
            estado == EcoReciclaDatabase.ESTADO_PENDIENTE

    // Determinar si el premio fue entregado.

    val entregado: Boolean
        get() =
            estado == EcoReciclaDatabase.ESTADO_ENTREGADO

    // Identificar operaciones anteriores a la migración.

    val anterior: Boolean
        get() =
            estado == EcoReciclaDatabase.ESTADO_ANTERIOR

    // Estado legible para la interfaz.

    val estadoVisible: String
        get() = when (estado) {

            EcoReciclaDatabase.ESTADO_PENDIENTE ->
                "Pendiente por reclamar"

            EcoReciclaDatabase.ESTADO_ENTREGADO ->
                "Entregado"

            EcoReciclaDatabase.ESTADO_ANTERIOR ->
                "Canje anterior"

            else ->
                "Estado desconocido"
        }
}

// ======================================================
// RESULTADO COMPLETO DE UNA OPERACIÓN DE CANJE
// ======================================================

data class ResultadoCanjeLocal(
    val canje: CanjeLocal,
    val saldoRestante: Int
)

// ======================================================
// RESUMEN DE PREMIOS DEL USUARIO
// ======================================================

data class MisPremiosLocal(
    val pendientes: List<CanjeLocal>,
    val entregados: List<CanjeLocal>,
    val anteriores: List<CanjeLocal>
) {

    val totalPendientes: Int
        get() = pendientes.size

    val totalEntregados: Int
        get() = entregados.size

    val totalAnteriores: Int
        get() = anteriores.size

    val totalCanjes: Int
        get() =
            totalPendientes +
                    totalEntregados +
                    totalAnteriores
}

// ======================================================
// PATRÓN REPOSITORY
// ======================================================

class RewardsRepository(context: Context) {

    // Patrón Singleton para acceder a SQLite.

    private val database =
        EcoReciclaDatabase.obtenerInstancia(
            context.applicationContext
        )

    // ==================================================
    // CONSULTAR SALDO DEL USUARIO
    // ==================================================

    private fun consultarSaldo(
        db: SQLiteDatabase,
        usuarioId: Long
    ): Int {

        return db.rawQuery(
            """
            SELECT puntos
            FROM usuarios
            WHERE id = ?
            """.trimIndent(),

            arrayOf(usuarioId.toString())

        ).use { cursor ->

            check(cursor.moveToFirst()) {
                "Usuario no encontrado"
            }

            cursor.getInt(0)
        }
    }

    // ==================================================
    // CONVERTIR CURSOR EN RECOMPENSA
    // ==================================================

    private fun leerRecompensa(
        cursor: Cursor
    ): RecompensaLocal {

        return RecompensaLocal(
            id = cursor.getLong(0),
            nombre = cursor.getString(1),
            descripcion = cursor.getString(2),
            costoPuntos = cursor.getInt(3),
            stock = cursor.getInt(4)
        )
    }

    // ==================================================
    // CONSULTAR CATÁLOGO DE RECOMPENSAS
    // ==================================================

    suspend fun obtenerCatalogo(
        usuarioId: Long
    ): CatalogoLocal = withContext(Dispatchers.IO) {

        val db = database.readableDatabase

        val saldo = consultarSaldo(
            db,
            usuarioId
        )

        val recompensas =
            mutableListOf<RecompensaLocal>()

        db.rawQuery(
            """
            SELECT
                id,
                nombre,
                descripcion,
                costo_puntos,
                stock
            FROM recompensas
            ORDER BY costo_puntos ASC, id ASC
            """.trimIndent(),

            null

        ).use { cursor ->

            while (cursor.moveToNext()) {

                recompensas.add(
                    leerRecompensa(cursor)
                )
            }
        }

        CatalogoLocal(
            saldo = saldo,
            recompensas = recompensas
        )
    }

    // ==================================================
    // CONSULTAR UNA RECOMPENSA ESPECÍFICA
    // ==================================================

    private fun consultarRecompensa(
        db: SQLiteDatabase,
        recompensaId: Long
    ): RecompensaLocal {

        return db.rawQuery(
            """
            SELECT
                id,
                nombre,
                descripcion,
                costo_puntos,
                stock
            FROM recompensas
            WHERE id = ?
            """.trimIndent(),

            arrayOf(recompensaId.toString())

        ).use { cursor ->

            check(cursor.moveToFirst()) {
                "Recompensa no encontrada"
            }

            leerRecompensa(cursor)
        }
    }

    // ==================================================
    // CANJE COMPATIBLE CON EL VIEWMODEL ANTERIOR
    // ==================================================

    suspend fun canjear(
        usuarioId: Long,
        recompensaId: Long
    ): String {

        // Utiliza el nuevo sistema de reclamación,
        // conservando el tipo de retorno original.

        val resultado = canjearConComprobante(
            usuarioId = usuarioId,
            recompensaId = recompensaId
        )

        return "¡Canje exitoso! ${resultado.canje.nombre}"
    }

    // ==================================================
    // NUEVO SISTEMA DE CANJE CON COMPROBANTE
    // ==================================================

    suspend fun canjearConComprobante(
        usuarioId: Long,
        recompensaId: Long
    ): ResultadoCanjeLocal =
        withContext(Dispatchers.IO) {

            require(usuarioId > 0) {
                "Usuario inválido"
            }

            require(recompensaId > 0) {
                "Recompensa inválida"
            }

            val db = database.writableDatabase

            // Iniciar transacción.
            // Todas las operaciones deben completarse.

            db.beginTransaction()

            try {

                // ======================================
                // 1. CONSULTAR RECOMPENSA
                // ======================================

                val recompensa = consultarRecompensa(
                    db,
                    recompensaId
                )

                // ======================================
                // 2. CONSULTAR SALDO REAL
                // ======================================

                val saldo = consultarSaldo(
                    db,
                    usuarioId
                )

                // ======================================
                // 3. VALIDACIONES DEL NEGOCIO
                // ======================================

                check(recompensa.stock > 0) {
                    "La recompensa está agotada"
                }

                check(recompensa.costoPuntos > 0) {
                    "El costo de la recompensa no es válido"
                }

                check(saldo >= recompensa.costoPuntos) {
                    "No tienes suficientes puntos"
                }

                // ======================================
                // 4. GENERAR CÓDIGO ÚNICO
                // ======================================

                val codigo =
                    database.generarCodigoReclamacion(db)

                // ======================================
                // 5. DESCONTAR PUNTOS
                // ======================================

                val usuariosActualizados =
                    db.compileStatement(
                        """
                        UPDATE usuarios
                        SET puntos = puntos - ?
                        WHERE id = ?
                        AND puntos >= ?
                        """.trimIndent()
                    ).use { sentencia ->

                        sentencia.bindLong(
                            1,
                            recompensa.costoPuntos.toLong()
                        )

                        sentencia.bindLong(
                            2,
                            usuarioId
                        )

                        sentencia.bindLong(
                            3,
                            recompensa.costoPuntos.toLong()
                        )

                        sentencia.executeUpdateDelete()
                    }

                check(usuariosActualizados == 1) {
                    "No se pudieron descontar los puntos"
                }

                // ======================================
                // 6. RESERVAR INVENTARIO
                // ======================================

                val recompensasActualizadas =
                    db.compileStatement(
                        """
                        UPDATE recompensas
                        SET stock = stock - 1
                        WHERE id = ?
                        AND stock > 0
                        """.trimIndent()
                    ).use { sentencia ->

                        sentencia.bindLong(
                            1,
                            recompensaId
                        )

                        sentencia.executeUpdateDelete()
                    }

                check(recompensasActualizadas == 1) {
                    "La recompensa ya no está disponible"
                }

                // ======================================
                // 7. GUARDAR CANJE EN SQLITE
                // ======================================

                val valores = ContentValues().apply {

                    put(
                        "usuario_id",
                        usuarioId
                    )

                    put(
                        "recompensa_id",
                        recompensaId
                    )

                    put(
                        "puntos_utilizados",
                        recompensa.costoPuntos
                    )

                    put(
                        "codigo_reclamacion",
                        codigo
                    )

                    put(
                        "estado",
                        EcoReciclaDatabase.ESTADO_PENDIENTE
                    )

                    putNull("fecha_entrega")
                }

                val canjeId = db.insertOrThrow(
                    "canjes",
                    null,
                    valores
                )

                check(canjeId > 0) {
                    "No se pudo guardar el canje"
                }

                // ======================================
                // 8. RECUPERAR COMPROBANTE GUARDADO
                // ======================================

                val comprobante = consultarCanje(
                    db = db,
                    usuarioId = usuarioId,
                    canjeId = canjeId
                )

                checkNotNull(comprobante) {
                    "No se pudo recuperar el comprobante"
                }

                // ======================================
                // 9. CONSULTAR SALDO ACTUALIZADO
                // ======================================

                val saldoRestante = consultarSaldo(
                    db,
                    usuarioId
                )

                val resultado = ResultadoCanjeLocal(
                    canje = comprobante,
                    saldoRestante = saldoRestante
                )

                // ======================================
                // 10. CONFIRMAR TRANSACCIÓN
                // ======================================

                db.setTransactionSuccessful()

                resultado

            } finally {

                // Si alguna operación falla, SQLite
                // revierte los puntos, el inventario
                // y el registro del canje.

                db.endTransaction()
            }
        }

    // ==================================================
    // CONVERTIR CURSOR EN COMPROBANTE
    // ==================================================

    private fun leerCanje(
        cursor: Cursor
    ): CanjeLocal {

        return CanjeLocal(

            id = cursor.getLong(
                cursor.getColumnIndexOrThrow("id")
            ),

            usuarioId = cursor.getLong(
                cursor.getColumnIndexOrThrow("usuario_id")
            ),

            recompensaId = cursor.getLong(
                cursor.getColumnIndexOrThrow("recompensa_id")
            ),

            nombre = cursor.getString(
                cursor.getColumnIndexOrThrow("nombre")
            ),

            descripcion = cursor.getString(
                cursor.getColumnIndexOrThrow("descripcion")
            ),

            puntosUtilizados = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                    "puntos_utilizados"
                )
            ),

            codigoReclamacion = cursor.getString(
                cursor.getColumnIndexOrThrow(
                    "codigo_reclamacion"
                )
            ),

            estado = cursor.getString(
                cursor.getColumnIndexOrThrow("estado")
            ),

            fecha = cursor.getString(
                cursor.getColumnIndexOrThrow("fecha")
            ).orEmpty(),

            fechaEntrega = cursor.getString(
                cursor.getColumnIndexOrThrow(
                    "fecha_entrega"
                )
            )
        )
    }

    // ==================================================
    // CONSULTA BASE DE CANJES
    // ==================================================

    private val consultaBaseCanjes = """
        SELECT
            c.id AS id,
            c.usuario_id AS usuario_id,
            c.recompensa_id AS recompensa_id,
            p.nombre AS nombre,
            p.descripcion AS descripcion,
            c.puntos_utilizados AS puntos_utilizados,
            c.codigo_reclamacion AS codigo_reclamacion,
            c.estado AS estado,
            c.fecha AS fecha,
            c.fecha_entrega AS fecha_entrega
        FROM canjes c
        INNER JOIN recompensas p
            ON p.id = c.recompensa_id
    """.trimIndent()

    // ==================================================
    // CONSULTAR CANJE DENTRO DE UNA TRANSACCIÓN
    // ==================================================

    private fun consultarCanje(
        db: SQLiteDatabase,
        usuarioId: Long,
        canjeId: Long
    ): CanjeLocal? {

        return db.rawQuery(
            """
            $consultaBaseCanjes
            WHERE c.usuario_id = ?
            AND c.id = ?
            LIMIT 1
            """.trimIndent(),

            arrayOf(
                usuarioId.toString(),
                canjeId.toString()
            )

        ).use { cursor ->

            if (cursor.moveToFirst()) {

                leerCanje(cursor)

            } else {

                null
            }
        }
    }

    // ==================================================
    // CONSULTAR COMPROBANTE INDIVIDUAL
    // ==================================================

    suspend fun obtenerCanje(
        usuarioId: Long,
        canjeId: Long
    ): CanjeLocal? = withContext(Dispatchers.IO) {

        consultarCanje(
            db = database.readableDatabase,
            usuarioId = usuarioId,
            canjeId = canjeId
        )
    }

    // ==================================================
    // CONSULTAR COMPROBANTE MEDIANTE CÓDIGO ECO#
    // ==================================================

    suspend fun obtenerCanjePorCodigo(
        usuarioId: Long,
        codigo: String
    ): CanjeLocal? = withContext(Dispatchers.IO) {

        val codigoNormalizado =
            codigo.trim().uppercase(java.util.Locale.ROOT)

        // Validar formato de cinco u ocho dígitos.

        if (
            !codigoNormalizado.matches(
                Regex("ECO#(?:[0-9]{5}|[0-9]{8})")
            )
        ) {

            return@withContext null
        }

        database.readableDatabase.rawQuery(
            """
            $consultaBaseCanjes
            WHERE c.usuario_id = ?
            AND c.codigo_reclamacion = ?
            LIMIT 1
            """.trimIndent(),

            arrayOf(
                usuarioId.toString(),
                codigoNormalizado
            )

        ).use { cursor ->

            if (cursor.moveToFirst()) {

                leerCanje(cursor)

            } else {

                null
            }
        }
    }

    // ==================================================
    // CONSULTAR TODOS LOS CANJES DEL USUARIO
    // ==================================================

    suspend fun obtenerCanjes(
        usuarioId: Long,
        estado: String? = null
    ): List<CanjeLocal> =
        withContext(Dispatchers.IO) {

            val canjes = mutableListOf<CanjeLocal>()

            val consulta: String

            val argumentos: Array<String>

            if (estado == null) {

                consulta = """
                    $consultaBaseCanjes
                    WHERE c.usuario_id = ?
                    ORDER BY c.fecha DESC, c.id DESC
                """.trimIndent()

                argumentos = arrayOf(
                    usuarioId.toString()
                )

            } else {

                require(
                    estado in listOf(
                        EcoReciclaDatabase.ESTADO_PENDIENTE,
                        EcoReciclaDatabase.ESTADO_ENTREGADO,
                        EcoReciclaDatabase.ESTADO_ANTERIOR
                    )
                ) {
                    "Estado de canje inválido"
                }

                consulta = """
                    $consultaBaseCanjes
                    WHERE c.usuario_id = ?
                    AND c.estado = ?
                    ORDER BY c.fecha DESC, c.id DESC
                """.trimIndent()

                argumentos = arrayOf(
                    usuarioId.toString(),
                    estado
                )
            }

            database.readableDatabase.rawQuery(
                consulta,
                argumentos

            ).use { cursor ->

                while (cursor.moveToNext()) {

                    canjes.add(
                        leerCanje(cursor)
                    )
                }
            }

            canjes
        }

    // ==================================================
    // CONSULTAR PREMIOS PENDIENTES POR RECLAMAR
    // ==================================================

    suspend fun obtenerCanjesPendientes(
        usuarioId: Long
    ): List<CanjeLocal> {

        return obtenerCanjes(
            usuarioId = usuarioId,
            estado = EcoReciclaDatabase.ESTADO_PENDIENTE
        )
    }

    // ==================================================
    // CONSULTAR PREMIOS ENTREGADOS
    // ==================================================

    suspend fun obtenerCanjesEntregados(
        usuarioId: Long
    ): List<CanjeLocal> {

        return obtenerCanjes(
            usuarioId = usuarioId,
            estado = EcoReciclaDatabase.ESTADO_ENTREGADO
        )
    }

    // ==================================================
    // CONSULTAR CANJES ANTERIORES
    // ==================================================

    suspend fun obtenerCanjesAnteriores(
        usuarioId: Long
    ): List<CanjeLocal> {

        return obtenerCanjes(
            usuarioId = usuarioId,
            estado = EcoReciclaDatabase.ESTADO_ANTERIOR
        )
    }

    // ==================================================
    // CONSULTAR CANTIDAD DE PREMIOS PENDIENTES
    // ==================================================

    suspend fun contarCanjesPendientes(
        usuarioId: Long
    ): Int = withContext(Dispatchers.IO) {

        database.readableDatabase.rawQuery(
            """
            SELECT COUNT(*)
            FROM canjes
            WHERE usuario_id = ?
            AND estado = ?
            """.trimIndent(),

            arrayOf(
                usuarioId.toString(),
                EcoReciclaDatabase.ESTADO_PENDIENTE
            )

        ).use { cursor ->

            if (cursor.moveToFirst()) {

                cursor.getInt(0)

            } else {

                0
            }
        }
    }

    // ==================================================
    // CONSULTAR RESUMEN DE MIS PREMIOS
    // ==================================================

    suspend fun obtenerMisPremios(
        usuarioId: Long
    ): MisPremiosLocal {

        val canjes = obtenerCanjes(usuarioId)

        return MisPremiosLocal(

            pendientes = canjes.filter {
                it.pendiente
            },

            entregados = canjes.filter {
                it.entregado
            },

            anteriores = canjes.filter {
                it.anterior
            }
        )
    }

    // ==================================================
    // HISTORIAL UNIFICADO DE RECICLAJES Y CANJES
    // ==================================================

    suspend fun obtenerHistorial(
        usuarioId: Long
    ): List<MovimientoLocal> =
        withContext(Dispatchers.IO) {

            val movimientos =
                mutableListOf<MovimientoLocal>()

            database.readableDatabase.rawQuery(
                """
                SELECT
                    'Reciclaje' AS tipo,

                    m.nombre || ' - ' || r.peso || ' kg'
                        AS descripcion,

                    r.puntos_ganados AS puntos,

                    r.fecha AS fecha,

                    NULL AS canje_id,

                    NULL AS codigo_reclamacion,

                    NULL AS estado,

                    NULL AS fecha_entrega

                FROM reciclajes r

                INNER JOIN materiales m
                    ON m.id = r.material_id

                WHERE r.usuario_id = ?

                UNION ALL

                SELECT
                    'Canje' AS tipo,

                    p.nombre AS descripcion,

                    -c.puntos_utilizados AS puntos,

                    c.fecha AS fecha,

                    c.id AS canje_id,

                    c.codigo_reclamacion
                        AS codigo_reclamacion,

                    c.estado AS estado,

                    c.fecha_entrega AS fecha_entrega

                FROM canjes c

                INNER JOIN recompensas p
                    ON p.id = c.recompensa_id

                WHERE c.usuario_id = ?

                ORDER BY fecha DESC, canje_id DESC
                """.trimIndent(),

                arrayOf(
                    usuarioId.toString(),
                    usuarioId.toString()
                )

            ).use { cursor ->

                while (cursor.moveToNext()) {

                    movimientos.add(

                        MovimientoLocal(

                            tipo = cursor.getString(0),

                            descripcion =
                                cursor.getString(1),

                            puntos = cursor.getInt(2),

                            fecha =
                                cursor.getString(3).orEmpty(),

                            canjeId =
                                if (cursor.isNull(4)) {

                                    null

                                } else {

                                    cursor.getLong(4)
                                },

                            codigoReclamacion =
                                cursor.getString(5),

                            estado =
                                cursor.getString(6),

                            fechaEntrega =
                                cursor.getString(7)
                        )
                    )
                }
            }

            movimientos
        }

    // ==================================================
    // SEGURIDAD DE LA ENTREGA FÍSICA
    // ==================================================

    // No se proporciona un método público para marcar
    // un premio como entregado desde la cuenta estudiantil.
    //
    // La confirmación de entrega deberá implementarse
    // mediante un sistema autorizado para la papelería.
    //
    // Presentar o copiar el código no modifica el estado
    // del premio ni vuelve a descontar puntos.
}

// ======================================================
// FIN DEL ARCHIVO
// ECORECICLA MOBILE
// ======================================================