package com.example.intellihome

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var btnUploadPhoto: Button
    private lateinit var imageUrl: Uri

    /*private lateinit var socket: Socket
    private lateinit var out_cliente: PrintWriter
    private lateinit var input_server: Scanner
    private lateinit var outputStream: OutputStream*/

    //Variables del registro
    private lateinit var firstNameInput: TextInputEditText
    private lateinit var registerButton: Button

    private lateinit var lastNameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var etusername: TextInputEditText
    private lateinit var accountNumberInput: TextInputEditText
    private lateinit var etvalidunitl: TextInputEditText
    private lateinit var etcvc: TextInputEditText
    private lateinit var etHobbies: EditText
    private lateinit var addressInput  : EditText


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


        val itemTransport = resources.getStringArray(R.array.transport_types).toList()
        val transport: AutoCompleteTextView = findViewById(R.id.autocomplete_transport)
        val adapterTransport = ArrayAdapter(this,R.layout.list_item,itemTransport)
        transport.setAdapter(adapterTransport)
        transport.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            val itemSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(this, "Item: $itemSelected", Toast.LENGTH_SHORT).show()
        }

        val guestPasswordInput: TextInputEditText = findViewById(R.id.contrasena_huesped)
        val guestPasswordConfirmInput: TextInputEditText = findViewById(R.id.contrasena_huesped_confirmar)



        val phoneInput = findViewById<TextInputEditText>(R.id.phonenumber)
        phoneInput.setText(" +506 ")

        var isUpdatingPhone = false

        phoneInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isUpdatingPhone) {
                    if (!s.toString().startsWith(" +506 ")) {
                        isUpdatingPhone = true
                        phoneInput.setText(" +506 ")
                        phoneInput.setSelection(phoneInput.text?.length ?: 0)
                        isUpdatingPhone = false
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        firstNameInput = findViewById(R.id.etNombre)
        registerButton = findViewById(R.id.button_res)
        emailInput = findViewById(R.id.etCorreo)
        lastNameInput = findViewById(R.id.etApellidos)
        etusername = findViewById(R.id.etusername)
        selectDate = findViewById(R.id.selectDate)
        accountNumberInput = findViewById(R.id.etacountNumber)

        accountNumberInput.setText(" CR ")
        var isUpdatingAccountNumber = false

        accountNumberInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isUpdatingAccountNumber) {
                    if (!s.toString().startsWith(" CR ")) {
                        isUpdatingAccountNumber = true
                        accountNumberInput.setText(" CR ")
                        accountNumberInput.setSelection(accountNumberInput.text?.length ?: 0)
                        isUpdatingAccountNumber = false
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        etvalidunitl = findViewById(R.id.etvalidunitl)
        etcvc = findViewById(R.id.etcvc)
        etHobbies = findViewById(R.id.etHobbies)
        addressInput  = findViewById(R.id.Direccion)



        // Initialize UI components

        imageView = findViewById(R.id.foto_de_perfil)
        val button_tomar_foto = findViewById<ImageButton>(R.id.button_tomar_foto)

        imageView.setOnClickListener{
            pickImageGallery()
        }

        registerButton.setOnClickListener {
            // Obtener los datos de entrada
            val action = "registro"
            val firstName = firstNameInput.text.toString()
            val email  = emailInput.text.toString()
            val lastName  = lastNameInput.text.toString()
            val username = etusername.text.toString()
            val birthdate = selectDate.text.toString()
            val accountNumberInput = accountNumberInput.text.toString()
            val etvalidunitl = etvalidunitl.text.toString()
            val etcvc = etcvc.text.toString()
            val autoComplete = autoComplete.text.toString()
            val etHobbies = etHobbies.text.toString()
            val transportInput  = transport.text.toString()
            val addressInput   = addressInput.text.toString()
            val phoneInput = phoneInput.text.toString()
            // Verificar que ningún campo esté vacío
            val campos = listOf(firstName, email , lastName , username, birthdate, accountNumberInput, etvalidunitl,
                etcvc, autoComplete,etHobbies,transportInput ,addressInput,phoneInput)
            if (campos.any { it.isEmpty() }) {
                Toast.makeText(this, getString(R.string.completa_los_campos), Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Salir del evento si hay campos vacíos
            }

            // Obtener las contraseñas
            val password = guestPasswordInput.text.toString()
            val guestPasswordConfirm = guestPasswordConfirmInput.text.toString()

            // Verificar la contraseña
            if (!confirmPassword(password)) {
                Toast.makeText(
                    this,
                    getString(R.string.Mensaje_contras_no_cumple_con_los_requsitos),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener // Salir del evento si la contraseña no cumple requisitos
            }

            if (password != guestPasswordConfirm) {
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
                    action,
                    firstName,
                    lastName ,
                    email ,
                    username,
                    password,
                    birthdate,
                    accountNumberInput,
                    etvalidunitl,
                    etcvc,
                    autoComplete,
                    etHobbies,
                    transportInput ,
                    addressInput,
                    phoneInput
                )
                sendDataToServer("192.168.0.207", 8080,jsonData)
            }
        }



        // Set up click listeners
        /*btnUploadPhoto.setOnClickListener {
            pickImageGallery()
        }*/

        button_tomar_foto.setOnClickListener {
            imageUrl = createImageUri()
            cameraContract.launch(imageUrl)
        }


        selectDate.setOnClickListener {
            showDatePickerDialog()
        }

        etvalidunitl.setOnClickListener {
            showDatePickerDialogValidUntil()
        }


        val floatingActionButtonPassword = findViewById<FloatingActionButton>(R.id.floatingActionButton_contraseña)
        floatingActionButtonPassword.setOnClickListener {
            val message = getString(R.string.Info_contrasena)
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                .setAction("OK") {
                }
                .show()
        }

        val floatingActionButtonConfirmPassword =
            findViewById<FloatingActionButton>(R.id.floatingActionButton_confimar_contrasena)
        floatingActionButtonConfirmPassword.setOnClickListener {
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

    private fun showDatePickerDialogValidUntil() {
        val c = Calendar.getInstance()
        val cDay = c.get(Calendar.DAY_OF_MONTH)
        val cMonth = c.get(Calendar.MONTH)
        val cYear = c.get(Calendar.YEAR)

        val calendarDialog = DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                textMessage(selectedDate)
                etvalidunitl.setText(selectedDate)
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
        action: String,
        firstName: String,
        lastName : String,
        email : String,
        username: String,
        password: String,
        birhtdate: String,
        acountNumber: String,
        validuntil: String,
        cvc: String,
        houseprefence: String,
        hobbies: String,
        transport : String,
        address : String,
        phone : String
    ): String {
        val json = JSONObject()
        json.put("action", action)
        json.put("firstName", firstName)
        json.put("lastName", lastName )
        json.put("email", email )
        json.put("username", username)
        json.put("password", password)
        json.put("birhtdate", birhtdate)
        json.put("acountNumber", acountNumber)
        json.put("validuntil", validuntil)
        json.put("cvc", cvc)
        json.put("houseprefence", houseprefence)
        json.put("hobbie", hobbies)
        json.put("transport", transport )
        json.put("address", address )
        json.put("phone",phone)
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
    private fun confirmPassword(password: String): Boolean {
        val patron = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$")
        return patron.matches(password)
    }

}
