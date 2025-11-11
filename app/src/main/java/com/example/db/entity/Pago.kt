package com.example.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pagos",
    foreignKeys = [ForeignKey(
        entity = Cliente::class,
        parentColumns = ["id"],
        childColumns = ["cliente_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["cliente_id"])]

)
data class Pago(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cliente_id") val clienteId: Int,
    val monto: Double,
    val concepto: String?,
    val fechaPago: String,
    val fechaVencimiento: String,
    val tipoPago: TipoPago
)


enum class TipoPago(val displayName: String) {
    MENSUAL("Cuota Mensual"),
    DIARIA("Cuota Diaria");

    companion object {
        fun fromString(value: String): TipoPago {
            return when (value.lowercase()) {
                "mensual" -> MENSUAL
                "diaria" -> DIARIA
                else -> DIARIA
            }
        }
    }
}