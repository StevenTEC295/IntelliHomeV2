package com.example.intellihome

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private val prohibitedWords = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Cargar las palabras prohibidas desde el archivo de recursos
        loadProhibitedWords()

        setupPasswordValidation()
        setupInputFilter()

        val btnIngresar = findViewById<TextView>(R.id.button_login)
        val usuario = findViewById<EditText>(R.id.editTextEmail)
        val password = findViewById<EditText>(R.id.editTextPassword)
        val action = "login"

        btnIngresar.setOnClickListener {
            thread {
                val jsonData = createJsonData(
                    action,
                    usuario.text.toString(),
                    password.text.toString()
                )
                sendDataToServer("192.168.0.207", 8080, jsonData)
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
        }
    }

    // Cargar palabras prohibidas desde 'prohibited_words.txt'
    private fun loadProhibitedWords() {
        val inputStream = resources.openRawResource(R.raw.prohibited_words)
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            reader.forEachLine { line ->
                prohibitedWords.add(line.trim().lowercase())
            }
        }
    }

    // Filtro que elimina palabras prohibidas del campo de contraseña en tiempo real
    private fun setupInputFilter() {
        val passwordField = findViewById<EditText>(R.id.editTextPassword)

        passwordField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val cleanedText = removeProhibitedWords(it.toString())
                    if (cleanedText != it.toString()) {
                        passwordField.setText(cleanedText)
                        passwordField.setSelection(cleanedText.length)  // Mantener el cursor al final
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Función para eliminar palabras prohibidas del texto ingresado
    private fun removeProhibitedWords(text: String): String {
        var cleanedText = text.lowercase()
        prohibitedWords.forEach { word ->
            cleanedText = cleanedText.replace(word, "", ignoreCase = true)
        }
        return cleanedText
    }

    private fun sendDataToServer(serverIp: String, serverPort: Int, jsonData: String) {
        try {
            val socket = Socket(serverIp, serverPort)
            val outputStream: OutputStream = socket.getOutputStream()
            val printWriter = PrintWriter(outputStream, true)
            val inputStream = BufferedReader(InputStreamReader(socket.getInputStream()))

            printWriter.println(jsonData)

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
            }

            printWriter.close()
            inputStream.close()
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this, "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createJsonData(action: String, username: String, password: String): String {
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
                if (password.length >= 8) {
                    val hasUppercase = password.any { it.isUpperCase() }
                    val hasLowercase = password.any { it.isLowerCase() }
                    val hasDigit = password.any { it.isDigit() }
                    val hasSpecialChar = password.any { !it.isLetterOrDigit() }

                    if (hasUppercase && hasLowercase && hasDigit && hasSpecialChar) {
                        errorTextView.visibility = View.GONE
                    } else {
                        errorTextView.text =
                            "Debe contener al menos 1 mayúscula, 1 minúscula, 1 dígito y 1 carácter especial"
                        errorTextView.visibility = View.GONE
                    }
                } else {
                    errorTextView.text = "La contraseña debe tener exactamente 8 caracteres"
                    errorTextView.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}