package com.example.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class Cliente(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val documento: String,
    val tipoCliente: TipoCliente,
    val fechaRegistro: String
)
//agregamos tipo cliente enum class

enum class TipoCliente {
    SOCIO,
    NO_SOCIO
}
