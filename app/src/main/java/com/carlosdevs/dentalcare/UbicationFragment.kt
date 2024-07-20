package com.carlosdevs.dentalcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

// Definición de la clase UbicationFragment que extiende Fragment e implementa OnMapReadyCallback
class UbicationFragment : Fragment(), OnMapReadyCallback {

    // Declaración de la variable map que almacenará la instancia del mapa
    private lateinit var map: GoogleMap

    // Método onCreateView que se llama cuando se crea la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_ubication, container, false)

        // Obtener el fragmento del mapa y configurar el callback
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    // Método que se llama cuando el mapa está listo para ser utilizado
    override fun onMapReady(googleMap: GoogleMap) {

        // Variable map
        map = googleMap

        // Variable donde se guarda las coordenadas de la ubicación de (DentalCare)
        val location = LatLng(3.430781245705406, -76.51005088391572)

        // Se añade un marcador a la ubicación
        map.addMarker(MarkerOptions().position(location).title("DentalCare"))

        // Permite mover la cámara a la ubicación y ajustar el nivel de zoom
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}
