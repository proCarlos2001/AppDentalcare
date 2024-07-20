package com.carlosdevs.dentalcare

import CitaViewModel
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class CitaFragment : Fragment() {

    // ViewModel para manejar la lógica y datos de la cita
    private val citaViewModel: CitaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                // Configuración de la UI utilizando Jetpack Compose
                app(citaViewModel, ::navigateToHistorialClinico)     // Se instancia la clase CitaViewModel
            }
        }
    }

    // Método para navegar al historial clínico después de agendar una cita.
    private fun navigateToHistorialClinico() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HistorialClinicoFragment())
            .addToBackStack(null)
            .commit()
    }

    // Método que se llama cuando la vista del fragmento ha sido creada.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observador pára el resultado de guardar cita.
        citaViewModel.saveCitaResult.observe(viewLifecycleOwner, Observer { result ->
            result.fold(
                onSuccess = {
                    Log.d("Firestore", it)
                    Toast.makeText(requireContext(), "Cita agendada exitosamente", Toast.LENGTH_LONG).show()
                    navigateToHistorialClinico()
                },
                onFailure = { e ->
                    Log.w("Firestore", "Error al agendar cita", e)
                    Toast.makeText(requireContext(), "Error al agendar cita: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        })
    }
}
// Colores personalizados para la UI
val CustomGreen = Color(0xFF009E0F)
val CustomBlack = Color(0xFF000000)
val CustomWhite = Color(0xFFFFFFFF)

// Función que define la UI utilizando Jetpack Compose.
@Preview
@Composable
fun app(citaViewModel: CitaViewModel, onCitaAgendada: () -> Unit) {

    // Variables de estado para almacenar los datos ingresados por el usuario.
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Variables para obtener la fecha y hora actuales.
    val calendar = remember { Calendar.getInstance() }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    // Dialogo para seleccionar la fecha.
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            R.style.CustomDatePickerDialog,
            { _, year, month, dayOfMonth ->
                selectedDate = "$dayOfMonth/${month + 1}/$year"
            },
            year, month, day
        ).apply {
            setOnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate = "$dayOfMonth/${month + 1}/$year"
            }
        }
    }
    // Dialogo para seleccionar la hora.
    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            R.style.CustomTimePickerDialog,
            { _, hourOfDay, minuteOfHour ->
                val amPm = if (hourOfDay < 12) "AM" else "PM"
                val hour12 = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                selectedTime = String.format("%02d:%02d %s", hour12, minuteOfHour, amPm)
            },
            hour, minute, false
        )
    }

    // Colores personalizados para los campos de texto.
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = CustomGreen, // Cambia el color del borde cuando está Selecccionado
        focusedLabelColor = CustomGreen, // Cambia el color de la etiqueta cuando está seleccionada
        cursorColor = CustomGreen // Cambia el color del cursor
    )

    // Colores personalizados para los campos de texto de la fecha y hora.
    val textFieldColorsTwo = TextFieldDefaults.outlinedTextFieldColors(
        unfocusedBorderColor = CustomBlack, // Cambia el color del borde cuando no esta seleccionado
        unfocusedLabelColor = CustomBlack   // Cambia el color de la etiqueta cuando no esta seleccionada
    )

    // Colores personalizados para los botones.
    val buttonColors = ButtonDefaults.buttonColors(
        backgroundColor = CustomGreen, // Cambia el color del fondo del botón.
        contentColor = CustomWhite     // Cambia el color del contenido dentro del botón.
    )

    // Estructura de la interfaz de usuario.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            colors = textFieldColors,
            label = { Text("Ingrese su nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            colors = textFieldColors,
            label = { Text("Ingrese su Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )
        OutlinedTextField(
            value = motivo,
            onValueChange = { motivo = it },
            maxLines = Int.MAX_VALUE,
            colors = textFieldColors,
            label = { Text("Indique el mótivo de su cita") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )
        Card(
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp) // Se ajusta el padding horizontal del botón
                ) {
                    Button(
                        onClick = { datePickerDialog.show() },
                        colors = buttonColors,  // Pinta el botón y el texto.
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Seleccionar Fecha"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (selectedDate.isNotEmpty()) {  // Condición que valida si la fecha se ha seleccionado.
                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        colors = textFieldColorsTwo,
                        label = { Text("Fecha Seleccionada") },
                        enabled = false,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
        Card(
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp) // Se ajusta el padding horizontal del botón
                ) {
                    Button(
                        onClick = { timePickerDialog.show() },
                        colors = buttonColors,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Seleccionar Hora"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (selectedTime.isNotEmpty()) {  // Condición que valida si la hora se ha seleccionado.
                    OutlinedTextField(
                        value = selectedTime,
                        onValueChange = {},
                        colors = textFieldColorsTwo,
                        label = { Text("Hora Seleccionada") },
                        enabled = false,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                if (nombre.isBlank() || correo.isBlank() || motivo.isBlank() || selectedDate.isBlank() || selectedTime.isBlank()) {
                    Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    val cita = citasData(
                        nombre = nombre,
                        correo = correo,
                        motivo = motivo,
                        fecha = selectedDate,
                        hora = selectedTime,
                        estado = ""
                    )
                    citaViewModel.saveCita(cita)
                }
            },
            colors = buttonColors,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Agendar Cita"
            )
        }
    }
}