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

class UbicationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ubication, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {

        /* Variable */
        map = googleMap

        /* Variable donde se guarda las coordenadas de la ubicación de (DentalCare) */
        val location = LatLng(3.430781245705406, -76.51005088391572)

        /* Se añade un marcador a la ubicación */
        map.addMarker(MarkerOptions().position(location).title("DentalCare"))

        /* Permite mover la cámara a la ubicación y ajustar el nivel de zoom */
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}
