package com.example.layouts

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class VerAfiliadosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_afiliados)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //configuracion boton menu principal
        BotonMenuHelper.configurarBotonMenu(this, findViewById(android.R.id.content))



//        val btnMenu = findViewById<ImageView>(R.id.btn_menu)
//        btnMenu.setOnClickListener {
//            val intent = Intent(this, MenuPrincipalActivity::class.java)
//            startActivity(intent)
//        }
    }
}