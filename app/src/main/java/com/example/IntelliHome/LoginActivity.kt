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
import com.example.IntelliHome.TipoUsuario
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupPasswordValidation()
        val btn1 = findViewById<TextView>(R.id.create_new_account)
        val about = findViewById<ImageButton>(R.id.button_help)
        val btnIngresar = findViewById<TextView>(R.id.button_login)
        val usuario = findViewById<EditText>(R.id.editTextEmail)
        val password = findViewById<EditText>(R.id.editTextPassword)
        val action = "login"
        btnIngresar.setOnClickListener{
            thread {
                val jsonData = createJsonData(
                    action,
                    usuario.text.toString(),
                    password.text.toString()

                )
                sendDataToServer("192.168.0.207",8080,jsonData)
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
        }
        btn1.setOnClickListener {
            //val socket =  SocketConnection();
            //socket.startConnection();
            navegar()
        }
        about.setOnClickListener{
            val intent = Intent(this, About::class.java)
            startActivity(intent)
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
            if (serverResponse != null) {


                if (serverResponse == "1") {
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                println("No se recibió respuesta del servidor")
            }

            // Cierra todo aquí al final
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
    private fun setupPasswordValidation() {
        val passwordField = findViewById<EditText>(R.id.editTextPassword)
        val errorTextView = findViewById<TextView>(R.id.textViewError)

        passwordField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()

                // Verificar si el texto tiene exactamente 8 caracteres
                if (password.length == 8) {
                    val hasUppercase = password.count { it.isUpperCase() } >= 1
                    val hasLowercase = password.count { it.isLowerCase() } >= 1
                    val hasDigit = password.count { it.isDigit() } >= 1
                    val hasSpecialChar = password.any { !it.isLetterOrDigit() }

                    // Validar la contraseña
                    if (hasUppercase && hasLowercase && hasDigit && hasSpecialChar) {
                        errorTextView.visibility = View.GONE  // Ocultar el mensaje de error
                        Toast.makeText(this@LoginActivity, "Contraseña válida", Toast.LENGTH_SHORT).show()
                    } else {
                        // Mostrar el error si no cumple con los requisitos
                        errorTextView.text = "Debe contener al menos 1 mayúscula, 1 minúscula, 1 dígito y 1 carácter especial"
                        errorTextView.visibility = View.VISIBLE
                    }
                } else {
                    // Mostrar error si no tiene exactamente 8 caracteres
                    errorTextView.text = "La contraseña debe tener exactamente 8 caracteres"
                    errorTextView.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No necesario
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No necesario
            }
        })
    }










    private fun navegar(){
        val intent = Intent(this,TipoUsuario::class.java)
        startActivity(intent)
    }



}