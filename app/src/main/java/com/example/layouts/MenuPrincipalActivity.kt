package com.example.layouts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.constraintlayout.widget.ConstraintLayout

class MenuPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //hacemos que boton realizar pago llame a la actividad realizar pago
        //val btnRealizarPago = findViewById<Button>(R.id.btnPagoContainer)
        //btnRealizarPago.setOnClickListener {
        //    val intent = Intent(this, RealizarPago::class.java)
        //    startActivity(intent)
        //}

        //btnPagoContainer
        val btnPagoContainer = findViewById<ConstraintLayout>(R.id.btnPagoContainer)

        btnPagoContainer.setOnClickListener {
            val intent = Intent(this, RealizarPagoActivity::class.java) // o PagoActivity::class.java
            startActivity(intent)
        }


        val btnVerContainer = findViewById<ConstraintLayout>(R.id.btnVerContainer)
        btnVerContainer.setOnClickListener {
            val intent = Intent(this, MostrarAfiliadosActivity::class.java)
            startActivity(intent)
        }

        val btnRegistrarContainer = findViewById<ConstraintLayout>(R.id.btnRegistrarContainer)
        btnRegistrarContainer.setOnClickListener {
            val intent = Intent(this, RegistrarClienteActivity::class.java)
            startActivity(intent)
        }

        //btnDeudoresContainer
        val btnDeudoresContainer = findViewById<ConstraintLayout>(R.id.btnDeudoresContainer)
        btnDeudoresContainer.setOnClickListener {
            val intent = Intent(this, MostrarDeudoresActivity::class.java)
            startActivity(intent)
        }

    }
}