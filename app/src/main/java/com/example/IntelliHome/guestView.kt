package com.example.intellihome // Cambia esto al nombre de tu paquete

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.IntelliHome.CustomAdapter_guestView
import com.example.intellihome.R

class guestView : AppCompatActivity() {

    private lateinit var priceSeekBar: SeekBar
    private lateinit var peopleSeekBar: SeekBar
    private lateinit var priceValue: TextView
    private lateinit var peopleValue: TextView
    private lateinit var petsAllowed: CheckBox
    private lateinit var filterDialog: View
    private lateinit var backgroundDim: View
    private lateinit var hamburgerMenu: View
    private lateinit var recycler: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_view)

        // Inicialización de elementos
        priceSeekBar = findViewById(R.id.priceSeekBar)
        peopleSeekBar = findViewById(R.id.peopleSeekBar)
        priceValue = findViewById(R.id.priceValue)
        peopleValue = findViewById(R.id.peopleValue)
        petsAllowed = findViewById(R.id.petsAllowed)
        filterDialog = findViewById(R.id.filter_dialog)
        backgroundDim = findViewById(R.id.backgroundDim)
        hamburgerMenu = findViewById(R.id.hamburger_menu) // Inicializa el menú hamburguesa
        val applyFiltersButton: Button = findViewById(R.id.applyFiltersButton)
        recycler = findViewById(R.id.recycleViewListadeCasas)

        // Listener para el SeekBar de precio
        priceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                priceValue.text = "Precio seleccionado: $$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Listener para el SeekBar de personas
        peopleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                peopleValue.text = "Personas seleccionadas: ${progress + 1}" // +1 porque empieza en 0
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Manejo del botón de filtros
        findViewById<View>(R.id.internal_menu_button).setOnClickListener { showFilterDialog() }

        // Manejo del botón de aplicar filtros
        applyFiltersButton.setOnClickListener {
            // Aquí puedes manejar los filtros aplicados
            hideFilterDialog()
        }

        // Manejo del fondo oscuro
        backgroundDim.setOnClickListener { hideFilterDialog() }

        // Manejo del botón del menú hamburguesa
        findViewById<View>(R.id.menu_button).setOnClickListener { toggleHamburgerMenu() }
        backgroundDim.setOnClickListener { closeHamburgerMenu() }
    }
    private fun setupRecyclerView(recyclerView: RecyclerView, dataSet: List<Pair<String, Int>>) {
        // Inicializar el adaptador con el conjunto de datos proporcionado
        val adapter = CustomAdapter_guestView(dataSet)

        // Establecer el adaptador en el RecyclerView
        recyclerView.adapter = adapter

        // Establecer un LayoutManager para el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    }
    private fun showFilterDialog() {
        filterDialog.visibility = View.VISIBLE
        backgroundDim.visibility = View.VISIBLE
    }

    private fun hideFilterDialog() {
        filterDialog.visibility = View.GONE
        backgroundDim.visibility = View.GONE
    }

    private fun toggleHamburgerMenu() {
        if (hamburgerMenu.visibility == View.GONE) {
            hamburgerMenu.visibility = View.VISIBLE
            backgroundDim.visibility = View.VISIBLE
        } else {
            closeHamburgerMenu()
        }
    }

    private fun closeHamburgerMenu() {
        hamburgerMenu.visibility = View.GONE
        backgroundDim.visibility = View.GONE
    }
}
