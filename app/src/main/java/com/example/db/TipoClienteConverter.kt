package com.example.db

import androidx.room.TypeConverter
import com.example.db.entity.TipoCliente

class TipoClienteConverter {
    @TypeConverter
    fun fromTipoCliente(value: TipoCliente): String {
        return value.name
    }

    @TypeConverter
    fun toTipoCliente(value: String): TipoCliente {
        return TipoCliente.valueOf(value)
    }
}