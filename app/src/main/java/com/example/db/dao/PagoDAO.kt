package com.example.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.db.entity.Pago

@Dao
interface PagoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(pago: Pago): Long

    @Query("SELECT * FROM pagos WHERE cliente_id = :clienteId")
    suspend fun pagosPorCliente(clienteId: Int): List<Pago>

    //pbtener ultimo pago por cliente Actualizamos
    @Query("SELECT * FROM pagos WHERE cliente_id = :clienteId ORDER BY fechaPago DESC LIMIT 1")
    suspend fun getUltimoPagoPorCliente(clienteId: Int): Pago?

    // registramos pago


}
