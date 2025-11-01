package com.example.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.db.entity.Cliente

@Dao
interface ClienteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(cliente: Cliente)

    @Query("SELECT * FROM clientes")
    suspend fun getTodos(): List<Cliente>
}
