package com.example.IntelliHome

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.intellihome.HomePage
import com.example.intellihome.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.Scanner

class ListofHostViewActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainLayout: RelativeLayout
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CustomAdapter
    private lateinit var home: ImageView
    private lateinit var addProperty: ImageView

    private var out: PrintWriter? = null
    private var socket: Socket? = null
    private var inputmsg: Scanner? = null
    private var inputReader: BufferedReader? = null // Cambiado de Scanner a BufferedReader

    private var isMessageSent = false
    private lateinit var lupa: ImageView
    private val myDataSet = mutableListOf<Pair<String, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listof_host_view)
        sharedPreferences = getSharedPreferences("IntelliHomePrefs", Context.MODE_PRIVATE)
        mainLayout = findViewById(R.id.main)


        //EL RECYCLER VIEW
        recycler = findViewById(R.id.recycleViewListadeCasas)
        home = findViewById(R.id.home)
        addProperty = findViewById(R.id.addProperty)
        lupa = findViewById(R.id.lupa)
        myDataSet.add(Pair("Información Casa 1", R.drawable.image_casas_template))
        myDataSet.add(Pair("Información Casa 4", R.drawable.image_casas_template))

        // Configura el RecyclerView
        setupRecyclerView(recycler, myDataSet)
        addProperty.setOnClickListener {
            navegarAlFormulariopropiedad()
        }

        home.setOnClickListener {
            navegarAlHome()
        }
        lupa.setOnClickListener {

        }

        Thread {
            try {
                socket = Socket(Constants.SERVER_IP, Constants.SERVER_PORT)
                out = PrintWriter(socket!!.getOutputStream(), true)
                inputmsg = Scanner(socket!!.getInputStream())  //Es casi lo mismo que el buffer los dos funcionan

                inputReader = BufferedReader(InputStreamReader(socket!!.getInputStream())) // Inicializa BufferedReader

                if (!isMessageSent) {
                    val jsonData = createJsonData(Constants.RQHOUSE)
                    sendMessage(jsonData)
                    isMessageSent = true // Marcar el mensaje como enviado
                }

                Thread {
                    while (true) {
                        val message = inputReader!!.readLine()
                        if (message!=null) {
                            //val message = inputmsg!!.nextLine()
                            //val properties = parseProperties(message)

                            val parser = PropertyParser()
                            val properties = parser.parseProperties(message)


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
                                adapter.notifyItemInserted(myDataSet.size - 1) // Notifica al adaptador que se ha insertado un nuevo elemento
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

    private fun setupRecyclerView(recyclerView: RecyclerView, dataSet: List<Pair<String, Int>>) {
        // Inicializar el adaptador con el conjunto de datos proporcionado
        adapter = CustomAdapter(dataSet) // Asigna a la variable de clase

        // Establecer el adaptador en el RecyclerView
        recyclerView.adapter = adapter

        // Establecer un LayoutManager para el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    }

    private fun navegarAlFormulariopropiedad() {
        val intent = Intent(this, HostViewActivity::class.java)
        startActivity(intent)
        //onDestroy()
        finish()

    }

    private fun navegarAlHome() {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
        finish()
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