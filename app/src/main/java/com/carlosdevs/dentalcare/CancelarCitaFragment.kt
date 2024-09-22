package com.carlosdevs.dentalcare

import CitaViewModel
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.FirebaseFirestore

class CancelarCitaFragment : Fragment() {

    private val citaViewModel: CitaViewModel by viewModels() // ViewModel para gestionar el estado
    private var idCita: String? = null // ID de la cita a eliminar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        citaViewModel.citaEliminada.observe(viewLifecycleOwner) { isEliminada ->
            if (isEliminada == true) {
                Toast.makeText(requireContext(), "Cita eliminada con éxito", Toast.LENGTH_SHORT).show()
                // Aquí puedes mostrar un mensaje de éxito o navegar a otra pantalla
                Log.d("CancelarCitaFragment", "Cita eliminada con éxito")
                // Navegar a otro fragmento o acción aquí
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            } else if (isEliminada == false) {
                // Aquí puedes mostrar un mensaje de error
                Toast.makeText(requireContext(), "Error al eliminar la cita", Toast.LENGTH_SHORT).show()
                Log.e("CancelarCitaFragment", "Error al eliminar la cita")
            }
        }
        // Obtenemos el idCita de los argumentos
        idCita = arguments?.getString("idCita") // Asegúrate de que el idCita se esté pasando correctamente

        return ComposeView(requireContext()).apply {
            setContent {
                // Obtenemos los argumentos pasados desde el HistorialClinicoFragment
                val motivo = arguments?.getString("motivo")
                val paciente = arguments?.getString("paciente")
                val fecha = arguments?.getString("fecha")
                val hora = arguments?.getString("hora")
                val idCita = arguments?.getString("idCita")

                // Condicional para comprobar si hay datos de la cita
                if (motivo != null && paciente != null && fecha != null && hora != null) {
                    CancelarCitaScreen(motivo, paciente, fecha, hora, idCita, citaViewModel)
                } else {
                    MensajeSinCita()
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Manejo del botón de retroceso
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            }
        })
    }
}
val CustomRed = Color(0xFFCC0000) // Para pendiente
val CustomGreenthree = Color(0xFF009E0F) // Para realizada


@Composable
fun CancelarCitaScreen(
    motivo: String,
    paciente: String,
    fecha: String,
    hora: String,
    idCita: String?,
    viewModel: CitaViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 60.dp)
    ) {
        CancelarCitaCard(motivo, paciente, fecha, hora, idCita, viewModel)
    }
}

@Composable
fun CancelarCitaCard (
    motivo: String,
    paciente: String,
    fecha: String,
    hora: String,
    idCita: String?,
    viewModel: CitaViewModel
){
    // Caja para mostrar información del servicio y paciente
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .fillMaxHeight(0.9f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                elevation = 4.dp // Sombreado de la tarjeta
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Espaciado entre los elementos
                ) {
                    Text(paciente, fontSize = 30.sp)
                }
            }
            Divider(color = Color.Gray, thickness = 1.dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                elevation = 4.dp // Sombreado de la tarjeta
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Espaciado entre los elementos
                ) {
                    Text(
                        text = stringResource(id = R.string.text_info_cita), // Aquí llamas el string,
                        textAlign = TextAlign.Justify,
                        style = MaterialTheme.typography.body1.copy(fontSize = 23.sp)
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                elevation = 4.dp // Sombreado de la tarjeta
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Espaciado entre los elementos
                ) {
                    Text("Motivo: $motivo", fontSize = 18.sp)
                    Text("Fecha de la Cita: $fecha", fontSize = 18.sp)
                    Text("Hora: $hora", fontSize = 18.sp)
                }
            }
            Text(
                "Id: $idCita",
                fontSize = 10.sp,
                modifier = Modifier.alpha(0f) // Totalmente invisible
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                elevation = 4.dp // Sombreado de la tarjeta
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp), // Espaciado entre los elementos
                    horizontalAlignment = Alignment.CenterHorizontally // Centra el contenido horizontalmente
                ) {
                    Text("!Gracias por confiar en nosotros¡",
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth(), // Asegura que el Text ocupe todo el ancho
                        textAlign = TextAlign.Center // Alinea el texto al centro
                    )
                }
            }

        }
    }

    // Botón de cancelar cita
    Button(
        onClick = {
            // Aquí puedes agregar la lógica para cancelar la cita
            idCita?.let { idCita ->
                viewModel.eliminarCita(idCita) // Usa el ViewModel para eliminar la cita
            } ?: run {
                // Manejar el caso donde idCita es nulo (opcional)
                Log.w("CancelarCita", "ID de cita es nulo, no se puede eliminar")
            }
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(48.dp)
    ) {
        Text(text = "Cancelar Cita", color = Color.White, fontSize = 16.sp)
    }
}
// Composable para mostrar un mensaje cuando no hay cita
@Composable
fun MensajeSinCita() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Para cancelar una cita, navegue al historial clínico y seleccione la cita que desea cancelar.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6
        )
    }
}