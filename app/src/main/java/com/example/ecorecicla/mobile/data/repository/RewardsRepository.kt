package com.example.ecorecicla.mobile.data.repository

import android.content.ContentValues
import android.content.Context
import com.example.ecorecicla.mobile.data.local.EcoReciclaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Modelo de recompensa.
data class RecompensaLocal(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val costoPuntos: Int,
    val stock: Int
)

// Modelo para el historial de operaciones.
data class MovimientoLocal(
    val tipo: String,
    val descripcion: String,
    val puntos: Int,
    val fecha: String
)

// Información del catálogo.
data class CatalogoLocal(
    val saldo: Int,
    val recompensas: List<RecompensaLocal>
)

// Patrón Repository.
class RewardsRepository(context: Context) {

    private val database =
        EcoReciclaDatabase.obtenerInstancia(context)

    // Consultar saldo y recompensas disponibles.
    suspend fun obtenerCatalogo(
        usuarioId: Long
    ): CatalogoLocal = withContext(Dispatchers.IO) {

        val db = database.readableDatabase

        val saldo = db.rawQuery(
            "SELECT puntos FROM usuarios WHERE id = ?",
            arrayOf(usuarioId.toString())
        ).use { cursor ->

            check(cursor.moveToFirst()) {
                "Usuario no encontrado"
            }

            cursor.getInt(0)
        }

        val recompensas = mutableListOf<RecompensaLocal>()

        db.rawQuery(
            """
            SELECT id, nombre, descripcion, costo_puntos, stock
            FROM recompensas
            ORDER BY costo_puntos
            """.trimIndent(),
            null
        ).use { cursor ->

            while (cursor.moveToNext()) {

                recompensas.add(
                    RecompensaLocal(
                        id = cursor.getLong(0),
                        nombre = cursor.getString(1),
                        descripcion = cursor.getString(2),
                        costoPuntos = cursor.getInt(3),
                        stock = cursor.getInt(4)
                    )
                )
            }
        }

        CatalogoLocal(saldo, recompensas)
    }

    // Canjear una recompensa mediante una transacción.
    suspend fun canjear(
        usuarioId: Long,
        recompensaId: Long
    ): String = withContext(Dispatchers.IO) {

        val db = database.writableDatabase

        db.beginTransaction()

        try {

            val recompensa = db.rawQuery(
                """
                SELECT id, nombre, descripcion, costo_puntos, stock
                FROM recompensas
                WHERE id = ?
                """.trimIndent(),
                arrayOf(recompensaId.toString())
            ).use { cursor ->

                check(cursor.moveToFirst()) {
                    "Recompensa no encontrada"
                }

                RecompensaLocal(
                    id = cursor.getLong(0),
                    nombre = cursor.getString(1),
                    descripcion = cursor.getString(2),
                    costoPuntos = cursor.getInt(3),
                    stock = cursor.getInt(4)
                )
            }

            val saldo = db.rawQuery(
                "SELECT puntos FROM usuarios WHERE id = ?",
                arrayOf(usuarioId.toString())
            ).use { cursor ->

                check(cursor.moveToFirst()) {
                    "Usuario no encontrado"
                }

                cursor.getInt(0)
            }

            // Validaciones del negocio.
            check(recompensa.stock > 0) {
                "La recompensa está agotada"
            }

            check(saldo >= recompensa.costoPuntos) {
                "No tienes suficientes puntos"
            }

            // Descontar puntos.
            db.execSQL(
                """
                UPDATE usuarios
                SET puntos = puntos - ?
                WHERE id = ?
                """.trimIndent(),
                arrayOf<Any>(
                    recompensa.costoPuntos,
                    usuarioId
                )
            )

            // Actualizar inventario.
            db.execSQL(
                """
                UPDATE recompensas
                SET stock = stock - 1
                WHERE id = ?
                """.trimIndent(),
                arrayOf<Any>(recompensaId)
            )

            // Guardar el canje.
            val valores = ContentValues().apply {

                put("usuario_id", usuarioId)
                put("recompensa_id", recompensaId)
                put(
                    "puntos_utilizados",
                    recompensa.costoPuntos
                )
            }

            db.insertOrThrow(
                "canjes",
                null,
                valores
            )

            db.setTransactionSuccessful()

            "¡Canje exitoso! ${recompensa.nombre}"

        } finally {

            db.endTransaction()
        }
    }

    // Historial unificado de reciclajes y canjes.
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
                    r.fecha AS fecha
                FROM reciclajes r
                INNER JOIN materiales m
                    ON m.id = r.material_id
                WHERE r.usuario_id = ?

                UNION ALL

                SELECT
                    'Canje' AS tipo,
                    p.nombre AS descripcion,
                    -c.puntos_utilizados AS puntos,
                    c.fecha AS fecha
                FROM canjes c
                INNER JOIN recompensas p
                    ON p.id = c.recompensa_id
                WHERE c.usuario_id = ?

                ORDER BY fecha DESC
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
                            descripcion = cursor.getString(1),
                            puntos = cursor.getInt(2),
                            fecha = cursor.getString(3)
                        )
                    )
                }
            }

            movimientos
        }
}