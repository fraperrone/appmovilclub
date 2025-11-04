package com.example.layouts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.db.AppDatabase
import com.example.db.entity.Usuario
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Si no vas a mostrar layout, podés omitir el setContentView,
        // pero algunas versiones de AS necesitan un contexto inicial.



         setContentView(R.layout.activity_main)


        //hacer prueba de db
        val db = AppDatabase.getInstance(this)
        val usuarioDao = db.usuarioDao()

        lifecycleScope.launch {
            val usuarios = usuarioDao.getTodos()
            Log.d("MainActivity", "Usuarios: $usuarios")
        }


        //des habilitamos temporalmente el resto de la app

        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
