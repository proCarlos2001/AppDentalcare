package com.carlosdevs.dentalcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carlosdevs.dentalcare.Adapter.ImageAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment() {

    /* Se inicializan las variables a utilizar */
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private lateinit var vibrator: Vibrator
    private lateinit var firestore: FirebaseFirestore // Se inicializa la varible para la conexión con la bd
    private lateinit var exception: Exception

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Se instancia el recyclerView donde se mostraran las imángenes
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        vibrator = requireActivity().getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator

        // Se instancia la conexión con Firebase Firestore
        firestore = FirebaseFirestore.getInstance()
        loadImages()

        /* Se implementa la navegación de los botones desde el home */
        openFragmentHome(view, R.id.card_citas, CitaFragment())
        openFragmentHome(view, R.id.card_historial_clinico, HistorialClinicoFragment())
        openFragmentHome(view, R.id.card_pagos, PagosFragment())

        openFragmentHome2(view, R.id.icon_card_arrow_one, EspecialistasFragment())
        openFragmentHome2(view, R.id.icon_card_arrow_two, ReportesFragment())
        openFragmentHome2(view, R.id.icon_card_arrow_two_three, CancelarCitaFragment())

        // Manejo del botón de retroceso
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Salir de la aplicación
                requireActivity().finish()
            }
        })

        return view
    }

    // Función que permite cargar las imágenes desde Firebase a la app
    private fun loadImages() {
        firestore.collection("images").get()
            .addOnSuccessListener { result ->
                val images = mutableListOf<imageData>()
                for (document in result) {
                    val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(document.getString("url") ?: "")
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        images.add(imageData(uri.toString()))
                        // Una vez que todas las imágenes se han agregado, actualiza el adaptador
                        if (images.size == result.size()) {
                            adapter = ImageAdapter(images)
                            recyclerView.adapter = adapter
                        }
                    }.addOnFailureListener {
                        Toast.makeText(activity, "Error al cargar las imágenes: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
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

