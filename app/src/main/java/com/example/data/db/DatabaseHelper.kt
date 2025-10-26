package com.example.layouts.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "ClubDeportivo.db"
        private const val DATABASE_VERSION = 1

        // Tabla Clientes
        const val TABLE_CLIENTES = "clientes"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_APELLIDO = "apellido"
        const val COLUMN_DOCUMENTO = "documento"
        const val COLUMN_TIPO_CLIENTE = "tipo_cliente"
        const val COLUMN_FECHA_REGISTRO = "fecha_registro"

        // Tabla Pagos
        const val TABLE_PAGOS = "pagos"
        const val COLUMN_PAGO_ID = "id"
        const val COLUMN_PAGO_CLIENTE_ID = "cliente_id"
        const val COLUMN_PAGO_MONTO = "monto"
        const val COLUMN_PAGO_CONCEPTO = "concepto"
        const val COLUMN_PAGO_FECHA = "fecha_pago"
        const val COLUMN_PAGO_FECHA_VENCIMIENTO = "fecha_vencimiento"
        const val COLUMN_PAGO_TIPO = "tipo_pago"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createClientesTable = """
            CREATE TABLE $TABLE_CLIENTES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_APELLIDO TEXT NOT NULL,
                $COLUMN_DOCUMENTO TEXT NOT NULL UNIQUE,
                $COLUMN_TIPO_CLIENTE TEXT NOT NULL,
                $COLUMN_FECHA_REGISTRO TEXT NOT NULL
            )
        """.trimIndent()

        val createPagosTable = """
            CREATE TABLE $TABLE_PAGOS (
                $COLUMN_PAGO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PAGO_CLIENTE_ID INTEGER NOT NULL,
                $COLUMN_PAGO_MONTO REAL NOT NULL,
                $COLUMN_PAGO_CONCEPTO TEXT,
                $COLUMN_PAGO_FECHA TEXT NOT NULL,
                $COLUMN_PAGO_FECHA_VENCIMIENTO TEXT NOT NULL,
                $COLUMN_PAGO_TIPO TEXT NOT NULL,
                FOREIGN KEY($COLUMN_PAGO_CLIENTE_ID) REFERENCES $TABLE_CLIENTES($COLUMN_ID)
            )
        """.trimIndent()

        db?.execSQL(createClientesTable)
        db?.execSQL(createPagosTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PAGOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTES")
        onCreate(db)
    }
}