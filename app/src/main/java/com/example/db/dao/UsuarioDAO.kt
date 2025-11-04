package com.example.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.db.entity.Pago
import com.example.db.entity.Usuario

@Dao
interface UsuarioDao {
    //get todos
    @Query("SELECT * FROM usuarios")
    suspend fun getTodos(): List<Usuario>

    //insertar
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(usuario: Usuario)

    //validar credenciales
    @Query("SELECT * FROM usuarios WHERE username = :username AND password = :password")
    suspend fun validarCredenciales(username: String, password: String): Usuario?
}
