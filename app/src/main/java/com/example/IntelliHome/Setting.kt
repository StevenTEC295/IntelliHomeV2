package com.example.intellihome


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.widget.Button

import android.widget.RelativeLayout

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


import com.example.intellihome.Customization

import com.example.intellihome.R
import java.util.Locale

class Setting : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_setting)

        sharedPreferences = getSharedPreferences("IntelliHomePrefs", Context.MODE_PRIVATE)
        mainLayout = findViewById(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val botonTemas = findViewById<Button>(R.id.themes_button)
        botonTemas.setOnClickListener{
            val intent = Intent(this, Customization::class.java)
            startActivity(intent)
        }
        val botonAyuda = findViewById<Button>(R.id.help_button)
        botonAyuda.setOnClickListener{
            val intent = Intent(this, Ayuda::class.java)
            startActivity(intent)
        }
        // Botón para cambiar a inglés
        val btnChangeToEnglish = findViewById<Button>(R.id.spanish_button)
        btnChangeToEnglish.setOnClickListener {
            setLocale("es")
        }

        // Botón para cambiar a español
        val btnChangeToSpanish = findViewById<Button>(R.id.english_button)
        btnChangeToSpanish.setOnClickListener {
            setLocale("en")
        }

        loadSavedBackground()


    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        // Actualizar configuración
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Reiniciar actividad para aplicar el nuevo idioma
        val refresh = Intent(this, Setting::class.java)
        startActivity(refresh)
        finish()  // Finaliza la actividad actual para evitar que quede en el stack
    }

    private fun loadSavedBackground() {
        val savedBackground = sharedPreferences.getInt("background_resource", R.drawable.redbackground)
        mainLayout.setBackgroundResource(savedBackground)
    }

}