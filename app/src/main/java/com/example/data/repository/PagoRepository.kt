package com.example.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.data.db.DatabaseHelper
import com.example.data.model.ClienteConDeuda
import com.example.data.model.Pago
import com.example.data.model.TipoPago
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PagoRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    private val clienteRepository = ClienteRepository(context)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Registra un pago en la base
    fun registrarPago(pago: Pago): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PAGO_CLIENTE_ID, pago.clienteId)
            put(DatabaseHelper.COLUMN_PAGO_MONTO, pago.monto)
            put(DatabaseHelper.COLUMN_PAGO_CONCEPTO, pago.concepto)
            put(DatabaseHelper.COLUMN_PAGO_FECHA, dateFormat.format(Date()))
            put(DatabaseHelper.COLUMN_PAGO_FECHA_VENCIMIENTO, pago.fechaVencimiento)
            put(DatabaseHelper.COLUMN_PAGO_TIPO, pago.tipoPago.name.lowercase())
        }

        return try {
            db.insert(DatabaseHelper.TABLE_PAGOS, null, values)
        } catch (e: Exception) {
            -1
        } finally {
            db.close()
        }
    }

    // Obtiene el último pago de un cliente
    fun obtenerUltimoPagoPorCliente(clienteId: Int): Pago? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PAGOS,
            null,
            "${DatabaseHelper.COLUMN_PAGO_CLIENTE_ID} = ?",
            arrayOf(clienteId.toString()),
            null,
            null,
            "${DatabaseHelper.COLUMN_PAGO_FECHA} DESC",
            "1"
        )

        return try {
            if (cursor.moveToFirst()) {
                cursorAPago(cursor)
            } else null
        } finally {
            cursor.close()
            db.close()
        }
    }

    // Clientes con pagos vencidos
    fun obtenerClientesConCuotaVencida(): List<ClienteConDeuda> {
        val clientesConDeuda = mutableListOf<ClienteConDeuda>()
        val clientes = clienteRepository.obtenerTodosLosClientes()

        // Fecha actual sin hora
        val hoy = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        Log.d("PagoRepository", "Fecha de hoy para comparación: ${dateOnlyFormat.format(hoy)}")

        for (cliente in clientes) {
            val ultimoPago = obtenerUltimoPagoPorCliente(cliente.id)

            if (ultimoPago != null) {
                try {
                    // Extraer solo la fecha (yyyy-MM-dd)
                    val fechaVencimientoStr = ultimoPago.fechaVencimiento.substring(0, 10)
                    val fechaVencimiento = dateOnlyFormat.parse(fechaVencimientoStr)

                    if (fechaVencimiento != null) {
                        // Normalizar fecha de vencimiento (sin hora)
                        val fechaVencimientoNormalizada = Calendar.getInstance().apply {
                            time = fechaVencimiento
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.time

                        Log.d("PagoRepository", "Cliente: ${cliente.nombre}, " +
                                "Vencimiento: $fechaVencimientoStr, " +
                                "Hoy: ${dateOnlyFormat.format(hoy)}")

                        // Verificar si está vencido
                        if (fechaVencimientoNormalizada.before(hoy)) {
                            val diasVencido = TimeUnit.DAYS.convert(
                                hoy.time - fechaVencimientoNormalizada.time,
                                TimeUnit.MILLISECONDS
                            ).toInt()

                            Log.d("PagoRepository", "✓ VENCIDO - ${cliente.nombre}, " +
                                    "Días: $diasVencido")

                            clientesConDeuda.add(
                                ClienteConDeuda(
                                    cliente = cliente,
                                    ultimoPago = ultimoPago,
                                    diasVencido = diasVencido
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PagoRepository", "Error procesando cliente ${cliente.nombre}: ${e.message}")
                }
            }
        }

        return clientesConDeuda
    }

    // Clientes que nunca pagaron
    fun obtenerClientesSinPagos(): List<ClienteConDeuda> {
        val clientesConDeuda = mutableListOf<ClienteConDeuda>()

        try {
            val clientes = clienteRepository.obtenerTodosLosClientes()
            val db = dbHelper.readableDatabase

            // IDs de clientes con al menos un pago
            val query = """
                SELECT DISTINCT ${DatabaseHelper.COLUMN_PAGO_CLIENTE_ID} 
                FROM ${DatabaseHelper.TABLE_PAGOS}
            """.trimIndent()

            val cursor = db.rawQuery(query, null)
            val clientesConPagos = mutableSetOf<Int>()

            while (cursor.moveToNext()) {
                val clienteId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGO_CLIENTE_ID))
                clientesConPagos.add(clienteId)
            }

            cursor.close()
            db.close()

            // Filtramos los clientes que no tienen pagos
            val clientesSinPagos = clientes.filter { it.id !in clientesConPagos }

            Log.d("VerDeudores", "Total clientes sin pagos: ${clientesSinPagos.size}")

            // Creamos ClienteConDeuda con valores por defecto
            for (cliente in clientesSinPagos) {
                clientesConDeuda.add(
                    ClienteConDeuda(
                        cliente = cliente,
                        ultimoPago = null,
                        diasVencido = 0
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("VerDeudores", "Error al obtener clientes sin pagos: ${e.message}")
            e.printStackTrace()
        }

        return clientesConDeuda
    }

    // Método para obtener todos los deudores (vencidos + sin pagos)
    fun obtenerTodosLosDeudores(): List<ClienteConDeuda> {
        val deudores = mutableListOf<ClienteConDeuda>()
        deudores.addAll(obtenerClientesConCuotaVencida())
        deudores.addAll(obtenerClientesSinPagos())

        Log.d("VerDeudores", "Total deudores combinados: ${deudores.size}")
        return deudores
    }

    // Calcula fecha de vencimiento según tipo de pago
    fun calcularFechaVencimiento(tipoPago: TipoPago): String {
        val calendar = Calendar.getInstance()
        when (tipoPago) {
            TipoPago.MENSUAL -> calendar.add(Calendar.MONTH, 1)
            TipoPago.DIARIA -> calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dateOnlyFormat.format(calendar.time)
    }

    // Convierte cursor a Pago
    private fun cursorAPago(cursor: Cursor): Pago {
        return Pago(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGO_ID)),
            clienteId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGO_CLIENTE_ID)),
            monto = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGO_MONTO)),
            concepto = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGO_CONCEPTO)),
            fechaPago = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGO_FECHA)),
            fechaVencimiento = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGO_FECHA_VENCIMIENTO)),
            tipoPago = TipoPago.fromString(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGO_TIPO))
            )
        )
    }
}