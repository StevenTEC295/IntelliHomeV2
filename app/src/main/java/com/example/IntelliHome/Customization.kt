package com.example.intellihome

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.widget.TextView
import com.example.intellihome.R
class Customization : AppCompatActivity() {
    private lateinit var mainLayout:RelativeLayout
    private var isDarkMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customization)
        mainLayout=findViewById(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val botonAtras = findViewById<TextView>(R.id.back_button)
        botonAtras.setOnClickListener{
            val intent = Intent(this,  Setting::class.java)
            startActivity(intent)
        }
        //Modo claro /Oscuro
        val toggleButton = findViewById<ToggleButton>(R.id.toggleMode)
        toggleButton.setOnClickListener{
            isDarkMode = toggleButton.isChecked
            if (isDarkMode) {
                updateThemeImagesToDark()
            } else {
                updateThemeImagesToLight()
            }
        }
        findViewById<LinearLayout>(R.id.red_card).setOnClickListener {
            setColorBackground(if (isDarkMode) R.drawable.darkredbackground else R.drawable.redbackground)
        }
        findViewById<LinearLayout>(R.id.pink_card).setOnClickListener {
            setColorBackground(if (isDarkMode) R.drawable.darkpinkbackground else R.drawable.pinkbackground)
        }
        findViewById<LinearLayout>(R.id.green_card).setOnClickListener {
            setColorBackground(if (isDarkMode) R.drawable.darkgreenbackground else R.drawable.greenbackground)
        }
        findViewById<LinearLayout>(R.id.blue_card).setOnClickListener {
            setColorBackground(if (isDarkMode) R.drawable.darkbluebackground else R.drawable.bluebackground)
        }
    }
    // Cambia las imágenes para el modo oscuro
    private fun updateThemeImagesToDark() {
        findViewById<ImageView>(R.id.red_image).setImageResource(R.drawable.darkredbackground)
        findViewById<ImageView>(R.id.pink_image).setImageResource(R.drawable.darkpinkbackground)
        findViewById<ImageView>(R.id.green_image).setImageResource(R.drawable.darkgreenbackground)
        findViewById<ImageView>(R.id.blue_image).setImageResource(R.drawable.darkbluebackground)
    }

    // Cambia las imágenes para el modo claro
    private fun updateThemeImagesToLight() {
        findViewById<ImageView>(R.id.red_image).setImageResource(R.drawable.redbackground)
        findViewById<ImageView>(R.id.pink_image).setImageResource(R.drawable.pinkbackground)
        findViewById<ImageView>(R.id.green_image).setImageResource(R.drawable.greenbackground)
        findViewById<ImageView>(R.id.blue_image).setImageResource(R.drawable.bluebackground)
    }

    // Cambia el fondo de pantalla según el modo
    private fun setColorBackground(backgroundResId: Int) {
        mainLayout.setBackgroundResource(backgroundResId)
    }
}