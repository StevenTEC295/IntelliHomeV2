package com.example.IntelliHome

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.intellihome.HomePage
import com.example.intellihome.LoginActivity
import com.example.intellihome.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class HostViewActivity : AppCompatActivity() {
    private lateinit var mainLayout: RelativeLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var selectDate: TextInputEditText
    private lateinit var exitbuton: TextView
    //ESTO SON LOS CHECKBOXES DE LAS AMENIDADES
    private lateinit var amenidadesManager: AmenidadesManager
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
    private lateinit var imageProperty: ImageView

    private lateinit var amenidadAnimales: CheckBox

    //BOTON DE REGISTRO
    private lateinit var registerButton: Button
    //EL URI DE LA IMAGEN
    private var imageUri: Uri? = null
    private var imageUris: MutableList<Uri> = mutableListOf()
    private lateinit var btnNextImage: Button

    //Campos de textos del FORMULARIO
    private lateinit var ubicacion: TextInputEditText
    private lateinit var cantofPeople: TextInputEditText
    private lateinit var reglas: EditText
    private lateinit var precio: TextInputEditText

    //UN CODIGO DE RESQUEST EL VALOR NO IMPORTA
    private val CODE =50
    private var nulllist = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_view)
        //LOGICA DEL CAMBIAR EL FONDO DE PANTALLA
        sharedPreferences = getSharedPreferences("IntelliHomePrefs", Context.MODE_PRIVATE)
        mainLayout = findViewById(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Logica del dropdown MENU
        val items = resources.getStringArray(R.array.house_types).toList()
        val autoComplete: AutoCompleteTextView = findViewById(R.id.autocomplete_tipo_de_casa)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        autoComplete.setAdapter(adapter)
        autoComplete.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            val itemSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(this, "Item: $itemSelected", Toast.LENGTH_SHORT).show()
        }



        loadSavedBackground()

        //FINDVIEW DE LOS CAMPOS DE TEXTOS
        val selectDate = findViewById<TextInputEditText>(R.id.Disponibilidad)
        exitbuton = findViewById(R.id.back_to_home_page)
        imageProperty = findViewById(R.id.imageProperty)
        ubicacion= findViewById(R.id.ubicacion)
        cantofPeople = findViewById(R.id.cantofPeople)
        reglas = findViewById(R.id.reglas)
        precio = findViewById(R.id.precio)
        btnNextImage = findViewById(R.id.next_Image)


        //LISTA DE AMENIDADES
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
        amenidadAnimales = findViewById(R.id.amenidad_mascotas)


        amenidadesManager = AmenidadesManager(this)
        amenidadesManager.configurarListeners(
            checkBoxCocina, checkBoxAC,
            checkBoxCalefaccion, amenidadWifi, tvO,
            amenidadLavadora, amenidadPiscina, amenidadJardin,
            amenidadBarbacoa, amenidadTerraza, amenidadGym,
            amenidadGaraje, amenidadSeguridad,
            amenidadHabitaciones, amenidadMuebles, amenidadMicro,
            amenidadLavajillas, amenidadCafetera,
            amenidadRopa, amenidadComunes, amenidadCamas,
            amenidadLimpieza, amenidadTransportePublico,
            amenidadCercania, amenidadRadiacion, amenidadEscritorio, amenidadEntretenimiento,
            amenidadChimenea, amenidadInternetAlta, amenidadAnimales
        )

        //PARA SELECCIONAR UNA IMAGEN EN EL IMAGE VIEW
        imageProperty.setOnClickListener {
            ImageController.multiplephotos(this,CODE)
            nulllist++
        }
        btnNextImage.setOnClickListener {
            cycleImage()
        }



        //BOTON DE SUBIR PROPIEDAD
        registerButton = findViewById(R.id.button_res)
        registerButton.setOnClickListener {
            val lista =amenidadesManager.obtenerAmenidadesSeleccionadas()
            val ubicacion = ubicacion.text.toString()
            val autoComplete = autoComplete.text.toString()
            val disponibilidad = selectDate.text.toString()
            val cantofPeople = cantofPeople.text.toString()
            val reglas = reglas.text.toString()
            val precio = precio.text.toString()

            //SE CREA UN ID UNICO PARA CADA PROPIEDAD
            val idPropertyRegister = UUID.randomUUID().toString()



            //VERIFICAR LOS CAMPOS DE TEXTO que cumplan con lo especificado
            val campos = listOf(ubicacion,autoComplete,disponibilidad,cantofPeople,reglas,precio)
            if (campos.any { it.isEmpty() }) {
                Toast.makeText(this, getString(R.string.completa_los_campos), Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Salir del evento si hay campos vacíos
            }

            if (imageUris.size < 1 || imageUris.size > 10) {
                Toast.makeText(this, getString(R.string.porfavor_selccione_10_imgs), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (lista.any { it.isEmpty() }) {
                Toast.makeText(this, getString(R.string.advertencia_amenidades), Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Salir del evento si hay campos vacíos
            }

            // Verifica si tiene ceros iniciales pero no es exactamente "0"
            if (cantofPeople.startsWith("0") && cantofPeople != "0") {
                Toast.makeText(this, getString(R.string.valor_cero), Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Salir del evento si empieza con 0 pero no es 0
            }

            val cantofPeopleInt = cantofPeople.toIntOrNull()
            if (cantofPeopleInt != null && (cantofPeopleInt >= 30 || cantofPeopleInt <= 0) ){
                Toast.makeText(this, getString(R.string.advertencia_cantofPeople), Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Salir del evento si hay campos vacíos
            }

            // Verifica si tiene ceros iniciales pero no es exactamente "0"
            if (precio.startsWith("0") && precio != "0") {
                Toast.makeText(this, getString(R.string.valor_cero), Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Salir del evento si empieza con 0 pero no es 0
            }

            val precioInt = precio.toIntOrNull()
            if (precioInt != null && precioInt <= 0){
                Toast.makeText(this, getString(R.string.advertencia_precio), Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Salir del evento si hay campos vacíos
            }

            //MEJORA PARA NO USAR THREADS SINO FUNCIONES DE KOTLIN COMO CoroutineScope
            CoroutineScope(Dispatchers.IO).launch {
                val jsonData = createJsonData(
                    Constants.SVHOUSE,
                    idPropertyRegister,
                    ubicacion,
                    autoComplete,
                    disponibilidad,
                    cantofPeople,
                    lista,
                    reglas,
                    precio
                    //base64Images
                )

                val sender = DataSender(Constants.SERVER_IP, Constants.SERVER_PORT)
                sender.sendDataToServer(jsonData)

                // Regresar al hilo principal para iniciar la nueva actividad
                withContext(Dispatchers.Main) {
                    val intent = Intent(this@HostViewActivity, ListofHostViewActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }
        // Disable keyboard input
        selectDate.isFocusable = false

        // Set click listener to show the date range picker
        selectDate.setOnClickListener {
            showDateRangePicker(selectDate)
        }
        exitbuton.setOnClickListener {
            backtoHomePage()
        }
    }

    private var currentIndex = 0

    private fun cycleImage() {

        if(nulllist == 0){
            Toast.makeText(this, getString(R.string.no_hay_imagenes), Toast.LENGTH_SHORT).show()
        }else{
            currentIndex = (currentIndex + 1) % imageUris.size
            imageProperty.setImageURI(imageUris[currentIndex])
        }
    }


    //Para lograr cargar varias imagenes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE && resultCode == Activity.RESULT_OK) {
            imageUris.clear() // Clear previous images

            // Check if multiple images were selected
            if (data?.clipData != null) {
                val clipData = data.clipData
                for (i in 0 until clipData!!.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    imageUris.add(imageUri)
                }
            } else if (data?.data != null) { // Single image selected
                val imageUri = data.data!!
                imageUris.add(imageUri)
            }

            // Show the first selected image in the ImageView
            if (imageUris.isNotEmpty()) {
                imageProperty.setImageURI(imageUris[0])
            }
        }
    }

    private fun createJsonData(
        action: String,
        idPropertyRegister: String,
        location: String,
        typeofHouse: String,
        availability: String,
        cantofPeople: String,
        amenidades: List<String>,
        rules: String,
        price: String
        //base64Image: List<String>
    ): String {
        val json = JSONObject()
        json.put("action", action)
        json.put("idPropertyRegister",idPropertyRegister)
        json.put("location", location)
        json.put("typeofHouse", typeofHouse)
        json.put("availability", availability)
        json.put("cantofPeople", cantofPeople)

        val amenidadesJsonArray = JSONArray(amenidades)
        json.put("amenidades", amenidadesJsonArray)
        json.put("rules", rules)
        json.put("price", price)

        /* val imagenesJSONArray = JSONArray(base64Image)
         json.put("image", imagenesJSONArray)*/

        return json.toString()
    }


    private fun loadSavedBackground() {
        val savedBackground =
            sharedPreferences.getInt("background_resource", R.drawable.redbackground)
        mainLayout.setBackgroundResource(savedBackground)

    }

    @SuppressLint("SetTextI18n")
    private fun showDateRangePicker(editText: EditText) {
        // Obtener la fecha actual en UTC y convertirla a milisegundos de la medianoche de hoy
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val today = calendar.timeInMillis

        // Crear restricciones de calendario para que la fecha mínima sea hoy
        val constraintsBuilder = CalendarConstraints.Builder()
            .setStart(today) // Establecer la fecha mínima permitida (hoy)
            .setValidator(DateValidatorPointForward.from(today)) // Asegurar que solo se puedan seleccionar fechas futuras

        // Crear el DateRangePicker con las restricciones
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.Select_Date_Range))
            .setCalendarConstraints(constraintsBuilder.build()) // Aplicar las restricciones
            .build()

        // Mostrar el picker
        dateRangePicker.show(supportFragmentManager, "date_range_picker")

        // Manejar la selección del rango de fechas
        dateRangePicker.addOnPositiveButtonClickListener { dateRange ->
            val startDate = dateRange.first
            val endDate = dateRange.second

            // Validar que la fecha de finalización sea posterior a la de inicio
            if (startDate != null && endDate != null) {
                if (startDate < endDate) {
                    // Establecer el rango de fechas seleccionado en el EditText
                    editText.setText("${convertToDate(startDate)} - ${convertToDate(endDate)}")
                } else {
                    // Mostrar un mensaje de error si las fechas son iguales o inválidas
                    Toast.makeText(this, getString(R.string.end_date_warning), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun convertToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Establecer la zona horaria a GMT+6
        format.timeZone = TimeZone.getTimeZone("GMT+6")
        return format.format(date)
    }

    private fun backtoHomePage() {
        val intent = Intent(this, ListofHostViewActivity::class.java)
        startActivity(intent)
        finish()

    }
}