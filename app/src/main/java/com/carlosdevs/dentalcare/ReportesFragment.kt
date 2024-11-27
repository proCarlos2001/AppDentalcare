package com.carlosdevs.dentalcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.viewmodel.compose.viewModel
class ReportesFragment : Fragment() {
    // Obtén la instancia del ViewModel
    private val pagosViewModel: PagosViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Vuelve a configurar la vista para observar los datos
        return ComposeView(requireContext()).apply {
            setContent {
                // Observa la lista de pagos desde el ViewModel
                val pagos = pagosViewModel.pagos.observeAsState(emptyList())

                // Mostrar la lista de pagos usando LazyColumn
                ReportesList(pagos = pagos.value)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Cargar los pagos al iniciar el fragmento
        pagosViewModel.cargarPagos()
    }
}
// Composable para mostrar los pagos en una lista
@Composable
fun ReportesList(pagos: List<pagosData>) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Espacio vacío antes de la lista
        Spacer(modifier = Modifier.height(60.dp)) // Ajusta esta altura según lo necesites

        // LazyColumn para mostrar la lista de pagos
        LazyColumn(
            contentPadding = PaddingValues(bottom = 60.dp), // Espaciado adicional para evitar que la barra inferior tape el contenidocontentPadding = PaddingValues(bottom = 60.dp) // Espaciado adicional para evitar que la barra inferior tape el contenido
            modifier = Modifier
                .fillMaxWidth() // Asegura que ocupe el ancho completo
                .padding(10.dp) // Mantiene un padding alrededor de la lista
        ) {
            items(pagos) { pago ->
                ReporteItem(pago = pago)
            }
        }
    }
}
val CustomGreent = Color(0xFF009E0F)
@Composable
fun ReporteItem(pago: pagosData) {
    // Mostrar información del pago dentro de una tarjeta
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Espaciado alrededor de cada tarjeta
        elevation = 4.dp // Sombra de la tarjeta
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Espaciado dentro de la tarjeta
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            ){
                Text(
                    text = "${pago.estado}",
                    style = MaterialTheme.typography.body2,
                    fontSize = 16.sp,
                    color = CustomGreent,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = "${pago.fechaPago}",
                    style = MaterialTheme.typography.body2,
                    fontSize = 16.sp
                )

            }
            // Línea divisora
            Divider(color = Color.Gray, thickness = 1.dp)
            Text(
                text = "Nombre: ${pago.nombre}",
                style = MaterialTheme.typography.body1,
                fontSize = 16.sp
            )
            Text(
                text = "Motivo: ${pago.motivo}",
                style = MaterialTheme.typography.body2,
                fontSize = 16.sp
            )
            Text(
                text = "Monto: $${pago.monto}",
                style = MaterialTheme.typography.body2,
                fontSize = 16.sp
            )
        }
    }
}