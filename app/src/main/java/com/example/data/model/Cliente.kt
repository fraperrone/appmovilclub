package com.example.data.model

data class Cliente(
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val documento: String,
    val tipoCliente: TipoCliente,
    val fechaRegistro: String
)

enum class TipoCliente(val displayName: String) {
    SOCIO("Socio"),
    NO_SOCIO("No Socio");

    companion object {
        fun fromString(value: String): TipoCliente {
            return when (value.lowercase()) {
                "socio" -> SOCIO
                "no_socio", "no socio" -> NO_SOCIO
                else -> NO_SOCIO
            }
        }
    }
}

data class Pago(
    val id: Int = 0,
    val clienteId: Int,
    val monto: Double,
    val concepto: String,
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

data class ClienteConDeuda(
    val cliente: Cliente,
    val ultimoPago: Pago?,
    val diasVencido: Int
)