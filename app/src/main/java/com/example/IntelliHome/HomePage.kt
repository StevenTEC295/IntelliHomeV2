package com.example.intellihome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.intellihome.R

import android.content.SharedPreferences
import android.widget.RelativeLayout
import android.content.Context
import com.example.IntelliHome.HostViewActivity
import com.example.IntelliHome.ListofHostViewActivity

class HomePage : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainLayout: RelativeLayout
    private lateinit var btnforNavigation: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        sharedPreferences = getSharedPreferences("IntelliHomePrefs", Context.MODE_PRIVATE)
        mainLayout = findViewById(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val settings = findViewById<ImageButton>(R.id.button_help)
        settings.setOnClickListener{
            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
        }
        btnforNavigation = findViewById(R.id.boton_para_probar)
        btnforNavigation.setOnClickListener {
            navegar()
        }

        loadSavedBackground()
    }
    private fun loadSavedBackground() {
        val savedBackground = sharedPreferences.getInt("background_resource", R.drawable.redbackground)
        mainLayout.setBackgroundResource(savedBackground)

    }
    private fun navegar(){
        val intent = Intent(this,ListofHostViewActivity::class.java)
        startActivity(intent)
    }
}