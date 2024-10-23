package com.example.intellihome

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.intellihome.R
import org.json.JSONObject
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread

class ControlHouse : AppCompatActivity() {

    private var isSalaActive = false
    private var isCuarto1Active = false
    private var isCuarto2Active = false
    private var isBath1Active = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controlhouse)

        val areaSala: View = findViewById(R.id.areaSala)
        areaSala.setOnClickListener {
            isSalaActive = !isSalaActive
            toggleBackground(it, isSalaActive)
            sendCommand("Sala", isSalaActive)
        }

        val Cuarto1: View = findViewById(R.id.Cuarto1)
        Cuarto1.setOnClickListener {
            isCuarto1Active = !isCuarto1Active
            toggleBackground(it, isCuarto1Active)
            sendCommand("Cuarto1", isCuarto1Active)
        }

        val Cuarto2: View = findViewById(R.id.Cuarto2)
        Cuarto2.setOnClickListener {
            isCuarto2Active = !isCuarto2Active
            toggleBackground(it, isCuarto2Active)
            sendCommand("Cuarto2", isCuarto2Active)
        }

        val Bath1: View = findViewById(R.id.Bath1)
        Bath1.setOnClickListener {
            isBath1Active = !isBath1Active
            toggleBackground(it, isBath1Active)
            sendCommand("Baño", isBath1Active)
        }
    }
    // Cambia el fondo del botón dependiendo de su estado
    private fun toggleBackground(view: View, isActive: Boolean) {
        if (isActive) {
            view.setBackgroundColor(Color.parseColor("#7738E05D"))  // Color activo
        } else {
            view.setBackgroundColor(Color.TRANSPARENT)  // Resetear a transparente
        }
    }

    // Envía el comando al servidor usando un socket
    private fun sendCommand(room: String, isActive: Boolean) {
        val command = when (room) {
            "Sala" -> if (isActive) "S1_1" else "S1_0"
            "Cuarto1" -> if (isActive) "C1_1" else "C1_0"
            "Cuarto2" -> if (isActive) "C2_1" else "C2_0"
            "Baño" -> if (isActive) "B1_1" else "B1_0"
            else -> return
        }

        val json = JSONObject().apply {
            put("action", "arduino")
            put("command", command)
        }

        // Hacer el envío en un hilo separado
        thread {
            try {
                // Conectar al servidor de sockets
                val socket = Socket("192.168.0.207", 8080)  // Cambia la IP y puerto a los de tu servidor
                val outputStream: OutputStream = socket.getOutputStream()
                val writer = PrintWriter(outputStream, true)

                // Enviar el mensaje en formato JSON
                writer.println(json.toString())

                // Cerrar el socket
                //writer.close()
                //socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
