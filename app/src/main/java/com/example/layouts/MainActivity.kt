package com.example.layouts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Abrir LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        // Opcional: cerrar MainActivity si no querés que vuelva atrás
        finish()
    }

}