package com.example.intellihome

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btn1 = findViewById<Button>(R.id.boton_registro_huesped)
        btn1.setOnClickListener {
            navegar_huesped()
        }

        val btn_registro_propietario = findViewById<Button>(R.id.boton_registro_propietario)
        btn_registro_propietario.setOnClickListener {
            navegar_propietario()
        }

        // Botón para cambiar a inglés
        val btnChangeToEnglish = findViewById<Button>(R.id.boton_cambiar_ingles)
        btnChangeToEnglish.setOnClickListener {
            setLocale("en")
        }

        // Botón para cambiar a español
        val btnChangeToSpanish = findViewById<Button>(R.id.boton_cambiar_espanol)
        btnChangeToSpanish.setOnClickListener {
            setLocale("es")
        }
    }

    private fun navegar_huesped() {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }

    private fun navegar_propietario() {
        val intent = Intent(this, Registro_propietarioActivity::class.java)
        startActivity(intent)
    }

    // Método para cambiar el idioma
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        // Actualizar configuración
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Reiniciar actividad para aplicar el nuevo idioma
        val refresh = Intent(this, LoginActivity::class.java)
        startActivity(refresh)
        finish()  // Finaliza la actividad actual para evitar que quede en el stack
    }
}
