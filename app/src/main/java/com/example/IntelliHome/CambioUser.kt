
package com.example.intellihome

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.intellihome.R
import com.example.intellihome.Registro_propietarioActivity
import com.example.intellihome.RegistroActivity
import android.content.SharedPreferences
import android.widget.RelativeLayout
import android.content.Context
import com.example.IntelliHome.HostViewActivity
import com.example.IntelliHome.ListofHostViewActivity

class CambioUser : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainLayout: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cambio_user)
        sharedPreferences = getSharedPreferences("IntelliHomePrefs", Context.MODE_PRIVATE)
        mainLayout = findViewById(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnHuesped = findViewById<ImageButton>(R.id.btn_huesped)
        val btnAnfitrion = findViewById<ImageButton>(R.id.btn_anfitrion)

        btnAnfitrion.setOnClickListener{
            val intent = Intent(this, ListofHostViewActivity::class.java)
            startActivity(intent)
        }
        btnHuesped.setOnClickListener {
            val intent = Intent(this, guestView::class.java)
            startActivity(intent)
        }
        loadSavedBackground()
    }
    private fun loadSavedBackground() {
        val savedBackground = sharedPreferences.getInt("background_resource", R.drawable.redbackground)
        mainLayout.setBackgroundResource(savedBackground)
    }
}

