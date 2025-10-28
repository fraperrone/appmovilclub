package com.example.layouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.layouts.data.repository.ClienteRepository
import com.example.layouts.data.model.Cliente

class VerAfiliadosActivity : AppCompatActivity() {

    private lateinit var textViewBienvenida: TextView
    private lateinit var buttonMostrar: Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var clienteRepository: ClienteRepository
    private lateinit var adapter: AfiliadosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_afiliados)

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

        configurarRecyclerView()
        configurarBotones()

        // Configurar botones de navegación
        BotonMenuHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
        BotonBackHelper.configurarBotonMenu(this, findViewById(android.R.id.content))
    }

    private fun configurarBienvenida() {
        textViewBienvenida = findViewById(R.id.textViewBienvenida)
        val userName = SessionManager.getUserName(this)
        textViewBienvenida.text = "Bienvenida, ${userName ?: "Usuario"}"
    }

    private fun inicializarVistas() {
        buttonMostrar = findViewById(R.id.button_enviar)
        recyclerView = findViewById(R.id.recyclerView_conceptos)
    }

    private fun configurarRecyclerView() {
        adapter = AfiliadosAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun configurarBotones() {
        buttonMostrar.setOnClickListener {
            mostrarAfiliados()
        }
    }

    private fun mostrarAfiliados() {
        val clientes = clienteRepository.obtenerTodosLosClientes()

        if (clientes.isEmpty()) {
            // Mostrar mensaje vacío
            adapter.actualizarDatos(emptyList())
        } else {
            adapter.actualizarDatos(clientes)
        }
    }
}

class AfiliadosAdapter(
    private var clientes: List<Cliente>
) : RecyclerView.Adapter<AfiliadosAdapter.AfiliadoViewHolder>() {

    class AfiliadoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewNombre: TextView = view.findViewById(R.id.textViewNombre)
        val textViewDocumento: TextView = view.findViewById(R.id.textViewDocumento)
        val textViewTipo: TextView = view.findViewById(R.id.textViewTipo)
        val textViewFecha: TextView = view.findViewById(R.id.textViewFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AfiliadoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_afiliado, parent, false)
        return AfiliadoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AfiliadoViewHolder, position: Int) {
        val cliente = clientes[position]

        holder.textViewNombre.text = "${cliente.apellido}, ${cliente.nombre}"
        holder.textViewDocumento.text = "Doc: ${cliente.documento}"
        holder.textViewTipo.text = "Tipo: ${cliente.tipoCliente.displayName}"
        holder.textViewFecha.text = "Registro: ${cliente.fechaRegistro.split(" ")[0]}"
    }

    override fun getItemCount() = clientes.size

    fun actualizarDatos(nuevosClientes: List<Cliente>) {
        clientes = nuevosClientes
        notifyDataSetChanged()
    }
}