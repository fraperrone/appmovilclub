package com.example.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "pagos",
    foreignKeys = [ForeignKey(
        entity = Cliente::class,
        parentColumns = ["id"],
        childColumns = ["cliente_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Pago(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cliente_id") val clienteId: Int,
    val monto: Double,
    val concepto: String?,
    val fecha_pago: String,
    val fecha_vencimiento: String,
    val tipo_pago: String
)
