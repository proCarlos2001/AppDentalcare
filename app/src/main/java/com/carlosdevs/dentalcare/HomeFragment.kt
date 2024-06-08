package com.carlosdevs.dentalcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carlosdevs.dentalcare.Adapter.RecyclerViewSliderImagenes

class HomeFragment : Fragment() {

    /* Se inicializan las variables a utilizar */
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewSliderImagenes
    private lateinit var vibrator: Vibrator

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

        vibrator = requireActivity().getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator

        /* Se implementa la navegación de los botones desde el home */
        openFragmentHome(view, R.id.card_citas, CitaFragment())
        openFragmentHome(view, R.id.card_historial_clinico, HistorialClinicoFragment())
        openFragmentHome(view, R.id.card_pagos, PagosFragment())

        openFragmentHome2(view, R.id.icon_card_arrow_one, EspecialistasFragment())
        openFragmentHome2(view, R.id.icon_card_arrow_two, ReportesFragment())
        openFragmentHome2(view, R.id.icon_card_arrow_two_three, CancelarCitaFragment())

        return view
    }
    /* Se crea una función para navegar a los fragmentos con la propiedad CardView */
    private fun openFragmentHome(view: View, buttonId: Int, fragment: Fragment) {
        val transactionHome = view.findViewById<CardView>(buttonId)
        transactionHome.setOnClickListener {

            // Vibrar al presionar el botón
            vibrate()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

        }
    }
    /* Se crea una función para navegar a los fragmentos con la propiedad ImageView */
    private fun openFragmentHome2(view: View, buttonId: Int, fragment: Fragment) {
        val transactionHome2 = view.findViewById<ImageView>(buttonId)
        transactionHome2.setOnClickListener {

            // Vibrar al presionar el botón
            vibrate()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

        }
    }
    /* Se crea una función para manejar la vibración del CardView y ImageView */
    private fun vibrate() {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    }
}

