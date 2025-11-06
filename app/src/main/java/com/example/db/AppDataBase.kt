package com.example.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase


import com.example.db.dao.ClienteDao
import com.example.db.dao.PagoDao
import com.example.db.entity.Usuario
import com.example.db.entity.Pago
import com.example.db.entity.Cliente
import com.example.db.dao.UsuarioDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@Database(
    entities = [Usuario::class, Cliente::class, Pago::class],
    version = 3
)
@TypeConverters(TipoClienteConverter::class)
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
                    .fallbackToDestructiveMigration() // para pruebas con borrado de db
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                runBlocking {
                                    val dao = INSTANCE?.usuarioDao()
                                    dao?.insertar(Usuario(nombre = "Administrador", username = "admin", password = "admin123"))
                                    dao?.insertar(Usuario(nombre = "Empleado", username = "emp", password = "emp123"))
                                    Log.d("AppDatabase", "Usuarios por defecto insertados")
                                }


                            }
                        }
                    }

                    )
                    .build().also { INSTANCE = it }
            }
    }
}