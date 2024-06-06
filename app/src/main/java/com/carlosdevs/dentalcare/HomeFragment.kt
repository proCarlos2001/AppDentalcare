package com.carlosdevs.dentalcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carlosdevs.dentalcare.Adapter.RecyclerViewSliderImagenes

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewSliderImagenes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)

        val imagenes = listOf(
            R.drawable.image_one,
            R.drawable.image_two,
            R.drawable.image_three,
            R.drawable.image_four,
            R.drawable.image_five,
            R.drawable.image_six
        )
        adapter = RecyclerViewSliderImagenes(imagenes)

        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        return view
    }
}

