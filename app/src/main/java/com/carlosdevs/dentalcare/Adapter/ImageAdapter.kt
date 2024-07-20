package com.carlosdevs.dentalcare.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.carlosdevs.dentalcare.R
import com.carlosdevs.dentalcare.imageData

// El adaptador se usa para manejar una lista de imágenes en un RecyclerView
class ImageAdapter(private val images: List<imageData>): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    // La clase ViewHolder interna contiene la referencia a los elementos de la vista que se van a reutilizar
    class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)       // Referencia al ImageView en el layout
    }

    // Método para inflar la vista del item del RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_imagenes, parent, false)
        return ImageViewHolder(view)
    }

    // Método para enlazar los datos de la imagen con la vista del item
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]   // Obtiene la imagen en la posición actual de la lista
        // Usa Glide para cargar la imagen desde una URL en el ImageView
        Glide.with(holder.itemView.context).load(image.url).into(holder.imageView)
    }

    // Método para devolver la cantidad de items en el RecyclerView
    override fun getItemCount(): Int = images.size  // Devuelve el tamaño de la lista de imágenes
}