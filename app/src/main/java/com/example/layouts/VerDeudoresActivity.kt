package com.example.layouts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.layouts.data.repository.PagoRepository
import com.example.layouts.data.model.ClienteConDeuda
import com.example.layouts.data.repository.ClienteRepository

class VerDeudoresActivity : AppCompatActivity() {

    private lateinit var textViewBienvenida: TextView
    private lateinit var buttonMostrar: Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var pagoRepository: PagoRepository
    private lateinit var adapter: DeudoresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_deudores)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()

        // Configurar bienvenida
        configurarBienvenida()

        inicializarVistas()
        pagoRepository = PagoRepository(this)

        configurarRecyclerView()
        configurarBotones()

        // Configurar botones de navegación
        BotonBackHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
        BotonMenuHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
    }

    private fun configurarBienvenida() {
        textViewBienvenida = findViewById(R.id.textViewBienvenida)
        val userName = SessionManager.getUserName(this)
        textViewBienvenida.text = "Bienvenida, ${userName ?: "Usuario"}"
    }

    private fun inicializarVistas() {
        buttonMostrar = findViewById(R.id.buttonSave)
        recyclerView = findViewById(R.id.recyclerView_deudores)
    }

    private fun configurarRecyclerView() {
        adapter = DeudoresAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun configurarBotones() {
        buttonMostrar.setOnClickListener {
            mostrarDeudores()
        }
    }

    private fun mostrarDeudores() {
        Log.d("VerDeudores", "=== Iniciando búsqueda de deudores ===")

        // Obtener deudores
        val clientesConCuotaVencida = pagoRepository.obtenerClientesConCuotaVencida()
        val clientesSinPagos = pagoRepository.obtenerClientesSinPagos()
        val deudoresTotales = pagoRepository.obtenerTodosLosDeudores()

        Log.d("VerDeudores", "=== RESULTADOS ===")
        Log.d("VerDeudores", "Clientes con cuota vencida: ${clientesConCuotaVencida.size}")
        Log.d("VerDeudores", "Clientes sin pagos: ${clientesSinPagos.size}")
        Log.d("VerDeudores", "Total deudores: ${deudoresTotales.size}")

        // Mostrar detalles de cada deudor encontrado
        clientesConCuotaVencida.forEach { deudor ->
            Log.d("VerDeudores", "VENCIDO: ${deudor.cliente.nombre} - Días: ${deudor.diasVencido}")
        }

        clientesSinPagos.forEach { deudor ->
            Log.d("VerDeudores", "SIN PAGOS: ${deudor.cliente.nombre}")
        }

        if (deudoresTotales.isEmpty()) {
            Toast.makeText(this, "No hay clientes con deuda", Toast.LENGTH_SHORT).show()
        } else {
            val mensaje = "Vencidos: ${clientesConCuotaVencida.size}, Sin pagos: ${clientesSinPagos.size}"
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }

        adapter.actualizarDatos(deudoresTotales)
    }
}

class DeudoresAdapter(
    private var deudores: List<ClienteConDeuda>
) : RecyclerView.Adapter<DeudoresAdapter.DeudorViewHolder>() {

    class DeudorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewNombre: TextView = view.findViewById(R.id.textViewNombre)
        val textViewDocumento: TextView = view.findViewById(R.id.textViewDocumento)
        val textViewTipo: TextView = view.findViewById(R.id.textViewTipo)
        val textViewVencimiento: TextView = view.findViewById(R.id.textViewVencimiento)
        val textViewDiasVencido: TextView = view.findViewById(R.id.textViewDiasVencido)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeudorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deudor, parent, false)
        return DeudorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeudorViewHolder, position: Int) {
        val deudor = deudores[position]

        holder.textViewNombre.text = "${deudor.cliente.apellido}, ${deudor.cliente.nombre}"
        holder.textViewDocumento.text = "Doc: ${deudor.cliente.documento}"
        holder.textViewTipo.text = "Tipo: ${deudor.cliente.tipoCliente.displayName}"

        if (deudor.ultimoPago != null) {
            // Extraer solo la fecha (yyyy-MM-dd) sin la hora
            val fechaVencimiento = if (deudor.ultimoPago.fechaVencimiento.length > 10) {
                deudor.ultimoPago.fechaVencimiento.substring(0, 10)
            } else {
                deudor.ultimoPago.fechaVencimiento
            }

            holder.textViewVencimiento.text = "Vencimiento: $fechaVencimiento"
            holder.textViewDiasVencido.text = "Vencido hace ${deudor.diasVencido} día(s)"
            holder.textViewDiasVencido.visibility = View.VISIBLE

            val color = when {
                deudor.diasVencido > 30 -> android.graphics.Color.RED
                deudor.diasVencido > 15 -> android.graphics.Color.parseColor("#FF6B00")
                else -> android.graphics.Color.parseColor("#FFA500")
            }
            holder.textViewDiasVencido.setTextColor(color)

        } else {
            // Cliente sin pagos
            holder.textViewVencimiento.text = "Sin pagos registrados"
            holder.textViewDiasVencido.text = "Nunca pagó"
            holder.textViewDiasVencido.visibility = View.VISIBLE
            holder.textViewDiasVencido.setTextColor(android.graphics.Color.RED)
        }
    }

    override fun getItemCount() = deudores.size

    fun actualizarDatos(nuevosDeudores: List<ClienteConDeuda>) {
        deudores = nuevosDeudores
        notifyDataSetChanged()
    }
}