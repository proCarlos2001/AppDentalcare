package com.carlosdevs.dentalcare.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.carlosdevs.dentalcare.R

class RecyclerViewSliderImagenes(private val imagenes: List<Int>) : RecyclerView.Adapter<RecyclerViewSliderImagenes.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_imagenes, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imagenes.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imagenResId = imagenes[position]
        holder.imageView.setImageResource(imagenResId)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}