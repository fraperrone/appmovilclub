package com.example.layouts.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.layouts.data.db.DatabaseHelper
import com.example.layouts.data.model.ClienteConDeuda
import com.example.layouts.data.model.Pago
import com.example.layouts.data.model.TipoPago
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PagoRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val clienteRepository = ClienteRepository(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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

    fun obtenerClientesConCuotaVencida(): List<ClienteConDeuda> {
        val clientesConDeuda = mutableListOf<ClienteConDeuda>()
        val clientes = clienteRepository.obtenerTodosLosClientes()
        val fechaHoy = dateOnlyFormat.format(Date())

        for (cliente in clientes) {
            val ultimoPago = obtenerUltimoPagoPorCliente(cliente.id)

            if (ultimoPago != null) {
                try {
                    val fechaVencimiento = dateOnlyFormat.parse(ultimoPago.fechaVencimiento)
                    val hoy = dateOnlyFormat.parse(fechaHoy)

                    if (fechaVencimiento != null && hoy != null && fechaVencimiento.before(hoy)) {
                        val diasVencido = TimeUnit.DAYS.convert(
                            hoy.time - fechaVencimiento.time,
                            TimeUnit.MILLISECONDS
                        ).toInt()

                        clientesConDeuda.add(
                            ClienteConDeuda(
                                cliente = cliente,
                                ultimoPago = ultimoPago,
                                diasVencido = diasVencido
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return clientesConDeuda
    }

    fun calcularFechaVencimiento(tipoPago: TipoPago): String {
        val calendar = Calendar.getInstance()
        when (tipoPago) {
            TipoPago.MENSUAL -> calendar.add(Calendar.MONTH, 1)
            TipoPago.DIARIA -> calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dateOnlyFormat.format(calendar.time)
    }

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