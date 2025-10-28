package com.example.layouts

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat

class MenuPrincipalActivity : AppCompatActivity() {

    private lateinit var textViewBienvenida: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.hide()

        // Configurar mensaje de bienvenida
        configurarBienvenida()

        // Configurar botones del menú
        configurarBotones()

        // Configurar botón de salida
        configurarBotonSalida()

        // Callback para botón atrás del sistema
        configurarCallbackAtras()
    }

    private fun configurarBienvenida() {
        textViewBienvenida = findViewById(R.id.textViewBienvenida)
        val userName = SessionManager.getUserName(this)
        textViewBienvenida.text = "Bienvenida, ${userName ?: "Usuario"}"
    }

    private fun configurarBotones() {
        val btnPagoContainer = findViewById<ConstraintLayout>(R.id.btnPagoContainer)
        btnPagoContainer.setOnClickListener {
            val intent = Intent(this, RealizarPagoActivity::class.java)
            startActivity(intent)
        }

        val btnVerContainer = findViewById<ConstraintLayout>(R.id.btnVerContainer)
        btnVerContainer.setOnClickListener {
            val intent = Intent(this, VerAfiliadosActivity::class.java)
            startActivity(intent)
        }

        val btnRegistrarContainer = findViewById<ConstraintLayout>(R.id.btnRegistrarContainer)
        btnRegistrarContainer.setOnClickListener {
            val intent = Intent(this, RegistrarClienteActivity::class.java)
            startActivity(intent)
        }

        val btnDeudoresContainer = findViewById<ConstraintLayout>(R.id.btnDeudoresContainer)
        btnDeudoresContainer.setOnClickListener {
            val intent = Intent(this, VerDeudoresActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarBotonSalida() {
        val textCerrarSesion = findViewById<TextView>(R.id.textCerrarSesion)
        textCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
    }


    private fun configurarCallbackAtras() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mostrarDialogoCerrarSesion()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun mostrarDialogoCerrarSesion() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cerrar sesión")
        builder.setMessage("¿Deseás cerrar sesión y salir de la app?")
        builder.setCancelable(false)
        builder.setPositiveButton("Sí") { _, _ ->
            cerrarSesion()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun cerrarSesion() {
        SessionManager.clearSession(this)
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}