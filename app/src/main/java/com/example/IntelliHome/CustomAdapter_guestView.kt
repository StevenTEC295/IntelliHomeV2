package com.example.IntelliHome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.intellihome.R

class CustomAdapter_guestView(
    private val dataSet: List<Pair<String, Int>>,
    private val listener: OnItemClickListener // Pass the listener
) : RecyclerView.Adapter<CustomAdapter_guestView.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.info_de_la_casa_guest)
        val imageView: ImageView = view.findViewById(R.id.imageProperty_guest)
        val button: Button = view.findViewById(R.id.btnRentHouse)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val (info, imageResId) = dataSet[position]
        viewHolder.textView.text = info
        viewHolder.imageView.setImageResource(imageResId)

        viewHolder.button.setOnClickListener {
            val clickedInfo = getInfo(position)
            clickedInfo?.let { listener.onItemClick(it) }
        }
    }

    override fun getItemCount() = dataSet.size

    private fun getInfo(pos: Int): Pair<String, Int>? {
        return if (pos in dataSet.indices) {
            dataSet[pos]
        } else {
            null
        }
    }

    interface OnItemClickListener {
        fun onItemClick(info: Pair<String, Int>)
    }
}
