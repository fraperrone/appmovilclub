package com.example.layouts

import android.os.Bundle
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

class VerDeudoresActivity : AppCompatActivity() {

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

        inicializarVistas()
        pagoRepository = PagoRepository(this)

        configurarRecyclerView()
        configurarBotones()

        // Configurar botones de navegación
        BotonBackHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
        BotonMenuHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
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
        val deudores = pagoRepository.obtenerClientesConCuotaVencida()

        if (deudores.isEmpty()) {
            Toast.makeText(this, "No hay clientes con cuotas vencidas", Toast.LENGTH_SHORT).show()
        }

        adapter.actualizarDatos(deudores)
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
            holder.textViewVencimiento.text = "Vencimiento: ${deudor.ultimoPago.fechaVencimiento}"
            holder.textViewDiasVencido.text = "Vencido hace ${deudor.diasVencido} día(s)"

            // Cambiar color según días vencidos
            val color = when {
                deudor.diasVencido > 30 -> android.graphics.Color.RED
                deudor.diasVencido > 15 -> android.graphics.Color.parseColor("#FF6B00")
                else -> android.graphics.Color.parseColor("#FFA500")
            }
            holder.textViewDiasVencido.setTextColor(color)
        }
    }

    override fun getItemCount() = deudores.size

    fun actualizarDatos(nuevosDeudores: List<ClienteConDeuda>) {
        deudores = nuevosDeudores
        notifyDataSetChanged()
    }
}