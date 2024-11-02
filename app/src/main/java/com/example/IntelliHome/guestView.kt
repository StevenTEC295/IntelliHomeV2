package com.example.intellihome // Cambia esto al nombre de tu paquete

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.IntelliHome.Constants
import com.example.IntelliHome.CustomAdapter_guestView
import com.example.IntelliHome.PropertyParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import org.json.JSONObject
import java.util.Scanner

class guestView : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainLayout: RelativeLayout
    private lateinit var priceSeekBar: SeekBar
    private lateinit var peopleSeekBar: SeekBar
    private lateinit var priceValue: TextView
    private lateinit var peopleValue: TextView
    private lateinit var petsAllowed: CheckBox
    private lateinit var filterDialog: View
    private lateinit var info_casa: View
    private lateinit var backgroundDim: View
    private lateinit var hamburgerMenu: View
    private lateinit var upadatebtn: Button
    private val myDataSet = mutableListOf<Pair<String, Int>>()
    private lateinit var recycleadapter: CustomAdapter_guestView
    private lateinit var recycler: RecyclerView

    private var out: PrintWriter? = null
    private var socket: Socket? = null
    private var inputmsg: Scanner? = null
    private var inputReader: BufferedReader? = null // Cambiado de Scanner a BufferedReader
    private var isMessageSent = false
    private lateinit var house_image: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_view)
        sharedPreferences = getSharedPreferences("IntelliHomePrefs", Context.MODE_PRIVATE)
        mainLayout = findViewById(R.id.main)

        recycler = findViewById(R.id.recycleViewListadeCasas_guest)



        setupRecyclerView(recycler, myDataSet)


        // Inicialización de elementos
        priceSeekBar = findViewById(R.id.priceSeekBar)
        peopleSeekBar = findViewById(R.id.peopleSeekBar)
        priceValue = findViewById(R.id.priceValue)
        peopleValue = findViewById(R.id.peopleValue)
        filterDialog = findViewById(R.id.filter_dialog)
        info_casa = findViewById(R.id.info_container)
        backgroundDim = findViewById(R.id.backgroundDim)
        hamburgerMenu = findViewById(R.id.hamburger_menu)
        house_image = findViewById(R.id.homeIcon)
        val applyFiltersButton: Button = findViewById(R.id.applyFiltersButton)


        val button = findViewById<Button>(R.id.boton)

        button.setOnClickListener {
            val intent = Intent(this, ControlHouse::class.java)
            startActivity(intent)
        }
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
                peopleValue.text =
                    "Personas seleccionadas: ${progress + 1}" // +1 porque empieza en 0
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Manejo del botón de filtros
        findViewById<View>(R.id.internal_menu_button).setOnClickListener { showFilterDialog() }

        // Manejo del botón de aplicar filtros
        applyFiltersButton.setOnClickListener {
            // Aquí puedes manejar los filtros aplicados
            val maxPrice = priceSeekBar.progress
            val maxPeople = peopleSeekBar.progress + 1
            /*val isPetsAllowed = petsAllowed.isChecked*/
            //Primero hago un clear de de la Data
            eliminar()
            val infoFiltrar= GlobalVariables.globalInfo

            val parserFiltro = PropertyParser()
            val propertiesFiltro = parserFiltro.parseProperties(infoFiltrar)

            //Verifico que no hay un reset de la barra
            if(maxPrice==0 && maxPeople==1){
                Toast.makeText(this, getString(R.string.filterreset), Toast.LENGTH_SHORT).show()
                filterNotMatched()
            }else{
                for (propertyFilter in propertiesFiltro){
                    if (propertyFilter.price in 1..maxPrice && propertyFilter.cantofPeople in 1..maxPeople ){

                        val info = "${getString(R.string.casa)} ${propertyFilter.typeofHouse}\n" +
                                "${getString(R.string.ubicacion)} ${propertyFilter.location}\n" +
                                "${getString(R.string.disponilidad_casa)} ${propertyFilter.availability}\n" +
                                "${getString(R.string.cantpersonas)} ${propertyFilter.cantofPeople}\n\n" +
                                "${getString(R.string.amenidades_lista)} ${propertyFilter.amenities.filter { it.isNotBlank() }.joinToString(", ")}\n\n" +
                                "${getString(R.string.reglas_guess)} ${propertyFilter.rules}\n" +
                                "${getString(R.string.precio_sin_algoritmo)} ${propertyFilter.price}\$"

                        myDataSet.add(Pair(info, R.drawable.image_casas_template))
                    }
                }
            }

            recycleadapter.notifyDataSetChanged()
            hideFilterDialog()
        }


        // Manejo del fondo oscuro
        backgroundDim.setOnClickListener { hideFilterDialog() }

        // Manejo del botón del menú hamburguesa
        findViewById<View>(R.id.menu_button).setOnClickListener { toggleHamburgerMenu() }
        backgroundDim.setOnClickListener { closeHamburgerMenu() }

        Thread {
            try {
                socket = Socket(Constants.SERVER_IP, Constants.SERVER_PORT)
                out = PrintWriter(socket!!.getOutputStream(), true)
                inputmsg =
                    Scanner(socket!!.getInputStream())  //Es casi lo mismo que el buffer los dos funcionan

                inputReader =
                    BufferedReader(InputStreamReader(socket!!.getInputStream())) // Inicializa BufferedReader

                if (!isMessageSent) {
                    val jsonData = createJsonData(Constants.RQHOUSE)
                    sendMessage(jsonData)
                    isMessageSent = true // Marcar el mensaje como enviado
                }

                Thread {
                    while (true) {
                        val message = inputReader!!.readLine()
                        if (message != null) {
                            val parser = PropertyParser()
                            val properties = parser.parseProperties(message)
                            GlobalVariables.globalInfo = message
                            runOnUiThread { // actualiza el la gui en un hilo
                                for (property in properties) {

                                    val info = "${getString(R.string.casa)} ${property.typeofHouse}\n"+
                                            "${getString(R.string.ubicacion)} ${property.location}\n"+
                                            "${getString(R.string.disponilidad_casa)} ${property.availability}\n"+
                                            "${getString(R.string.cantpersonas)} ${property.cantofPeople}\n\n"+
                                            "${getString(R.string.amenidades_lista)} ${property.amenities.filter { it.isNotBlank() }.joinToString(", ")}\n\n"+
                                            "${getString(R.string.reglas_guess)} ${property.rules}\n"+
                                            "${getString(R.string.precio_sin_algoritmo)} ${property.price}\$\n"


                                    myDataSet.add(Pair(info, R.drawable.image_casas_template))
                                }
                                recycleadapter.notifyItemInserted(myDataSet.size - 1) // Notifica al adaptador que se ha insertado un nuevo elemento
                            }

                        }
                    }
                }.start()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
        loadSavedBackground()

    }

    private fun filterNotMatched() {
        val infoFiltrar= GlobalVariables.globalInfo
        val parserFiltro = PropertyParser()
        val propertiesFiltro = parserFiltro.parseProperties(infoFiltrar)

        for (propertyFilter in propertiesFiltro){
                val info = "${getString(R.string.casa)} ${propertyFilter.typeofHouse}\n" +
                        "${getString(R.string.ubicacion)} ${propertyFilter.location}\n" +
                        "${getString(R.string.disponilidad_casa)} ${propertyFilter.availability}\n" +
                        "${getString(R.string.cantpersonas)} ${propertyFilter.cantofPeople}\n\n" +
                        "${getString(R.string.amenidades_lista)} ${propertyFilter.amenities.filter { it.isNotBlank() }.joinToString(", ")}\n\n" +
                        "${getString(R.string.reglas_guess)} ${propertyFilter.rules}\n" +
                        "${getString(R.string.precio_sin_algoritmo)} ${propertyFilter.price}\$"

                myDataSet.add(Pair(info, R.drawable.image_casas_template))
        }
    }

    private fun eliminar() {
        myDataSet.clear()
        recycleadapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, dataSet: List<Pair<String, Int>>) {
        // Inicializar el adaptador con el conjunto de datos proporcionado
        recycleadapter = CustomAdapter_guestView(dataSet) // Asigna a la variable de clase

        // Establecer el adaptador en el RecyclerView
        recyclerView.adapter = recycleadapter

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

    object GlobalVariables {
        var globalInfo: String = ""
    }


    private fun closeHamburgerMenu() {
        hamburgerMenu.visibility = View.GONE
        backgroundDim.visibility = View.GONE
    }

    @SuppressLint("SuspiciousIndentation")
    private fun loadSavedBackground() {
        val savedBackground =
            sharedPreferences.getInt("background_resource", R.drawable.redbackground)
        mainLayout.setBackgroundResource(savedBackground)

    }

    data class Property(
        val action: String,
        val idPropertyRegister: String,
        val location: String,
        val typeofHouse: String,
        val availability: String,
        val cantofPeople: Int,
        val amenities: List<String>,
        val rules: String,
        val price: Int
    )

    private fun sendMessage(message: String) {
        Thread {
            try {
                out?.println(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun createJsonData(
        action: String
    ): String {
        val json = JSONObject()
        json.put("action", action)
        return json.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (out != null) out!!.close()
            if (inputmsg != null) inputmsg!!.close()
            if (socket != null) socket!!.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}