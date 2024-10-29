package com.example.intellihome
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.IntelliHome.About
import com.example.IntelliHome.SocketConnection

import com.example.intellihome.TipoUsuario

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread

import android.content.SharedPreferences
import android.widget.RelativeLayout
import android.content.Context
import com.example.IntelliHome.Constants
import com.example.IntelliHome.SquarePasswordTransformationMethod
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Scanner

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainLayout: RelativeLayout
    private var changeStatePasswordConfirm=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences("IntelliHomePrefs", Context.MODE_PRIVATE)
        mainLayout = findViewById(R.id.main)


        val btn1 = findViewById<TextView>(R.id.create_new_account)
        val about = findViewById<ImageButton>(R.id.button_help)
        val btnIngresar = findViewById<TextView>(R.id.button_login)
        val usuario = findViewById<EditText>(R.id.editTextEmail)

        val password: TextInputEditText = findViewById(R.id.contrasena)
        val layoutPassword: TextInputLayout = findViewById(R.id.PasswordLayout)

        password.transformationMethod = SquarePasswordTransformationMethod()

        layoutPassword.setEndIconOnClickListener {
            // Verificar si el ícono está activado (contraseña visible)
            if (changeStatePasswordConfirm==0) {
                // Si el ícono está activado, muestra el texto sin transformación
                password.transformationMethod = null
                changeStatePasswordConfirm=1
            } else {
                // Si el ícono está desactivado, vuelve a aplicar la transformación de cuadrados
                password.transformationMethod = SquarePasswordTransformationMethod()
                changeStatePasswordConfirm=0
            }
        }

        password.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Reaplica el método de transformación para asegurarte de que se mantenga
                password.transformationMethod = SquarePasswordTransformationMethod()
            }
        }

        btnIngresar.setOnClickListener{
            val passwordtext = password.text.toString()
            thread {
                val jsonData = createJsonData(
                    Constants.LOGIN,
                    usuario.text.toString(),
                    passwordtext

                )
                sendDataToServer(Constants.SERVER_IP,Constants.SERVER_PORT,jsonData)
                val intent = Intent(this, CambioUser::class.java)
                startActivity(intent)
            }
        }
        btn1.setOnClickListener {
            navegar()
        }
        about.setOnClickListener{
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }

        loadSavedBackground()
    }
    private fun loadSavedBackground() {
        val savedBackground = sharedPreferences.getInt("background_resource", R.drawable.redbackground)
        mainLayout.setBackgroundResource(savedBackground)

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

            /*val serverResponse = inputStream.readLine()

            if (serverResponse != null) {
                if (serverResponse == "1") {
                    val intent = Intent(this, CambioUser::class.java)
                    startActivity(intent)
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                println("No se recibió respuesta del servidor")
            }*/

            // Cierra la conexion
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
    }



    private fun createJsonData(
        action:String,
        username:String,
        password:String
    ): String {
        val json = JSONObject()
        json.put("action", action)
        json.put("usuario", username)
        json.put("password", password)
        return json.toString()
    }

    private fun navegar(){
        val intent = Intent(this,TipoUsuario::class.java)
        startActivity(intent)
    }

}
