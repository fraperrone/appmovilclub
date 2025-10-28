package com.example.layouts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Si no vas a mostrar layout, podés omitir el setContentView,
        // pero algunas versiones de AS necesitan un contexto inicial.
        // setContentView(R.layout.activity_main)


        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
