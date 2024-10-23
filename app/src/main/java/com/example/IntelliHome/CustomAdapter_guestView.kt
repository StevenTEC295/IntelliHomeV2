package com.example.IntelliHome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.intellihome.R

class CustomAdapter_guestView(private val dataSet: List<Pair<String, Int>>) : // Cambia a List<Pair<String, Int>>
    RecyclerView.Adapter<CustomAdapter_guestView.ViewHolder>() {

    // Define el ViewHolder
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.info_de_la_casa)
        val imageView: ImageView = view.findViewById(R.id.imageProperty) // Agrega la ImageView

        init {
            // Define el click listener si es necesario
        }
    }

    // Crea nuevas vistas
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row, viewGroup, false)

        return ViewHolder(view)
    }

    // Reemplaza el contenido de una vista
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val (info, imageResId) = dataSet[position]
        viewHolder.textView.text = info
        viewHolder.imageView.setImageResource(imageResId) // Establece la imagen
    }

    // Retorna el tamaño del dataset
    override fun getItemCount() = dataSet.size
}