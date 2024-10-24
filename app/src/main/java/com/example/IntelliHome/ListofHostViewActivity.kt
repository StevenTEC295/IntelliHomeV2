package com.example.IntelliHome

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.intellihome.HomePage
import com.example.intellihome.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread

class ListofHostViewActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainLayout: RelativeLayout
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CustomAdapter
    private lateinit var home: ImageView
    private lateinit var addProperty: ImageView
    private lateinit var lupa: ImageView
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

        addProperty.setOnClickListener {
            navegarAlFormulariopropiedad()
        }

        home.setOnClickListener {
            navegarAlHome()
        }

        /*lupa.setOnClickListener {
            val rq_house = "rq_house"
            thread {
                val jsonData = createJsonData(rq_house)
                //sendDataToServer("192.168.0.119", 8080,jsonData)
                //sendMessageToServerandReceiveMessage("192.168.0.119", 8080,jsonData)
                enviarRQHousing()
            }
        }*/



        val myDataSet = listOf(
            Pair("Información Casa 1", R.drawable.image_casas_template),
            Pair("Información Casa 4", R.drawable.image_casas_template)
            // Agrega más casas según sea necesario
        )

        setupRecyclerView(recycler, myDataSet)


        loadSavedBackground()

    }


    private fun loadSavedBackground() {
        val savedBackground =
            sharedPreferences.getInt("background_resource", R.drawable.redbackground)
            mainLayout.setBackgroundResource(savedBackground)

    }



   /* private fun sendMessageToServerandReceiveMessage(serverIp: String, serverPort: Int, jsonData: String) {
        try {
            // Crear socket y enviar el mensaje
            val socket = Socket(serverIp, serverPort)
            val outputStream: OutputStream = socket.getOutputStream()
            val bufferedWriter = PrintWriter(outputStream, true)

            // Convertir JSONObject a string y enviarlo al servidor
            bufferedWriter.println(jsonData)

            // Recibir respuesta del servidor
            val inputStream = socket.getInputStream()
            val responseBuffer = ByteArray(1024)
            val readBytes = inputStream.read(responseBuffer)
            val response = String(responseBuffer, 0, readBytes)

            // Parsear respuesta del servidor y actuar según la acción recibida
            val jsonResponse = JSONObject(response)
            println(jsonResponse)

            // Cerrar conexiones
            bufferedWriter.close()
            outputStream.close()
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al comunicarse con el servidor.")
        }
    }
    private fun enviarRQHousing() {
        val host = "192.168.0.119"  // Cambiar a la IP del servidor si es necesario
        val port = 8080         // Cambiar si se usa otro puerto

        try {
            // Establecer conexión con el servidor
            val socket = Socket(host, port)

            // Crear objeto JSON con la acción
            val requestJson = JSONObject()
            requestJson.put("action", "rq_house")

            // Enviar mensaje al servidor
            val out = PrintWriter(socket.getOutputStream(), true)
            out.println(requestJson.toString())

            // Leer la respuesta del servidor
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val response = StringBuilder()

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            // Imprimir la respuesta recibida
            println("Respuesta del servidor: $response")

            // Cerrar la conexión
            reader.close()
            out.close()
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendDataToServer(serverIp: String, serverPort: Int, jsonData: String) {
        try {
            val socket = Socket(serverIp, serverPort)
            val outputStream: OutputStream = socket.getOutputStream()
            val printWriter = PrintWriter(outputStream, true)
            val inputStream = BufferedReader(InputStreamReader(socket.getInputStream()))

            // Manda los datos al server
            printWriter.println(jsonData)

            // Aquí debería tener la respuesta del backend
            val serverResponse = inputStream.readLine()

            println("Respuesta del servidor: $serverResponse")


            // Cierra aquí al final
            printWriter.close()
            inputStream.close()
            socket.close()

            println("Conexión cerrada - envío completado")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al enviar los datos - envío fallido")
            runOnUiThread {
                Toast.makeText(this, "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

    /*private fun enviarDatosDePropiedad(propiedad: JSONObject) {
        val serverIp = "192.168.0.119"
        val serverPort = 8080
        sendMessageToServerandReceiveMessage(serverIp, serverPort, propiedad)
    }*/

    private fun createJsonData(
        rq_house:String,
    ): String {
        val json = JSONObject()
        json.put("action", rq_house)
        return json.toString()
    }




    private fun setupRecyclerView(recyclerView: RecyclerView, dataSet: List<Pair<String, Int>>) {
        // Inicializar el adaptador con el conjunto de datos proporcionado
        val adapter = CustomAdapter(dataSet)

        // Establecer el adaptador en el RecyclerView
        recyclerView.adapter = adapter

        // Establecer un LayoutManager para el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    }


    private fun navegarAlFormulariopropiedad() {
        val intent = Intent(this, HostViewActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navegarAlHome() {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
        finish()
    }
}