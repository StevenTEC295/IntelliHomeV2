package com.example.intellihome

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Scanner
import kotlin.concurrent.thread

//El huesped

class RegistroActivity : AppCompatActivity() {
    private lateinit var selectDate: TextInputEditText
    private lateinit var imageView: ImageView
    private lateinit var button_subir_foto: Button
    private lateinit var imageUrl: Uri

    private lateinit var socket: Socket
    private lateinit var out_cliente: PrintWriter
    private lateinit var input_server: Scanner
    private lateinit var outputStream: OutputStream

    //Variables del registro
    private lateinit var etNombre: TextInputEditText
    private lateinit var btnRegistro: Button

    private lateinit var etApellidos: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etusername: TextInputEditText
    private lateinit var etacountNumber: TextInputEditText
    private lateinit var etvalidunitl: TextInputEditText
    private lateinit var etcvc: TextInputEditText
    private lateinit var etHobbies: EditText
    private lateinit var etTransporte: TextInputEditText
    private lateinit var etDireccion: EditText


    // Register for camera activity result
    private val cameraContract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        imageView.setImageURI(imageUrl)
    }

    // Register for gallery activity result
    private val galleryContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageView.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Set up dropdown menu for house types
        //val items = listOf("Rustica", "Moderna", "Mansion")
        val items = resources.getStringArray(R.array.house_types).toList()
        val autoComplete: AutoCompleteTextView = findViewById(R.id.autocomplete_text_house)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        autoComplete.setAdapter(adapter)
        autoComplete.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            val itemSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(this, "Item: $itemSelected", Toast.LENGTH_SHORT).show()
        }

        val etcontrasena_huesped: TextInputEditText = findViewById(R.id.contrasena_huesped)
        val etcontrasena_huesped_confirmar: TextInputEditText = findViewById(R.id.contrasena_huesped_confirmar)

        etNombre = findViewById(R.id.etNombre)
        btnRegistro = findViewById(R.id.button_res)
        etCorreo = findViewById(R.id.etCorreo)
        etApellidos = findViewById(R.id.etApellidos)
        etusername = findViewById(R.id.etusername)
        selectDate = findViewById(R.id.selectDate)
        etacountNumber = findViewById(R.id.etacountNumber)
        etvalidunitl = findViewById(R.id.etvalidunitl)
        etcvc = findViewById(R.id.etcvc)
        etHobbies = findViewById(R.id.etHobbies)
        etTransporte = findViewById(R.id.etTransporte)
        etDireccion= findViewById(R.id.Direccion)

        // Initialize UI components
        button_subir_foto = findViewById(R.id.button_subir_foto)
        imageView = findViewById(R.id.foto_de_perfil)
        val button_tomar_foto = findViewById<Button>(R.id.button_tomar_foto)


        btnRegistro.setOnClickListener {
            // Obtener los datos de entrada
            val nombre = etNombre.text.toString()
            val correo = etCorreo.text.toString()
            val apellidos = etApellidos.text.toString()
            val username = etusername.text.toString()
            val birthdate = selectDate.text.toString()
            val etacountNumber = etacountNumber.text.toString()
            val etvalidunitl = etvalidunitl.text.toString()
            val etcvc = etcvc.text.toString()
            val autoComplete = autoComplete.text.toString()
            val etHobbies = etHobbies.text.toString()
            val etTransporte = etTransporte.text.toString()
            val etDireccion = etDireccion.text.toString()
            // Verificar que ningún campo esté vacío
            val campos = listOf(nombre, correo, apellidos, username, birthdate, etacountNumber, etvalidunitl,
                etcvc, autoComplete,etHobbies,etTransporte,etDireccion)
            if (campos.any { it.isEmpty() }) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Salir del evento si hay campos vacíos
            }

            // Obtener las contraseñas
            val contrasena = etcontrasena_huesped.text.toString()
            val contrasena_confirmar = etcontrasena_huesped_confirmar.text.toString()

            // Verificar la contraseña
            if (!comprobarContrasena(contrasena)) {
                Toast.makeText(
                    this,
                    getString(R.string.Mensaje_contras_no_cumple_con_los_requsitos),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener // Salir del evento si la contraseña no cumple requisitos
            }

            if (contrasena != contrasena_confirmar) {
                Toast.makeText(
                    this,
                    getString(R.string.Mensaje_contras_no_son_iguales),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener // Salir del evento si las contraseñas no coinciden
            }

            // Si esta correcto enviar datos al server
            Toast.makeText(
                this,
                getString(R.string.Mensaje_exito_registro),
                Toast.LENGTH_SHORT
            ).show()

            thread {
                val jsonData = createJsonData(
                    nombre,
                    apellidos,
                    correo,
                    username,
                    contrasena,
                    birthdate,
                    etacountNumber,
                    etvalidunitl,
                    etcvc,
                    autoComplete,
                    etHobbies,
                    etTransporte,
                    etDireccion
                )
                sendDataToServer("192.168.0.196", 8080,jsonData)
            }
        }

        //Esto codigo no es necesario ya que no ocupamos una conexion constante ni escuhar mensajes solo enviar
        /*thread {
            receiveDataFromServer("192.168.0.196", 8080)
            *//*try {
                // Cambiar a la dirección IP de su servidor
                socket = Socket("192.168.0.196", 8080)

                // Si la conexión es exitosa, se crean los streams de entrada y salida
                out_cliente = PrintWriter(socket.getOutputStream(), true)
                input_server = Scanner(socket.getInputStream())
                //No se ocupa escuhar datos por lo tanto no se implementa este thread
                thread {
                    try {
                        while (true) {
                            if (input_server.hasNextLine()) {
                                val mensajeServidor = input_server.nextLine()
                                println(mensajeServidor)
                            }
                        }
                    } catch (e: Exception) {
                        println("Error al leer del servidor.")
                        e.printStackTrace()
                    }
                }*//*
            } catch (e: UnknownHostException) {
                // Error en la resolución del host
                println("Error: No se pudo encontrar el host. Verifique la dirección IP.")
                e.printStackTrace()
            } catch (e: IOException) {
                // Error de entrada/salida, por ejemplo, si el socket no puede conectarse
                println("Error: Problema de conexión con el servidor.")
                e.printStackTrace()
            } catch (e: Exception) {
                // Captura cualquier otra excepción no prevista
                println("Error: Ocurrió un error inesperado.")
                e.printStackTrace()
            } finally {
                try {
                    input_server.close()
                    out_cliente.close()
                    socket.close()
                    println("Se cierran las conexiones por seguridad")
                } catch (e: IOException) {
                    println("Error al cerrar el socket.")
                    e.printStackTrace()
                }
            }
        }*/
        //THREAD para escuhar datos no es necesario porque no estamos pidiendo datos pero esta como referencia
        /*thread {
            receiveDataFromServer("192.168.0.196", 8080)
        }*/
        /*thread {
            socket = Socket("192.168.0.196", 8080)
            outputStream = socket.getOutputStream()
            out_cliente = PrintWriter(outputStream, true)
            input_server = Scanner(socket.getInputStream())

        }*/

        // Set up click listeners
        button_subir_foto.setOnClickListener {
            pickImageGallery()
        }

        button_tomar_foto.setOnClickListener {
            imageUrl = createImageUri()
            cameraContract.launch(imageUrl)
        }


        selectDate.setOnClickListener {
            showDatePickerDialog()
        }
        val floatingActionButton_contraseña = findViewById<FloatingActionButton>(R.id.floatingActionButton_contraseña)
        floatingActionButton_contraseña.setOnClickListener {
            val message = getString(R.string.Info_contrasena)
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                .setAction("OK") {
                }
                .show()
        }

        val floatingActionButton_confimar_contrasena =
            findViewById<FloatingActionButton>(R.id.floatingActionButton_confimar_contrasena)
        floatingActionButton_confimar_contrasena.setOnClickListener {
            val message = getString(R.string.Info_contrasena)
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                .setAction("OK") {
                }
                .show()
        }
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val cDay = c.get(Calendar.DAY_OF_MONTH)
        val cMonth = c.get(Calendar.MONTH)
        val cYear = c.get(Calendar.YEAR)

        val calendarDialog = DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                textMessage(selectedDate)
                selectDate.setText(selectedDate)
            }, cYear, cMonth, cDay
        )
        calendarDialog.show()
    }

    private fun textMessage(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    // Use the new contract to pick an image from the gallery
    private fun pickImageGallery() {
        galleryContract.launch("image/*")
    }

    private fun createImageUri(): Uri {
        val image = File(filesDir, "camara_photo.png")
        if (image.exists()) {
            image.delete()
        }
        return FileProvider.getUriForFile(this, "com.example.intellihome.FileProvider", image)
    }

    private fun createJsonData(
        nombre: String,
        apellidos: String,
        correo: String,
        username: String,
        password: String,
        birhtdate: String,
        acountNumber: String,
        validuntil: String,
        cvc: String,
        houseprefence: String,
        hobbies: String,
        transporte: String,
        direccion: String
    ): String {
        val json = JSONObject()
        json.put("nombre", nombre)
        json.put("apellidos", apellidos)
        json.put("correo", correo)
        json.put("username", username)
        json.put("password", password)
        json.put("birhtdate", birhtdate)
        json.put("acountNumber", acountNumber)
        json.put("validuntil", validuntil)
        json.put("cvc", cvc)
        json.put("houseprefence", houseprefence)
        json.put("hobbie", hobbies)
        json.put("transporte", transporte)
        json.put("direccion", direccion)
        return json.toString()
    }

    private fun sendDataToServer(serverIp: String, serverPort: Int,jsonData: String) {
        try {
            val socket = Socket(serverIp, serverPort)
            val outputStream: OutputStream = socket.getOutputStream()
            val printWriter = PrintWriter(outputStream, true)

            printWriter.println(jsonData)
            outputStream.close()
            printWriter.close()
            socket.close()
            println("Se cerro la conexion - envio")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al enviar los datos - envio")
        }
    }
    /*private fun receiveDataFromServer(serverIp: String, serverPort: Int) {
        try {
            val socket = Socket(serverIp, serverPort)
            val inputStream: InputStream = socket.getInputStream()
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            // Escuchar mensajes en un bucle
            while (true) {
                val message = bufferedReader.readLine()
                if (message != null) {
                    println("Mensaje recibido: $message")
                } else {
                    break // Salir si no hay más mensajes
                }
            }
            // Cerrar flujos y socket
            bufferedReader.close()
            inputStream.close()
            socket.close()
            println("Se cerró la conexión - eschucha")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al recibir los datos - eschuca")
        }
    }*/
    private fun comprobarContrasena(contrasena: String): Boolean {
        val patron = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$")
        return patron.matches(contrasena)
    }

}
