package com.carlosdevs.dentalcare

import CitaViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels

class HistorialClinicoFragment : Fragment() {

    // Variable privada citaViewModel para manejar el viewModels
    private val citaViewModel: CitaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                // Configuración de la UI utilizando Jetpack Compose
                HistorialClinicoScreen(citaViewModel)                 // Se instancia la clase CitaViewModel
            }
        }
    }

    // Método que se llama cuando la vista del fragmento ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        citaViewModel.fetchCitas()  // Se visualiza el estado de la cita

        // Manejo del botón de retroceso
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navega al HomeFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            }
        })
    }
}
val CustomGreenTwo = Color(0xFF009E0F)  // Verde para "Realizada"
val CustomOrange = Color(0xFFFF9800)    // Naranja para "Pendiente"
@Composable
fun HistorialClinicoScreen(citaViewModel: CitaViewModel) {

    // Observa el estado de las citas desde el ViewModel
    val citas by citaViewModel.citas.observeAsState(initial = emptyList())

    // Lista donde se visualizan las card creadas para el historial de las citas
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 60.dp)
            .padding(horizontal = 16.dp)
    ) {
        items(citas) { cita ->

            // Se llama la función CitaCard, la cual tiene el diseño de la Card
            CitaCard(cita)
        }
    }
}
@Composable
fun CitaCard(cita: citasData) {

    // Condición que según el estado de la cita, se visualiza determinado color
    val estadoColor = when (cita.estado) {
        "Realizada" -> CustomGreenTwo       // Color verde
        "Pendiente" -> CustomOrange         // Color Naranja
        else -> Color.Black                 // Color negro
    }
    // Tarjeta (Card) que muestra los detalles de la cita
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row {
                Text(
                    text = "Su cita se agendo el:",
                    color = CustomBlack,
                    fontSize = 17.sp,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = "${cita.fechaRegistro}",  // Muestra la fecha en que fue agendada la cita
                    color = CustomBlack,
                    style = MaterialTheme.typography.body2,
                    fontSize = 17.sp,
                )
            }
            // Línea divisora
            Divider(
                color = Color.Gray,
                thickness = 1.dp, // Grosor de la línea
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
            Text(
                text = "Nombre: ${cita.nombre}",  // Nombre del paciente
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
            Text(
                text = "Correo: ${cita.correo}",  // Correo del paciente
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
            Text(
                text = "Motivo: ${cita.motivo}",  // Motivo que ingreso el paciente.
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
            Text(
                text = "Fecha: ${cita.fecha}",  // Fecha que selecciono el paciente
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
            Row(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Hora: ${cita.hora}",  // Hora que selecciono el paciente
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = "${cita.estado}",  // Se visualiza el estado de la cita
                    color = estadoColor, // Color del estado, según lógica aplicada
                    style = MaterialTheme.typography.body2,
                    fontSize = 15.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHistorialClinicoScreen() {
    // Proveer datos de ejemplo para la vista previa
    val citasDePrueba = listOf(
        citasData(nombre = "Carlos", correo = "carlos@example.com", motivo = "Consulta", fecha = "10/07/2023", hora = "10:00 AM", estado = "Pendiente"),
        citasData(nombre = "Ana", correo = "ana@example.com", motivo = "Limpieza", fecha = "12/07/2023", hora = "11:00 AM", estado = "Realizada")
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(citasDePrueba) { cita ->
            CitaCard(cita)
        }
    }
}