package com.example.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.data.db.DatabaseHelper
import com.example.data.model.Cliente
import com.example.data.model.TipoCliente
import java.text.SimpleDateFormat
import java.util.*

class ClienteRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun insertarCliente(cliente: Cliente): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOMBRE, cliente.nombre)
            put(DatabaseHelper.COLUMN_APELLIDO, cliente.apellido)
            put(DatabaseHelper.COLUMN_DOCUMENTO, cliente.documento)
            put(DatabaseHelper.COLUMN_TIPO_CLIENTE, cliente.tipoCliente.name.lowercase())
            put(DatabaseHelper.COLUMN_FECHA_REGISTRO, dateFormat.format(Date()))
        }

        return try {
            db.insert(DatabaseHelper.TABLE_CLIENTES, null, values)
        } catch (e: Exception) {
            -1
        } finally {
            db.close()
        }
    }

    fun obtenerClientePorDocumento(documento: String): Cliente? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CLIENTES,
            null,
            "${DatabaseHelper.COLUMN_DOCUMENTO} = ?",
            arrayOf(documento),
            null,
            null,
            null
        )

        return try {
            if (cursor.moveToFirst()) {
                cursorACliente(cursor)
            } else null
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun obtenerClientePorId(id: Int): Cliente? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CLIENTES,
            null,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        return try {
            if (cursor.moveToFirst()) {
                cursorACliente(cursor)
            } else null
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun obtenerTodosLosClientes(): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CLIENTES,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COLUMN_APELLIDO} ASC"
        )

        try {
            if (cursor.moveToFirst()) {
                do {
                    clientes.add(cursorACliente(cursor))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
            db.close()
        }

        return clientes
    }

    private fun cursorACliente(cursor: Cursor): Cliente {
        return Cliente(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOMBRE)),
            apellido = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_APELLIDO)),
            documento = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOCUMENTO)),
            tipoCliente = TipoCliente.fromString(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIPO_CLIENTE))
            ),
            fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FECHA_REGISTRO))
        )
    }
}