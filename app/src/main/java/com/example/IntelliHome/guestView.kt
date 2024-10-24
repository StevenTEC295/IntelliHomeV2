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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import org.json.JSONArray
import org.json.JSONObject

data class House(
    val name: String,
    val price: Int,
    val location: String,
    val size: Int,
    val amenities: List<String>,
    val available: Boolean,
    val guestCount: Int
)

class guestView : AppCompatActivity() {

    private lateinit var priceSeekBar: SeekBar
    private lateinit var peopleSeekBar: SeekBar
    private lateinit var priceValue: TextView
    private lateinit var peopleValue: TextView
    private lateinit var petsAllowed: CheckBox
    private lateinit var filterDialog: View
    private lateinit var info_casa: View
    private lateinit var backgroundDim: View
    private lateinit var hamburgerMenu: View
    private lateinit var recycler: RecyclerView

    private lateinit var checkBoxCocina: CheckBox
    private lateinit var checkBoxAC: CheckBox
    private lateinit var checkBoxCalefaccion: CheckBox
    private lateinit var amenidadWifi: CheckBox
    private lateinit var tvO: CheckBox
    private lateinit var amenidadLavadora: CheckBox
    private lateinit var amenidadPiscina: CheckBox
    private lateinit var amenidadJardin: CheckBox
    private lateinit var amenidadBarbacoa: CheckBox
    private lateinit var amenidadTerraza: CheckBox
    private lateinit var amenidadGym: CheckBox
    private lateinit var amenidadGaraje: CheckBox
    private lateinit var amenidadSeguridad: CheckBox
    private lateinit var amenidadHabitaciones: CheckBox
    private lateinit var amenidadMuebles: CheckBox
    private lateinit var amenidadMicro: CheckBox
    private lateinit var amenidadLavajillas: CheckBox
    private lateinit var amenidadCafetera: CheckBox
    private lateinit var amenidadRopa: CheckBox
    private lateinit var amenidadComunes: CheckBox
    private lateinit var amenidadCamas: CheckBox
    private lateinit var amenidadLimpieza: CheckBox
    private lateinit var amenidadTransportePublico: CheckBox
    private lateinit var amenidadCercania: CheckBox
    private lateinit var amenidadRadiacion: CheckBox
    private lateinit var amenidadEscritorio: CheckBox
    private lateinit var amenidadEntretenimiento: CheckBox
    private lateinit var amenidadChimenea: CheckBox
    private lateinit var amenidadInternetAlta: CheckBox

    private lateinit var housesData: List<House> // Para almacenar los datos de las casas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_view)

        // Inicialización de elementos
        priceSeekBar = findViewById(R.id.priceSeekBar)
        peopleSeekBar = findViewById(R.id.peopleSeekBar)
        priceValue = findViewById(R.id.priceValue)
        peopleValue = findViewById(R.id.peopleValue)
        filterDialog = findViewById(R.id.filter_dialog)
        info_casa = findViewById(R.id.info_container)
        backgroundDim = findViewById(R.id.backgroundDim)
        hamburgerMenu = findViewById(R.id.hamburger_menu)
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

        // Solicitar casas al iniciar la actividad
        CoroutineScope(Dispatchers.IO).launch {
            requestHouses()
        }
    }

    private suspend fun requestHouses() {
        try {
            // Conectar al servidor
            val socket = Socket("IP_DEL_SERVIDOR", 8080) // Reemplaza IP_DEL_SERVIDOR con la dirección IP de tu servidor
            val out = PrintWriter(socket.getOutputStream(), true)
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))

            // Enviar solicitud al servidor
            val requestJson = JSONObject().apply {
                put("action", "rq_house")
            }.toString()
            out.println(requestJson)

            // Recibir respuesta
            val response = StringBuilder()
            var line: String?
            while (input.readLine().also { line = it } != null) {
                response.append(line)
            }

            // Procesar la respuesta
            housesData = parseHouseData(response.toString())
            withContext(Dispatchers.Main) {
                setupRecyclerView(recycler, housesData) // Muestra los datos en el RecyclerView
            }

            // Cerrar la conexión
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace() // Manejar errores de conexión
        }
    }

    private fun parseHouseData(data: String): List<House> {
        val housesList = mutableListOf<House>()
        val jsonArray = JSONObject(data).getJSONArray("houses") // Asegúrate que la respuesta JSON tiene este formato
        for (i in 0 until jsonArray.length()) {
            val house = jsonArray.getJSONObject(i)
            val name = house.getString("nombre")
            val price = house.getInt("precio")
            val location = house.getString("ubicacion")
            val size = house.getInt("tamano")
            val amenities = mutableListOf<String>()
            val amenitiesArray = house.getJSONArray("amenidades")
            for (j in 0 until amenitiesArray.length()) {
                amenities.add(amenitiesArray.getString(j))
            }
            val available = house.getBoolean("disponible")
            val guestCount = house.getInt("cantidad_personas")

            // Agregar cada casa a la lista
            housesList.add(House(name, price, location, size, amenities, available, guestCount))
        }
        return housesList
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, houses: List<House>) {
        val dataSet = houses.map { Pair(it.name, it.price) } // Convertimos la lista de House a Pair
        val adapter = CustomAdapter_guestView(dataSet)

        recyclerView.adapter = adapter
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