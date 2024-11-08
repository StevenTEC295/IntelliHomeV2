package com.example.intellihome

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
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
import java.text.Normalizer
import java.time.LocalDate
import java.util.Scanner

class guestView : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainLayout: RelativeLayout
    private lateinit var priceSeekBar: SeekBar
    private lateinit var peopleSeekBar: SeekBar
    private lateinit var priceValue: TextView
    private lateinit var peopleValue: TextView
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
    private lateinit var btncasaalquilada: Button
    private lateinit var btnDisponibilidad: Button

    //Checkboxes
    private lateinit var petsAllowed: CheckBox
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
    private lateinit var resetearfiltro: CheckBox
    private lateinit var btnsearch: ImageButton
    private lateinit var searchField: EditText

    private val  fechaActual = LocalDate.now()
    private val dia = fechaActual.dayOfMonth
    private val mes = fechaActual.monthValue


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_view)
        sharedPreferences = getSharedPreferences("IntelliHomePrefs", Context.MODE_PRIVATE)
        mainLayout = findViewById(R.id.main)

        recycler = findViewById(R.id.recycleViewListadeCasas_guest)

        val savedValue = loadPreference("selectedHouse")
        savedValue?.let {
            // Haz algo con el valor cargado, como actualizar la UI o establecer configuraciones
            println("Valor guardado: $it")
        }

        setupRecyclerView(recycler, myDataSet)

        recycler.itemAnimator = null

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
        btncasaalquilada = findViewById(R.id.btnViewHouse)
        btnDisponibilidad = findViewById(R.id.btnDisponibilidad)
        btnsearch = findViewById(R.id.internal_search_button)
        searchField = findViewById(R.id.search_field)


        petsAllowed = findViewById(R.id.petsAllowed)
        checkBoxCocina = findViewById(R.id.amenidad_cocina)
        checkBoxAC = findViewById(R.id.amenidad_AC)
        checkBoxCalefaccion = findViewById(R.id.amenidad_calefaccion)
        amenidadWifi = findViewById(R.id.amenidad_wifi)
        tvO = findViewById(R.id.tv_o)
        amenidadLavadora = findViewById(R.id.amenidad_lavadora)
        amenidadPiscina = findViewById(R.id.amenidad_piscina)
        amenidadJardin = findViewById(R.id.amenidad_jardin)
        amenidadBarbacoa = findViewById(R.id.amenidad_barbacoa)
        amenidadTerraza = findViewById(R.id.amenidad_terraza)
        amenidadGym = findViewById(R.id.amenidad_gym)
        amenidadGaraje = findViewById(R.id.amenidad_garaje)
        amenidadSeguridad = findViewById(R.id.amenidad_seguridad)
        amenidadHabitaciones = findViewById(R.id.amenidad_habitaciones)
        amenidadMuebles = findViewById(R.id.amenidad_muebles)
        amenidadMicro = findViewById(R.id.amenidad_micro)
        amenidadLavajillas = findViewById(R.id.amenidad_lavajillas)
        amenidadCafetera = findViewById(R.id.amenidad_cafetera)
        amenidadRopa = findViewById(R.id.amenidad_ropa)
        amenidadComunes = findViewById(R.id.amenidad_comunes)
        amenidadCamas = findViewById(R.id.amenidad_camas)
        amenidadLimpieza = findViewById(R.id.amenidad_limpieza)
        amenidadTransportePublico = findViewById(R.id.amenidad_transportePublico)
        amenidadCercania = findViewById(R.id.amenidad_cercania)
        amenidadRadiacion = findViewById(R.id.amenidad_radiacion)
        amenidadEscritorio = findViewById(R.id.amenidad_escritorio)
        amenidadEntretenimiento = findViewById(R.id.amenidad_entretenimiento)
        amenidadChimenea = findViewById(R.id.amenidad_chimenea)
        amenidadInternetAlta = findViewById(R.id.amenidad_internetalta)
        resetearfiltro = findViewById(R.id.resetfilters)
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
            if (resetearfiltro.isChecked) {
                Toast.makeText(this, getString(R.string.filterreset), Toast.LENGTH_SHORT).show()
                displayAllProperties()  // Display all properties if no filter is set
            } else {
                applyFilters(maxPrice, maxPeople)
            }

            hideFilterDialog()
        }

        btnsearch.setOnClickListener {
            val infoabuscar = normalize(searchField.text.toString())
            val infoFiltrar = GlobalVariables.globalInfo
            val parserFiltro = PropertyParser()
            val propertiesFiltro = parserFiltro.parseProperties(infoFiltrar)
            deleteData()
            for (property in propertiesFiltro){
                if (infoabuscar == normalize(property.location)){
                    val precioadouble = property.price.toDouble()
                    val mediaArmonicaAjustada = calcularNuevaCantidad(dia,mes,15.00,3.00,precioadouble)
                    val preciototal = precioadouble + mediaArmonicaAjustada
                    val info = "${getString(R.string.casa)} ${property.typeofHouse}\n" +
                            "${getString(R.string.ubicacion)} ${property.location}\n" +
                            "${getString(R.string.disponilidad_casa)} ${property.availability}\n" +
                            "${getString(R.string.cantpersonas)} ${property.cantofPeople}\n\n" +
                            "${getString(R.string.amenidades_lista)} ${property.amenities.filter { it.isNotBlank() }.joinToString(", ")}\n\n" +
                            "${getString(R.string.reglas_guess)} ${property.rules}\n" +
                            "${getString(R.string.precio_sin_algoritmo)} ${property.price}\$\n"+
                            "${getString(R.string.precio_ajustado)} ${preciototal}\$\n"
                    myDataSet.add(Pair(info, R.drawable.image_casas_template))
                }
            }
            recycleadapter.notifyDataSetChanged()
            if (myDataSet.isEmpty()) {
                Toast.makeText(this, "No resultados en la búsqueda", Toast.LENGTH_SHORT).show()
            }
            println(infoabuscar)
        }


        btncasaalquilada.setOnClickListener {
            //Elimino la data luego actualizo el recycle view
            deleteData()
            val savedValue = loadPreference("selectedHouse")
            if (savedValue != null) {
                myDataSet.add(Pair(savedValue, R.drawable.image_casas_template))
            }
            recycleadapter.notifyDataSetChanged()

        }

        btnDisponibilidad.setOnClickListener {
            displayAllProperties()
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
                                    val precioadouble = property.price.toDouble()
                                    val mediaArmonicaAjustada = calcularNuevaCantidad(dia,mes,15.00,3.00,precioadouble)
                                    val preciototal = precioadouble + mediaArmonicaAjustada
                                    val info = "${getString(R.string.casa)} ${property.typeofHouse}\n"+
                                            "${getString(R.string.ubicacion)} ${property.location}\n"+
                                            "${getString(R.string.disponilidad_casa)} ${property.availability}\n"+
                                            "${getString(R.string.cantpersonas)} ${property.cantofPeople}\n\n"+
                                            "${getString(R.string.amenidades_lista)} ${property.amenities.filter { it.isNotBlank() }.joinToString(", ")}\n\n"+
                                            "${getString(R.string.reglas_guess)} ${property.rules}\n"+
                                            "${getString(R.string.precio_sin_algoritmo)} ${property.price}\$\n"+
                                            "${getString(R.string.precio_ajustado)} ${preciototal}\$\n"


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


    private fun displayAllProperties() {
        myDataSet.clear()
        val properties = PropertyParser().parseProperties(GlobalVariables.globalInfo)
        for (property in properties) {
            val precioadouble = property.price.toDouble()
            val mediaArmonicaAjustada = calcularNuevaCantidad(dia,mes,15.00,3.00,precioadouble)
            val preciototal = precioadouble + mediaArmonicaAjustada
            val info = "${getString(R.string.casa)} ${property.typeofHouse}\n" +
                    "${getString(R.string.ubicacion)} ${property.location}\n" +
                    "${getString(R.string.disponilidad_casa)} ${property.availability}\n" +
                    "${getString(R.string.cantpersonas)} ${property.cantofPeople}\n\n" +
                    "${getString(R.string.amenidades_lista)} ${property.amenities.filter { it.isNotBlank() }.joinToString(", ")}\n\n" +
                    "${getString(R.string.reglas_guess)} ${property.rules}\n" +
                    "${getString(R.string.precio_sin_algoritmo)} ${property.price}\$\n"+
                    "${getString(R.string.precio_ajustado)} ${preciototal}\$\n"
            myDataSet.add(Pair(info, R.drawable.image_casas_template))
        }

        recycleadapter.notifyDataSetChanged()
    }


    private fun applyFilters(maxPrice: Int, maxPeople: Int) {
        val infoFiltrar = GlobalVariables.globalInfo
        val parserFiltro = PropertyParser()
        val propertiesFiltro = parserFiltro.parseProperties(infoFiltrar)

        val isPetsAllowedChecked = petsAllowed.isChecked
        val isKitchenChecked = checkBoxCocina.isChecked
        val isACChecked = checkBoxAC.isChecked
        val isHeatingChecked = checkBoxCalefaccion.isChecked
        val isWifiChecked = amenidadWifi.isChecked
        val isTVChecked = tvO.isChecked
        val isWasherChecked = amenidadLavadora.isChecked
        val isPoolChecked = amenidadPiscina.isChecked
        val isGardenChecked = amenidadJardin.isChecked
        val isBBQChecked = amenidadBarbacoa.isChecked
        val isTerraceChecked = amenidadTerraza.isChecked
        val isGymChecked = amenidadGym.isChecked
        val isGarageChecked = amenidadGaraje.isChecked
        val isSecurityChecked = amenidadSeguridad.isChecked
        val isBedroomsChecked = amenidadHabitaciones.isChecked
        val isFurnishedChecked = amenidadMuebles.isChecked
        val isMicrowaveChecked = amenidadMicro.isChecked
        val isDishwasherChecked = amenidadLavajillas.isChecked
        val isCoffeeMachineChecked = amenidadCafetera.isChecked
        val isClothingStorageChecked = amenidadRopa.isChecked
        val isCommonAreasChecked = amenidadComunes.isChecked
        val isBedsChecked = amenidadCamas.isChecked
        val isCleaningServiceChecked = amenidadLimpieza.isChecked
        val isPublicTransportChecked = amenidadTransportePublico.isChecked
        val isProximityChecked = amenidadCercania.isChecked
        val isLowRadiationChecked = amenidadRadiacion.isChecked
        val isDeskChecked = amenidadEscritorio.isChecked
        val isEntertainmentChecked = amenidadEntretenimiento.isChecked
        val isFireplaceChecked = amenidadChimenea.isChecked
        val isHighSpeedInternetChecked = amenidadInternetAlta.isChecked

        myDataSet.clear()

        for (property in propertiesFiltro) {
            val matchesPrice = property.price <= maxPrice
            val matchesPeople = property.cantofPeople <= maxPeople
            val matchesPetsAllowed = !isPetsAllowedChecked || property.amenities.contains(getString(R.string.aceptan_mascotas))

            // Additional filters for each amenity checkbox
            val matchesKitchen = !isKitchenChecked || property.amenities.contains(getString(R.string.cocina_equipada))
            val matchesAC = !isACChecked || property.amenities.contains(getString(R.string.Aire_acondicionado))
            val matchesHeating = !isHeatingChecked || property.amenities.contains(getString(R.string.CalefacciOn))
            val matchesWifi = !isWifiChecked || property.amenities.contains(getString(R.string.wifi))
            val matchesTV = !isTVChecked || property.amenities.contains(getString(R.string.tv_o_cable))
            val matchesWasher = !isWasherChecked || property.amenities.contains(getString(R.string.Lavadora_y_secadora))
            val matchesPool = !isPoolChecked || property.amenities.contains(getString(R.string.Piscina))
            val matchesGarden = !isGardenChecked || property.amenities.contains(getString(R.string.Jardín_o_patio))
            val matchesBBQ = !isBBQChecked || property.amenities.contains(getString(R.string.Barbacoa_o_parrilla))
            val matchesTerrace = !isTerraceChecked || property.amenities.contains(getString(R.string.Terraza_o_balcón))
            val matchesGym = !isGymChecked || property.amenities.contains(getString(R.string.Gimnasio_en_casa))
            val matchesGarage = !isGarageChecked || property.amenities.contains(getString(R.string.Garaje))
            val matchesSecurity = !isSecurityChecked || property.amenities.contains(getString(R.string.Sistema_de_seguridad))
            val matchesBedrooms = !isBedroomsChecked || property.amenities.contains(getString(R.string.habitaciones_con_bano_en_suite))
            val matchesFurnished = !isFurnishedChecked || property.amenities.contains(getString(R.string.muebles_de_exterior))
            val matchesMicrowave = !isMicrowaveChecked || property.amenities.contains(getString(R.string.microondas))
            val matchesDishwasher = !isDishwasherChecked || property.amenities.contains(getString(R.string.lavavajillas))
            val matchesCoffeeMachine = !isCoffeeMachineChecked || property.amenities.contains(getString(R.string.cafetera))
            val matchesClothingStorage = !isClothingStorageChecked || property.amenities.contains(getString(R.string.ropa_de_cama_y_toallas_incluidas))
            val matchesCommonAreas = !isCommonAreasChecked || property.amenities.contains(getString(R.string.acceso_a_areas_comunes))
            val matchesBeds = !isBedsChecked || property.amenities.contains(getString(R.string.camas_adicionales_o_sofa_cama))
            val matchesCleaningService = !isCleaningServiceChecked || property.amenities.contains(getString(R.string.servicios_de_limpieza_opcionales))
            val matchesPublicTransport = !isPublicTransportChecked || property.amenities.contains(getString(R.string.acceso_a_transporte_publico_cercano))
            val matchesProximity = !isProximityChecked || property.amenities.contains(getString(R.string.cercania_a_tiendas_y_restaurantes))
            val matchesLowRadiation = !isLowRadiationChecked || property.amenities.contains(getString(R.string.sistema_de_calefaccion_por_suelo_radiante))
            val matchesDesk = !isDeskChecked || property.amenities.contains(getString(R.string.escritorio_o_area_de_trabajo))
            val matchesEntertainment = !isEntertainmentChecked || property.amenities.contains(getString(R.string.sistemas_de_entretenimiento))
            val matchesFireplace = !isFireplaceChecked || property.amenities.contains(getString(R.string.chimenea))
            val matchesHighSpeedInternet = !isHighSpeedInternetChecked || property.amenities.contains(getString(R.string.acceso_a_internet_de_alta_velocidad))

            // Combine all conditions
            if (matchesPrice && matchesPeople && matchesPetsAllowed &&
                matchesKitchen && matchesAC && matchesHeating && matchesWifi && matchesTV &&
                matchesWasher && matchesPool && matchesGarden && matchesBBQ && matchesTerrace &&
                matchesGym && matchesGarage && matchesSecurity && matchesBedrooms && matchesFurnished &&
                matchesMicrowave && matchesDishwasher && matchesCoffeeMachine && matchesClothingStorage &&
                matchesCommonAreas && matchesBeds && matchesCleaningService && matchesPublicTransport &&
                matchesProximity && matchesLowRadiation && matchesDesk && matchesEntertainment &&
                matchesFireplace && matchesHighSpeedInternet
            ) {
                val precioadouble = property.price.toDouble()
                val mediaArmonicaAjustada = calcularNuevaCantidad(dia,mes,15.00,3.00,precioadouble)
                val preciototal = precioadouble + mediaArmonicaAjustada
                val info = "${getString(R.string.casa)} ${property.typeofHouse}\n" +
                        "${getString(R.string.ubicacion)} ${property.location}\n" +
                        "${getString(R.string.disponilidad_casa)} ${property.availability}\n" +
                        "${getString(R.string.cantpersonas)} ${property.cantofPeople}\n\n" +
                        "${getString(R.string.amenidades_lista)} ${property.amenities.filter { it.isNotBlank() }.joinToString(", ")}\n\n" +
                        "${getString(R.string.reglas_guess)} ${property.rules}\n" +
                        "${getString(R.string.precio_sin_algoritmo)} ${property.price}\$\n"+
                        "${getString(R.string.precio_ajustado)} ${preciototal}\$\n"
                myDataSet.add(Pair(info, R.drawable.image_casas_template))
            }
        }

        recycleadapter.notifyDataSetChanged()

        if (myDataSet.isEmpty()) {
            Toast.makeText(this, "No resultados en los filtros", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePreference(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)  // Aquí puedes usar putInt, putBoolean, etc., según el tipo de dato
        editor.apply()  // Usa commit() si necesitas una escritura sincrónica
    }

    // Método para cargar el valor desde SharedPreferences
    private fun loadPreference(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, dataSet: List<Pair<String, Int>>) {
        recycleadapter = CustomAdapter_guestView(dataSet, object : CustomAdapter_guestView.OnItemClickListener {
            override fun onItemClick(info: Pair<String, Int>) {
                // Handle item click here, for example:
                savePreference("selectedHouse", info.first)
                Toast.makeText(this@guestView, "Se alquilo la casa con exito", Toast.LENGTH_SHORT).show()
                val msg = info.first.replace("\n", "")
                val jsoncasa = sendMessagenotifi(Constants.NOTIFICASA,info.first)
                sendMessage(jsoncasa)
                println(jsoncasa)
            }
        })
        recyclerView.adapter = recycleadapter
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    }

    private fun deleteData() {
        myDataSet.clear()
        recycleadapter.notifyDataSetChanged()
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

    private fun normalize(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("\\p{M}".toRegex(), "") // Remove diacritical marks
            .lowercase() // Convert to lowercase
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

    private fun sendMessagenotifi(
        action: String,
        message: String
    ): String {
        val json = JSONObject()
        json.put("action", action)
        json.put("message",message)
        return json.toString()
    }



    private fun calcularNuevaCantidad(dia: Int, mes: Int, porcentajeImpuesto: Double, comision: Double, montoTotal: Double): Double {
        // Paso 1: Calcular el límite máximo
        val limiteMaximo = 0.10 * montoTotal
        //println("limiteMaximo:$limiteMaximo")
        // Paso 2: Calcular la media armónica
        val mediaArmonica = if ((porcentajeImpuesto + comision) > 0) {
            2 / ((1 / porcentajeImpuesto) + (1 / comision))
        } else {
            0.0
        }
        //println("mediaArmonica:$mediaArmonica")

        // Paso 3: Calcular el factor de ajuste
        val factorAjuste = (dia + mes) / 100.00

        //println("factorAjuste:$factorAjuste")
        // Paso 4: Ajustar la media armónica
        var mediaArmonicaAjustada = mediaArmonica * factorAjuste

        //println("mediaArmonicaAjustada:$mediaArmonicaAjustada")
        // Paso 5: Asegurarse de que no exceda el límite
        if (mediaArmonicaAjustada > limiteMaximo) {
            mediaArmonicaAjustada = limiteMaximo
        }
        //println("mediaArmonicaAjustadaantesdelretunr:$mediaArmonicaAjustada")
        // Retornar el resultado
        return mediaArmonicaAjustada
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