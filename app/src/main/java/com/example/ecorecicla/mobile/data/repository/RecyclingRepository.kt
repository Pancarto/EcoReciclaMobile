package com.example.ecorecicla.mobile.data.repository

import android.content.ContentValues
import android.content.Context
import com.example.ecorecicla.mobile.data.local.EcoReciclaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.floor

// Modelo de material reciclable.
data class MaterialReciclable(
    val id: Long,
    val nombre: String,
    val puntosPorKg: Int
)

// Estadísticas reales del usuario.
data class EstadisticasReciclaje(
    val puntos: Int = 0,
    val kilogramos: Double = 0.0,
    val reciclajes: Int = 0
)

// Patrón Repository.
class RecyclingRepository(context: Context) {

    private val database =
        EcoReciclaDatabase.obtenerInstancia(context)

    // Consultar materiales almacenados en SQLite.
    suspend fun obtenerMateriales(): List<MaterialReciclable> =
        withContext(Dispatchers.IO) {

            val materiales = mutableListOf<MaterialReciclable>()

            database.readableDatabase.rawQuery(
                """
                SELECT id, nombre, puntos_por_kg
                FROM materiales
                ORDER BY nombre
                """.trimIndent(),
                null
            ).use { cursor ->

                while (cursor.moveToNext()) {

                    materiales.add(
                        MaterialReciclable(
                            id = cursor.getLong(0),
                            nombre = cursor.getString(1),
                            puntosPorKg = cursor.getInt(2)
                        )
                    )
                }
            }

            materiales
        }

    // Calcular puntos, registrar reciclaje y actualizar saldo.
    suspend fun registrarReciclaje(
        usuarioId: Long,
        materialId: Long,
        peso: Double
    ): Int = withContext(Dispatchers.IO) {

        require(peso.isFinite() && peso > 0 && peso <= 1000) {
            "Ingresa un peso válido entre 0 y 1000 kg"
        }

        val db = database.writableDatabase

        // Transacción: todas las operaciones deben completarse.
        db.beginTransaction()

        try {

            val tarifa = db.rawQuery(
                """
                SELECT puntos_por_kg
                FROM materiales
                WHERE id = ?
                """.trimIndent(),
                arrayOf(materialId.toString())
            ).use { cursor ->

                check(cursor.moveToFirst()) {
                    "Material no encontrado"
                }

                cursor.getInt(0)
            }

            val puntosGanados =
                floor(peso * tarifa).toInt()

            val saldo = db.rawQuery(
                "SELECT puntos FROM usuarios WHERE id = ?",
                arrayOf(usuarioId.toString())
            ).use { cursor ->

                check(cursor.moveToFirst()) {
                    "Usuario no encontrado"
                }

                cursor.getLong(0)
            }

            check(saldo + puntosGanados <= Int.MAX_VALUE) {
                "El saldo supera el límite permitido"
            }

            // Guardar el registro de reciclaje.
            val valores = ContentValues().apply {

                put("usuario_id", usuarioId)
                put("material_id", materialId)
                put("peso", peso)
                put("puntos_ganados", puntosGanados)
            }

            db.insertOrThrow(
                "reciclajes",
                null,
                valores
            )

            // Actualizar los puntos del usuario.
            db.execSQL(
                """
                UPDATE usuarios
                SET puntos = puntos + ?
                WHERE id = ?
                """.trimIndent(),
                arrayOf<Any>(
                    puntosGanados,
                    usuarioId
                )
            )

            // Confirmar la transacción.
            db.setTransactionSuccessful()

            puntosGanados

        } finally {

            // Si ocurre un error, SQLite revierte la operación.
            db.endTransaction()
        }
    }

    // Obtener estadísticas reales para el Dashboard.
    suspend fun obtenerEstadisticas(
        usuarioId: Long
    ): EstadisticasReciclaje =
        withContext(Dispatchers.IO) {

            database.readableDatabase.rawQuery(
                """
                SELECT
                    u.puntos,
                    COALESCE(SUM(r.peso), 0),
                    COUNT(r.id)
                FROM usuarios u
                LEFT JOIN reciclajes r
                    ON r.usuario_id = u.id
                WHERE u.id = ?
                GROUP BY u.id
                """.trimIndent(),
                arrayOf(usuarioId.toString())
            ).use { cursor ->

                check(cursor.moveToFirst()) {
                    "Usuario no encontrado"
                }

                EstadisticasReciclaje(
                    puntos = cursor.getInt(0),
                    kilogramos = cursor.getDouble(1),
                    reciclajes = cursor.getInt(2)
                )
            }
        }
}