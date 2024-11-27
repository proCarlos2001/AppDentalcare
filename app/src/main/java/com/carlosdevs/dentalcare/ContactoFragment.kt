package com.carlosdevs.dentalcare

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

class ContactoFragment : Fragment() {

    private val TAG = "ContactoFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                ContactScreen(onCallPhone = { phoneNumber -> callPhone(phoneNumber) })
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    // Función para realizar una llamada
    private fun callPhone(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PHONE)
        } else {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        }
    }
    companion object {
        private const val REQUEST_CALL_PHONE = 1
    }
}
@Composable
fun ContactScreen(onCallPhone: (String) -> Unit) {
    // Color de fondo
    val backgroundColor = Color(0xFFFFFFFF) // Blanco
    val black = Color(0xFF000000) // Negro
    val buttonColor = Color(0xFF009E0F) // Verde

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Alineamos los elementos en la parte superior
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        // Imagen del logo
        Image(
            painter = painterResource(id = R.drawable.logo_grande), // Asegúrate de que el logo esté en res/drawable
            contentDescription = "Logo de la Clínica",
            contentScale = ContentScale.Fit, // Escalamos la imagen para que encaje bien
            modifier = Modifier
                .size(200.dp) // Tamaño del logo
                .padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "¡Contáctanos!",
            style = MaterialTheme.typography.h4.copy(fontSize = 28.sp),
            color = buttonColor, // Verde oscuro
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "Si tienes preguntas o necesitas más información, ¡no dudes en llamarnos!",
            style = MaterialTheme.typography.body1.copy(fontSize = 18.sp),
            color = black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(35.dp))

        Button(
            onClick = {
                val clinicPhoneNumber = "3177145142"
                onCallPhone(clinicPhoneNumber)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Llamar a la clínica", color = Color.White)
        }
    }
}
