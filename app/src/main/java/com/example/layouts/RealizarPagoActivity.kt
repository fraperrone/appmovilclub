package com.example.layouts

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.data.repository.ClienteRepository
import com.example.data.repository.PagoRepository
import com.example.data.model.Cliente
import com.example.data.model.Pago
import com.example.data.model.TipoCliente
import com.example.data.model.TipoPago
import java.text.SimpleDateFormat
import java.util.*

class RealizarPagoActivity : AppCompatActivity() {

    private lateinit var textViewBienvenida: TextView
    private lateinit var editTextDocumento: EditText
    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextMonto: EditText
    private lateinit var editTextConcepto: EditText
    private lateinit var buttonEnviar: Button

    private lateinit var clienteRepository: ClienteRepository
    private lateinit var pagoRepository: PagoRepository
    private var clienteActual: Cliente? = null
    private val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realizar_pago)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()

        configurarBienvenida()
        inicializarVistas()
        clienteRepository = ClienteRepository(this)
        pagoRepository = PagoRepository(this)

        configurarBusquedaCliente()
        configurarBotones()

        BotonMenuHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
        BotonBackHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
    }

    private fun configurarBienvenida() {
        textViewBienvenida = findViewById(R.id.textViewBienvenida)
        val userName = SessionManager.getUserName(this)
        textViewBienvenida.text = "Bienvenida, ${userName ?: "Usuario"}"
    }

    private fun inicializarVistas() {
        editTextDocumento = findViewById(R.id.editText_documento)
        editTextNombre = findViewById(R.id.editText_nombre)
        editTextApellido = findViewById(R.id.editText_apellido)
        editTextMonto = findViewById(R.id.editText_monto)
        editTextConcepto = findViewById(R.id.editText_concepto)
        buttonEnviar = findViewById(R.id.button_enviar)

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

            val monto = when (clienteActual!!.tipoCliente) {
                TipoCliente.SOCIO -> "25000"
                TipoCliente.NO_SOCIO -> "3000"
            }
            editTextMonto.setText(monto)

            val concepto = when (clienteActual!!.tipoCliente) {
                TipoCliente.SOCIO -> "Cuota Mensual"
                TipoCliente.NO_SOCIO -> "Cuota Diaria"
            }
            editTextConcepto.setText(concepto)
        } else {
            limpiarDatosCliente()
            if (documento.length >= 7) {
                Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
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
            if (validarCampos() && validarPagoActivo()) {
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

    private fun validarPagoActivo(): Boolean {
        val ultimoPago = pagoRepository.obtenerUltimoPagoPorCliente(clienteActual!!.id)

        if (ultimoPago != null) {
            try {
                // Parsear fecha de vencimiento
                val fechaVencimiento = try {
                    dateOnlyFormat.parse(ultimoPago.fechaVencimiento)
                } catch (e: Exception) {
                    val fullFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    fullFormat.parse(ultimoPago.fechaVencimiento)
                }

                val hoy = dateOnlyFormat.parse(dateOnlyFormat.format(Date()))

                if (fechaVencimiento != null && hoy != null) {
                    if (fechaVencimiento.after(hoy) || fechaVencimiento == hoy) {
                        // El pago aún está vigente
                        val mensaje = when (clienteActual!!.tipoCliente) {
                            TipoCliente.SOCIO ->
                                "Este cliente tiene un pago mensual activo hasta ${ultimoPago.fechaVencimiento}. No puede realizar otro pago hasta que venza."
                            TipoCliente.NO_SOCIO ->
                                "Este cliente ya pagó hoy. Debe esperar hasta mañana para realizar otro pago."
                        }

                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                        return false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return true
    }

    private fun registrarPago() {
        val monto = editTextMonto.text.toString().toDouble()
        val concepto = editTextConcepto.text.toString().trim()

        val tipoPago = when (clienteActual!!.tipoCliente) {
            TipoCliente.SOCIO -> TipoPago.MENSUAL
            TipoCliente.NO_SOCIO -> TipoPago.DIARIA
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
            finish()
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