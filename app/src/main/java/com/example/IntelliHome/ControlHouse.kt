package com.example.intellihome

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.IntelliHome.Constants
import com.example.intellihome.R
import org.json.JSONObject
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.example.IntelliHome.PropertyParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Scanner

class ControlHouse : AppCompatActivity() {
    private lateinit var btnAbrir: Button
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var isOpen = false
    // ImageView para alertas de sensores
    private lateinit var alertFire: ImageView
    private lateinit var alertHumidity: ImageView
    private lateinit var alertEarthquake: ImageView
    //
    private val roomStates = mutableMapOf(
        "Sala" to false,
        "Cuarto1" to false,
        "Cuarto2" to false,
        "Baño" to false,
        "Puerta" to false
    )

    private val handler = android.os.Handler()
    private val repeatInterval: Long = 10000 //  (10000ms = 10 segundos)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controlhouse)
        btnAbrir = findViewById(R.id.btnAbrirCasa)
        // Alertas
        alertFire = findViewById(R.id.alertFire)
        alertHumidity = findViewById(R.id.alertHumidity)
        alertEarthquake = findViewById(R.id.alertEarthquake)
        //
        setupBiometricPrompt()
        // Iniciar el hilo para recibir datos desde el servidor
        //startListeningToServer()

        val areaSala: View = findViewById(R.id.areaSala)
        areaSala.setOnClickListener {
            roomStates["Sala"] = !roomStates["Sala"]!!
            toggleBackground(it, roomStates["Sala"]!!)
            sendCommands(roomStates)
        }

        val Cuarto1: View = findViewById(R.id.Cuarto1)
        Cuarto1.setOnClickListener {
            roomStates["Cuarto1"] = !roomStates["Cuarto1"]!!
            toggleBackground(it, roomStates["Cuarto1"]!!)
            sendCommands(roomStates)
        }

        val Cuarto2: View = findViewById(R.id.Cuarto2)
        Cuarto2.setOnClickListener {
            roomStates["Cuarto2"] = !roomStates["Cuarto2"]!!
            toggleBackground(it, roomStates["Cuarto2"]!!)
            sendCommands(roomStates)
        }

        val Bath1: View = findViewById(R.id.Bath1)
        Bath1.setOnClickListener {
            roomStates["Baño"] = !roomStates["Baño"]!!
            toggleBackground(it, roomStates["Baño"]!!)
            sendCommands(roomStates)

        }
        btnAbrir.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
        handler.post(messageRepeater)

    }
    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // Autenticación exitosa, habilita la interacción con la interfaz
                //Cambiar el texto del buton
                if (isOpen){
                    btnAbrir.text = "Cerrado"
                    btnAbrir.setBackgroundColor(ContextCompat.getColor(this@ControlHouse, R.color.rojo_de_la_app))
                    roomStates["Puerta"] = !roomStates["Puerta"]!!
                    sendDoorCommand(false)
                }else{
                    btnAbrir.text = "Abierto"
                    btnAbrir.setBackgroundColor(ContextCompat.getColor(this@ControlHouse, R.color.green))
                    roomStates["Puerta"] = !roomStates["Puerta"]!!
                    sendDoorCommand(true)
                }
                isOpen = !isOpen

            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Manejo de fallo en autenticación
                Toast.makeText(this@ControlHouse, "Error en la huella", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación requerida")
            .setSubtitle("Por favor, usa tu huella para abrir la casa")
            .setNegativeButtonText("Cancelar")
            .build()
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
    private fun sendCommands(states: Map<String, Boolean>) {
        // Crear el JSON que se enviará al servidor
        val commands = mutableMapOf<String, String>().apply {
            put("Sala", if (states["Sala"] == true) "S1_1" else "S1_0")
            put("Cuarto1", if (states["Cuarto1"] == true) "C1_1" else "C1_0")
            put("Cuarto2", if (states["Cuarto2"] == true) "C2_1" else "C2_0")
            put("Baño", if (states["Baño"] == true) "B1_1" else "B1_0")

        }
        val values: Collection<String> = commands.values
        val comd = values.joinToString(",")
        val json = JSONObject().apply {
            put("action", "arduino")
            put("commands", comd)  // Enviar todos los comandos
        }
        // Hacer el envío en un hilo separado
        sendToServer(json)
    }


    private fun processSensorData(data: String) {
        try {
            // Dividir el string recibido por comas para obtener cada valor
            val list = data.split(",")

            // Asegurarse de que el mensaje contenga tres elementos
            if (list.size >= 3) {
                val humedad = list[0].trim()
                val fuego = list[1].trim()
                val sismo = list[2].trim()

                // Actualizar la interfaz de usuario en el hilo principal
                runOnUiThread {
                    println("Valores recibidos - Humedad: $humedad, Fuego: $fuego, Sismo: $sismo")

                    // Cambiar la visibilidad de los ImageView basándose en los valores recibidos
                    alertFire.visibility = if (fuego == "1") View.VISIBLE else View.GONE
                    alertHumidity.visibility = if (humedad == "1") View.VISIBLE else View.GONE
                    alertEarthquake.visibility = if (sismo == "1") View.VISIBLE else View.GONE
                    // Hacer que el teléfono vibre si hay un sismo
                    if (sismo == "1") {
                        vibratePhone()
                    }

                }
            } else {
                println("Error: La lista no contiene suficientes elementos.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al procesar los datos del sensor: ${e.message}")
        }
    }

    private fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Versión de Android 8.0 o superior
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    // Función para enviar solo el estado de la puerta
    private fun sendDoorCommand(isDoorOpen: Boolean) {
        val doorCommand = if (isDoorOpen) "SERVO_0" else "SERVO_1"
        val json = JSONObject().apply {
            put("action", "arduino")  // Action específico para la puerta
            put("commands", doorCommand)  // Enviar solo el comando de la puerta
        }
        // Hacer el envío en un hilo separado
        sendToServer(json)
    }


    // Hacer el envío en un hilo separado
    private fun sendToServer(json: JSONObject) {
        thread {
            try {
                val socket = Socket(Constants.SERVER_IP, Constants.SERVER_PORT)
                val outputStream: OutputStream = socket.getOutputStream()
                val writer = PrintWriter(outputStream, true)
                // Enviar el mensaje en formato JSON
                writer.println(json.toString())
                //Leemos los mensajes del server
                val inputReader2 = BufferedReader(InputStreamReader(socket.getInputStream()))
                val message = inputReader2.readLine()
                processSensorData(message)
                println("Mensja del sensor:$message\n")

                // Cerrar el socket
                writer.close()
                inputReader2.close()
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private val messageRepeater = object : Runnable {
        override fun run() {
            val json = JSONObject().apply {
                put("action", Constants.SENSORES)  // Action específico para la puerta
            }
            sendToServer(json)
            handler.postDelayed(this, repeatInterval) // Schedule the next execution
        }
    }
}