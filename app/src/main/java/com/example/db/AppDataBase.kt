package com.example.db

import androidx.room.Database

@Database(
    entities = [Usuario::class, Cliente::class, Pago::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun clienteDao(): ClienteDao
    abstract fun pagoDao(): PagoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ClubDeportivo.db"
                )
                    // .addCallback(...) // para inserts por defecto
                    .build().also { INSTANCE = it }
            }
    }
}