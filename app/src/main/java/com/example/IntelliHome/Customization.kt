package com.example.intellihome
import android.content.SharedPreferences

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import com.example.intellihome.R
import android.content.Context
class Customization : AppCompatActivity() {
    private lateinit var mainLayout:RelativeLayout
    private var isDarkMode = false
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customization)
        //iniciar el cambio del fondo
        sharedPreferences = getSharedPreferences("IntelliHomePrefs", Context.MODE_PRIVATE)
        mainLayout=findViewById(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadSavedBackground()
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
        findViewById<RelativeLayout>(R.id.red_card).setOnClickListener {
            setColorBackground(if (isDarkMode) R.drawable.darkredbackground else R.drawable.redbackground)
        }
        findViewById<RelativeLayout>(R.id.pink_card).setOnClickListener {
            setColorBackground(if (isDarkMode) R.drawable.darkpinkbackground else R.drawable.pinkbackground)
        }
        findViewById<RelativeLayout>(R.id.green_card).setOnClickListener {
            setColorBackground(if (isDarkMode) R.drawable.darkgreenbackground else R.drawable.greenbackground)
        }
        findViewById<RelativeLayout>(R.id.blue_card).setOnClickListener {
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
        val editor = sharedPreferences.edit()
        editor.putInt("background_resource", backgroundResId)
        editor.apply() // Apply the changes
    }
    private fun loadSavedBackground() {
        val savedBackground = sharedPreferences.getInt("background_resource", R.drawable.redbackground)
        mainLayout.setBackgroundResource(savedBackground)
    }
}