package com.carlosdevs.dentalcare

import CitaViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carlosdevs.dentalcare.PagosViewModel
import com.carlosdevs.dentalcare.R
import com.carlosdevs.dentalcare.citasData
import com.carlosdevs.dentalcare.pagosData

class PagosFragment : Fragment() {
    private val citaViewModel: CitaViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // Observa las citas desde el ViewModel
                val citasList by citaViewModel.citas.observeAsState(emptyList())
                // Llama a CitasScreen con las citas
                CitasScreen(citasList = citasList)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        // Llama a fetchCitas cuando el fragmento inicia
        citaViewModel.fetchCitas()
    }
}

val CustomGreentwo = Color(0xFF009E0F) // Para realizada
@Composable
fun CitasScreen(citasList: List<citasData>, pagosViewModel: PagosViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxSize()){
        Spacer(modifier = Modifier.height(60.dp))
        LazyColumn(
            contentPadding = PaddingValues(bottom = 60.dp), // Espaciado adicional para evitar que la barra inferior tape el contenido
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .background(Color.White)
        ) {
            items(citasList) { cita ->
                CitaItem(cita = cita, pagosViewModel = pagosViewModel)
            }
        }
    }
}
@Composable
fun CitaItem(cita: citasData, pagosViewModel: PagosViewModel) {

    val context = LocalContext.current // Contexto para Toast
    var showDialog by remember { mutableStateOf(false) }
    var cardNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val validCardNumber = "123456789"
    val validPassword = "1234"

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Nombre: ${cita.nombre}",
                style = MaterialTheme.typography.body1,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Motivo: ${cita.motivo}",
                style = MaterialTheme.typography.body2,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Fecha: ${cita.fecha}",
                style = MaterialTheme.typography.body2,
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically // Alinea todo en el centro vertical
            ) {
                Text(
                    text = "Hora: ${cita.hora}",
                    style = MaterialTheme.typography.body2,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .weight(1f) // El texto de la hora ocupa el espacio restante
                )
                if (!cita.isPaid) { // Solo muestra el ícono si no está pagada
                    IconButton(
                        onClick = { showDialog = true },
                        modifier = Modifier.align(Alignment.CenterVertically) // Alinea el icono verticalmente en el centro
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.button_pay),
                            contentDescription = "Pagar",
                            tint = CustomGreentwo
                        )
                    }
                }
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally, // Centrar contenido horizontalmente
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Centrar el título
                            Text(
                                text = "Pago de la cita",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp), // Altura para dar espacio debajo del título
                                style = MaterialTheme.typography.h6,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            // Campo de texto para número de tarjeta
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { cardNumber = it },
                                label = { Text("Número de Tarjeta") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    cursorColor = CustomGreentwo, // Cursor verde
                                    focusedBorderColor = CustomGreentwo, // Borde verde al seleccionar
                                    focusedLabelColor = CustomGreentwo // Color del label al enfocar
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Contraseña") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    cursorColor = CustomGreentwo, // Cursor verde
                                    focusedBorderColor = CustomGreentwo,
                                    focusedLabelColor = CustomGreentwo // Color del label al enfocar
                                )
                            )
                        }
                    },
                    buttons = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally, // Centrar los botones
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    if (cardNumber.isBlank() || password.isBlank()) {
                                        Toast.makeText(
                                            context,
                                            "Por favor, ingresa el número de tarjeta y la contraseña.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else if (cardNumber != validCardNumber || password != validPassword) {
                                        Toast.makeText(
                                            context,
                                            "Número de tarjeta o contraseña incorrectos.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val monto = pagosViewModel.obtenerPrecio(cita.motivo)
                                        val metodoPago = "Tarjeta"
                                        val pagoData = pagosData(
                                            idPago = "",
                                            nombre = cita.nombre,
                                            motivo = cita.motivo,
                                            monto = monto,
                                            metodoPago = metodoPago
                                        )
                                        pagosViewModel.guardarPago(pagoData)
                                        cita.isPaid = true
                                        Toast.makeText(
                                            context,
                                            "Pago de $monto para ${cita.motivo} registrado exitosamente.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showDialog = false
                                    }
                                },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = CustomGreentwo)
                            ) {
                                Text("Confirmar", color = Color.White)
                            }
                            Button(
                                onClick = { showDialog = false },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(), // Ajusta el tamaño del botón
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red) // Botón rojo
                            ) {
                                Text("Cancelar", color = Color.White)
                            }
                        }
                    }
                )
            }
        }
    }
}