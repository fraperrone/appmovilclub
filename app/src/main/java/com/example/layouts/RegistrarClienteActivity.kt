package com.example.layouts

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.layouts.data.repository.ClienteRepository
import com.example.layouts.data.model.Cliente
import com.example.layouts.data.model.TipoCliente

class RegistrarClienteActivity : AppCompatActivity() {

    private lateinit var textViewBienvenida: TextView
    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextDocumento: EditText
    private lateinit var radioGroupTipo: RadioGroup
    private lateinit var radioSocio: RadioButton
    private lateinit var radioNoSocio: RadioButton
    private lateinit var buttonGuardar: Button

    private lateinit var clienteRepository: ClienteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cliente)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()

        // Configurar bienvenida
        configurarBienvenida()

        inicializarVistas()
        clienteRepository = ClienteRepository(this)

        configurarBotones()

        // Configurar botones de navegación usando tus helpers
        BotonBackHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
        BotonMenuHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
    }

    private fun configurarBienvenida() {
        textViewBienvenida = findViewById(R.id.textViewBienvenida)
        val userName = SessionManager.getUserName(this)
        textViewBienvenida.text = "Bienvenida, ${userName ?: "Usuario"}"
    }

    private fun inicializarVistas() {
        editTextNombre = findViewById(R.id.editText_nombre)
        editTextApellido = findViewById(R.id.editText_apellido)
        editTextDocumento = findViewById(R.id.editText_documento)
        radioGroupTipo = findViewById(R.id.radioGroup_tipo)
        radioSocio = findViewById(R.id.radio_socio)
        radioNoSocio = findViewById(R.id.radio_no_socio)
        buttonGuardar = findViewById(R.id.buttonSave)
    }

    private fun configurarBotones() {
        buttonGuardar.setOnClickListener {
            if (validarCampos()) {
                registrarCliente()
            }
        }
    }

    private fun validarCampos(): Boolean {
        val nombre = editTextNombre.text.toString().trim()
        val apellido = editTextApellido.text.toString().trim()
        val documento = editTextDocumento.text.toString().trim()

        if (nombre.isEmpty()) {
            editTextNombre.error = "Ingrese el nombre"
            editTextNombre.requestFocus()
            return false
        }

        if (apellido.isEmpty()) {
            editTextApellido.error = "Ingrese el apellido"
            editTextApellido.requestFocus()
            return false
        }

        if (documento.isEmpty()) {
            editTextDocumento.error = "Ingrese el documento"
            editTextDocumento.requestFocus()
            return false
        }

        if (documento.length < 7 || documento.length > 8) {
            editTextDocumento.error = "DNI debe tener 7 u 8 dígitos"
            editTextDocumento.requestFocus()
            return false
        }

        if (radioGroupTipo.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Seleccione el tipo de cliente", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validar que el documento no exista
        val clienteExistente = clienteRepository.obtenerClientePorDocumento(documento)
        if (clienteExistente != null) {
            editTextDocumento.error = "Este documento ya está registrado"
            editTextDocumento.requestFocus()
            return false
        }

        return true
    }

    private fun registrarCliente() {
        val nombre = editTextNombre.text.toString().trim()
        val apellido = editTextApellido.text.toString().trim()
        val documento = editTextDocumento.text.toString().trim()
        val tipoCliente = if (radioSocio.isChecked) TipoCliente.SOCIO else TipoCliente.NO_SOCIO

        val cliente = Cliente(
            nombre = nombre,
            apellido = apellido,
            documento = documento,
            tipoCliente = tipoCliente,
            fechaRegistro = ""
        )

        val resultado = clienteRepository.insertarCliente(cliente)

        if (resultado != -1L) {
            Toast.makeText(
                this,
                "Cliente registrado exitosamente",
                Toast.LENGTH_SHORT
            ).show()
//            limpiarCampos()
            finish()
        } else {
            Toast.makeText(
                this,
                "Error al registrar cliente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun limpiarCampos() {
        editTextNombre.text.clear()
        editTextApellido.text.clear()
        editTextDocumento.text.clear()
        radioGroupTipo.clearCheck()
        editTextNombre.requestFocus()
    }
}