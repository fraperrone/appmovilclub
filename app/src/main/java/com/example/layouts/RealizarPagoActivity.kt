package com.example.layouts

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.layouts.data.repository.ClienteRepository
import com.example.layouts.data.repository.PagoRepository
import com.example.layouts.data.model.Cliente
import com.example.layouts.data.model.Pago
import com.example.layouts.data.model.TipoPago

class RealizarPagoActivity : AppCompatActivity() {

    private lateinit var editTextDocumento: EditText
    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextMonto: EditText
    private lateinit var editTextConcepto: EditText
    private lateinit var buttonEnviar: Button

    private lateinit var clienteRepository: ClienteRepository
    private lateinit var pagoRepository: PagoRepository
    private var clienteActual: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realizar_pago)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()

        inicializarVistas()
        clienteRepository = ClienteRepository(this)
        pagoRepository = PagoRepository(this)

        configurarBusquedaCliente()
        configurarBotones()

        // Configurar botones de navegación
        BotonMenuHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
        BotonBackHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
    }

    private fun inicializarVistas() {
        editTextDocumento = findViewById(R.id.editText_documento)
        editTextNombre = findViewById(R.id.editText_nombre)
        editTextApellido = findViewById(R.id.editText_apellido)
        editTextMonto = findViewById(R.id.editText_monto)
        editTextConcepto = findViewById(R.id.editText_concepto)
        buttonEnviar = findViewById(R.id.button_enviar)

        // Los campos de nombre y apellido son solo lectura
        editTextNombre.isEnabled = false
        editTextApellido.isEnabled = false
    }

    private fun configurarBusquedaCliente() {
        editTextDocumento.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val documento = s.toString().trim()
                if (documento.length >= 7) {
                    buscarCliente(documento)
                } else {
                    limpiarDatosCliente()
                }
            }
        })
    }

    private fun buscarCliente(documento: String) {
        clienteActual = clienteRepository.obtenerClientePorDocumento(documento)

        if (clienteActual != null) {
            editTextNombre.setText(clienteActual!!.nombre)
            editTextApellido.setText(clienteActual!!.apellido)

            // Establecer monto según tipo de cliente
            val monto = when (clienteActual!!.tipoCliente) {
                com.example.layouts.data.model.TipoCliente.SOCIO -> "5000"
                com.example.layouts.data.model.TipoCliente.NO_SOCIO -> "1000"
            }
            editTextMonto.setText(monto)

            val concepto = when (clienteActual!!.tipoCliente) {
                com.example.layouts.data.model.TipoCliente.SOCIO -> "Cuota Mensual"
                com.example.layouts.data.model.TipoCliente.NO_SOCIO -> "Cuota Diaria"
            }
            editTextConcepto.setText(concepto)
        } else {
            limpiarDatosCliente()
            if (documento.length >= 7) {
                Toast.makeText(
                    this,
                    "Cliente no encontrado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun limpiarDatosCliente() {
        clienteActual = null
        editTextNombre.text.clear()
        editTextApellido.text.clear()
        editTextMonto.text.clear()
        editTextConcepto.text.clear()
    }

    private fun configurarBotones() {
        buttonEnviar.setOnClickListener {
            if (validarCampos()) {
                registrarPago()
            }
        }
    }

    private fun validarCampos(): Boolean {
        if (clienteActual == null) {
            Toast.makeText(this, "Debe buscar un cliente válido", Toast.LENGTH_SHORT).show()
            editTextDocumento.requestFocus()
            return false
        }

        val monto = editTextMonto.text.toString().trim()
        if (monto.isEmpty()) {
            editTextMonto.error = "Ingrese el monto"
            editTextMonto.requestFocus()
            return false
        }

        try {
            monto.toDouble()
        } catch (e: NumberFormatException) {
            editTextMonto.error = "Monto inválido"
            editTextMonto.requestFocus()
            return false
        }

        val concepto = editTextConcepto.text.toString().trim()
        if (concepto.isEmpty()) {
            editTextConcepto.error = "Ingrese el concepto"
            editTextConcepto.requestFocus()
            return false
        }

        return true
    }

    private fun registrarPago() {
        val monto = editTextMonto.text.toString().toDouble()
        val concepto = editTextConcepto.text.toString().trim()

        val tipoPago = when (clienteActual!!.tipoCliente) {
            com.example.layouts.data.model.TipoCliente.SOCIO -> TipoPago.MENSUAL
            com.example.layouts.data.model.TipoCliente.NO_SOCIO -> TipoPago.DIARIA
        }

        val fechaVencimiento = pagoRepository.calcularFechaVencimiento(tipoPago)

        val pago = Pago(
            clienteId = clienteActual!!.id,
            monto = monto,
            concepto = concepto,
            fechaPago = "",
            fechaVencimiento = fechaVencimiento,
            tipoPago = tipoPago
        )

        val resultado = pagoRepository.registrarPago(pago)

        if (resultado != -1L) {
            Toast.makeText(
                this,
                "Pago registrado exitosamente\nVencimiento: $fechaVencimiento",
                Toast.LENGTH_LONG
            ).show()
            limpiarFormulario()
        } else {
            Toast.makeText(
                this,
                "Error al registrar el pago",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun limpiarFormulario() {
        editTextDocumento.text.clear()
        limpiarDatosCliente()
        editTextDocumento.requestFocus()
    }
}