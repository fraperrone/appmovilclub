package com.example.layouts

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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

            finish()
        }


        val btnVerContainer = findViewById<ConstraintLayout>(R.id.btnVerContainer)
        btnVerContainer.setOnClickListener {
            val intent = Intent(this, VerAfiliadosActivity::class.java)
            startActivity(intent)

            finish()
        }

        val btnRegistrarContainer = findViewById<ConstraintLayout>(R.id.btnRegistrarContainer)
        btnRegistrarContainer.setOnClickListener {
            val intent = Intent(this, RegistrarClienteActivity::class.java)
            startActivity(intent)

            finish()
        }

        //btnDeudoresContainer
        val btnDeudoresContainer = findViewById<ConstraintLayout>(R.id.btnDeudoresContainer)
        btnDeudoresContainer.setOnClickListener {
            val intent = Intent(this, VerDeudoresActivity::class.java)
            startActivity(intent)

            finish()
        }


        // Callback moderna para interceptar el botón "Atrás" y cerrar sesion
        //


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mostrarDialogoCerrarSesion()
            }

            private fun mostrarDialogoCerrarSesion() {
                val builder = AlertDialog.Builder(this@MenuPrincipalActivity)
                builder.setTitle("Cerrar sesión")
                builder.setMessage("¿Deseás cerrar sesión y salir de la app?")
                builder.setCancelable(false) // Evita que se cierre tocando fuera del modal
                builder.setPositiveButton("Sí") { _, _ ->
                    // cerrarSesion()
                    finish() // Cierra la actividad actual
                }
                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss() // Cierra solo el modal
                }
                builder.show()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)




    }


}