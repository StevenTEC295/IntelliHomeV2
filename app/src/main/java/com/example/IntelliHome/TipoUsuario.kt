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

class TipoUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tipo_usuario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnHuesped = findViewById<ImageButton>(R.id.btn_huesped)
        val btnAnfitrion = findViewById<ImageButton>(R.id.btn_anfitrion)

        btnAnfitrion.setOnClickListener{
            val intent = Intent(this, Registro_propietarioActivity::class.java)
            startActivity(intent)
        }
        btnHuesped.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}