package com.example.data.db

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
        private const val DATABASE_VERSION = 2 // Incrementamos la versión

        // Tabla Usuarios
        const val TABLE_USUARIOS = "usuarios"
        const val COLUMN_USUARIO_ID = "id"
        const val COLUMN_USUARIO_NOMBRE = "nombre"
        const val COLUMN_USUARIO_USERNAME = "username"
        const val COLUMN_USUARIO_PASSWORD = "password"

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
        // Crear tabla usuarios
        val createUsuariosTable = """
            CREATE TABLE $TABLE_USUARIOS (
                $COLUMN_USUARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USUARIO_NOMBRE TEXT NOT NULL,
                $COLUMN_USUARIO_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_USUARIO_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

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

        db?.execSQL(createUsuariosTable)
        db?.execSQL(createClientesTable)
        db?.execSQL(createPagosTable)

        // Insertar usuario por defecto
        db?.execSQL("""
            INSERT INTO $TABLE_USUARIOS ($COLUMN_USUARIO_NOMBRE, $COLUMN_USUARIO_USERNAME, $COLUMN_USUARIO_PASSWORD) 
            VALUES ('Administrador', 'admin', 'admin123')
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createUsuariosTable = """
                CREATE TABLE IF NOT EXISTS $TABLE_USUARIOS (
                    $COLUMN_USUARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_USUARIO_NOMBRE TEXT NOT NULL,
                    $COLUMN_USUARIO_USERNAME TEXT NOT NULL UNIQUE,
                    $COLUMN_USUARIO_PASSWORD TEXT NOT NULL
                )
            """.trimIndent()
            db?.execSQL(createUsuariosTable)

            // Insertar usuario por defecto si no existe
            db?.execSQL("""
                INSERT OR IGNORE INTO $TABLE_USUARIOS ($COLUMN_USUARIO_NOMBRE, $COLUMN_USUARIO_USERNAME, $COLUMN_USUARIO_PASSWORD) 
                VALUES ('Administrador', 'admin', 'admin123')
            """)
        }
    }
}