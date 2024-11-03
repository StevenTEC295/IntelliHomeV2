package com.example.intellihome

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
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
class ControlHouse : AppCompatActivity() {

    private var isSalaActive = false
    private var isCuarto1Active = false
    private var isCuarto2Active = false
    private var isBath1Active = false
    private lateinit var btnAbrir: Button
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var isOpen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controlhouse)
        btnAbrir = findViewById(R.id.btnAbrirCasa)

        setupBiometricPrompt()
        /*val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt.authenticate(promptInfo)  // Autenticación al inicio
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Manejar el caso donde no haya soporte biométrico
                // Podrías deshabilitar funciones o mostrar un mensaje
            }
        }*/



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


        btnAbrir.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

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
                    sendCommand("Puerta", isOpen)
                }else{
                    btnAbrir.text = "Abierto"
                    btnAbrir.setBackgroundColor(ContextCompat.getColor(this@ControlHouse, R.color.green))
                    sendCommand("Puerta", isOpen)
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
    private fun sendCommand(room: String, isActive: Boolean) {
        val command = when (room) {
            "Sala" -> if (isActive) "S1_1" else "S1_0"
            "Cuarto1" -> if (isActive) "C1_1" else "C1_0"
            "Cuarto2" -> if (isActive) "C2_1" else "C2_0"
            "Baño" -> if (isActive) "B1_1" else "B1_0"
            "Puerta" -> if (isActive) "SERVO_0" else "SERVO_1"
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
                val socket = Socket(Constants.SERVER_IP, Constants.SERVER_PORT)  // Cambia la IP y puerto a los de tu servidor
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
