package com.example.layouts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.layouts.data.repository.UsuarioRepository

class LoginActivity : AppCompatActivity() {

    private lateinit var inputUsuario: EditText
    private lateinit var inputContrasenia: EditText
    private lateinit var btnEnviar: Button
    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.hide()

        inicializarVistas()
        usuarioRepository = UsuarioRepository(this)

        btnEnviar.setOnClickListener {
            validarLogin()
        }
    }

    private fun inicializarVistas() {
        inputUsuario = findViewById(R.id.inputUsuario)
        inputContrasenia = findViewById(R.id.inputContrasenia)
        btnEnviar = findViewById(R.id.btnEnviar)
    }

    private fun validarLogin() {
        val username = inputUsuario.text.toString().trim()
        val password = inputContrasenia.text.toString().trim()

        if (username.isEmpty()) {
            inputUsuario.error = "Ingrese el usuario"
            inputUsuario.requestFocus()
            return
        }

        if (password.isEmpty()) {
            inputContrasenia.error = "Ingrese la contraseña"
            inputContrasenia.requestFocus()
            return
        }

        val usuario = usuarioRepository.validarCredenciales(username, password)

        if (usuario != null) {
            // Guardar sesión
            guardarSesion(usuario.id, usuario.nombre)

            // Ir al menú principal
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(
                this,
                "Usuario o contraseña incorrectos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun guardarSesion(userId: Int, userName: String) {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("USER_ID", userId)
            putString("USER_NAME", userName)
            putBoolean("IS_LOGGED_IN", true)
            apply()
        }
    }
}