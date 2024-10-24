package com.example.IntelliHome

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.intellihome.HomePage
import com.example.intellihome.R

class ListofHostViewActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainLayout: RelativeLayout
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CustomAdapter
    private lateinit var home: ImageView
    private lateinit var addProperty: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listof_host_view)
        sharedPreferences = getSharedPreferences("IntelliHomePrefs", Context.MODE_PRIVATE)
        mainLayout = findViewById(R.id.main)


        //EL RECYCLER VIEW
        recycler = findViewById(R.id.recycleViewListadeCasas)
        home = findViewById(R.id.home)
        addProperty = findViewById(R.id.addProperty)

        addProperty.setOnClickListener {
            navegarAlFormulariopropiedad()
        }

        home.setOnClickListener {
            navegarAlHome()
        }
        val myDataSet = listOf(
            Pair("Información Casa 1", R.drawable.image_casas_template),
            Pair("Información Casa 4", R.drawable.image_casas_template)
            // Agrega más casas según sea necesario
        )

        setupRecyclerView(recycler, myDataSet)


        loadSavedBackground()

    }


    private fun loadSavedBackground() {
        val savedBackground =
            sharedPreferences.getInt("background_resource", R.drawable.redbackground)
        mainLayout.setBackgroundResource(savedBackground)

    }

    /*private fun setupRecyclerView() {
        // Crear un conjunto de datos
        val myDataSet = listOf(
            Pair("Información Casa 1", R.drawable.image_casas_template),
            Pair("Información Casa 2", R.drawable.image_casas_template)
            // Agrega más casas según sea necesario
        )

        // Inicializar el adaptador
        adapter = CustomAdapter(myDataSet)

        // Establecer el adaptador en el RecyclerView
        recycler.adapter = adapter
        // Establecer un LayoutManager para el RecyclerView
        recycler.layoutManager = LinearLayoutManager(this)
    }*/
    private fun setupRecyclerView(recyclerView: RecyclerView, dataSet: List<Pair<String, Int>>) {
        // Inicializar el adaptador con el conjunto de datos proporcionado
        val adapter = CustomAdapter(dataSet)

        // Establecer el adaptador en el RecyclerView
        recyclerView.adapter = adapter

        // Establecer un LayoutManager para el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    }


    private fun navegarAlFormulariopropiedad() {
        val intent = Intent(this, HostViewActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navegarAlHome() {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
        finish()
    }
}