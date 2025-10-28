package com.example.layouts.data.repository

import android.content.Context
import android.database.Cursor
import com.example.layouts.data.db.DatabaseHelper
import com.example.layouts.data.model.Usuario

class UsuarioRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun validarCredenciales(username: String, password: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USUARIOS,
            null,
            "${DatabaseHelper.COLUMN_USUARIO_USERNAME} = ? AND ${DatabaseHelper.COLUMN_USUARIO_PASSWORD} = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )

        return try {
            if (cursor.moveToFirst()) {
                cursorAUsuario(cursor)
            } else null
        } finally {
            cursor.close()
            db.close()
        }
    }

    private fun cursorAUsuario(cursor: Cursor): Usuario {
        return Usuario(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID)),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_NOMBRE)),
            username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_USERNAME)),
            password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_PASSWORD))
        )
    }
}