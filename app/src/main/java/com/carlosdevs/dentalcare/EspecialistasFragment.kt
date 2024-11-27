package com.carlosdevs.dentalcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore


class EspecialistasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Usamos ComposeView para mostrar la UI de Jetpack Compose en este fragmento
        return ComposeView(requireContext()).apply {
            setContent {
                // Pantalla de especialistas
                SpecialistsScreen()
            }
        }
    }
}
// Data class para representar un especialista
data class Specialist(
    val name: String,
    val specialty: String,
    val schedule: String,
    val patientsAttended: String,
    val biography: String,
    val image: Int,  // ID del recurso drawable
    val rating: Float
)
// Composable que representa la pantalla de especialistas
@Composable
fun SpecialistsScreen() {
    // Lista de especialistas
    val specialists = listOf(
        Specialist("Dr. Andres R.", "Periodoncia",
            "08 am - 01 pm", "325",
            "El Dr. Andres R. es un destacado profesional en el campo de la periodoncia, graduado con altas calificaciones " +
                    "de la Universidad del Valle. A sus 31 años, ha demostrado un compromiso inquebrantable con la salud dental " +
                    "de sus pacientes, ofreciendo un nivel de profesionalismo excepcional en cada consulta. Su enfoque meticuloso y " +
                    "su dedicación al aprendizaje continuo le han permitido alcanzar un notable éxito en su carrera, donde ha atendido " +
                    "a más de 300 pacientes satisfechos.",
            R.drawable.doctor_andres_1_1, 5f),
        Specialist("Dr. Katalina S.", "Ortodoncia",
            "08 am - 05 pm", "230",
            "La Dr. Katalina S. es una reconocida ortodoncista, graduada con honores de la Universidad Javeriana. A sus 29 años," +
                    " ha sobresalido en su campo gracias a su enfoque innovador y atención al detalle. Con más de 200 pacientes atendidos," +
                    " su éxito es el reflejo de su compromiso con la excelencia y su constante búsqueda de nuevas técnicas que mejoren la " +
                    "calidad de los tratamientos. La Dra. Katalina S. es conocida por su dedicación y empatía, lo que la convierte en una " +
                    "profesional altamente valorada tanto por sus pacientes como por sus colegas.",
            R.drawable.doctor_katalina_2_1, 4.0f),
        Specialist("Dra. Esteban P.", "Endodoncia",
            "10 am - 03 pm", "420",
            "El Dr. Esteban P. es un experto en endodoncia, graduado de la Universidad del Cauca, con más de 8 años de experiencia " +
                    "en el tratamiento de casos complejos. A sus 33 años, ha atendido a más de 400 pacientes, salvando dientes que otros " +
                    "consideraban perdidos. Su profesionalismo y dedicación lo han posicionado como uno de los especialistas más confiables " +
                    "en su área. El Dr. Estiven combina tecnología avanzada con un enfoque personalizado, asegurando que cada paciente reciba " +
                    "el mejor tratamiento posible.",
            R.drawable.doctor_esteban_3_1, 3.5f),
        Specialist("Dr. Marco O.", "Odontopediatría",
            "09 am - 06 pm", "125",
            "El Dr. Marco O. es un odontopediatra dedicado, graduado de la Universidad de Antioquia, con más de 10 años de experiencia " +
                    "en el cuidado dental infantil. A sus 38 años, ha tratado a más de 100 niños, enfocándose en crear un ambiente " +
                    "cómodo y seguro para los pequeños pacientes. Su éxito se basa en su habilidad para conectar con los niños y sus " +
                    "familias, asegurando que cada visita sea positiva y libre de estrés. Además, el Dr. Marco se mantiene al tanto de " +
                    "las últimas técnicas y tecnologías en odontopediatría, lo que le permite ofrecer tratamientos efectivos y personalizados. ",
            R.drawable.doctor_marco_4, 3.5f),
        Specialist("Dr. Sandra R.", "Prostodoncia",
            "10 am - 06 pm", "350",
            "La Dra. Sandra R. es una especialista en prostodoncia con más de 12 años de experiencia, graduada de la Universidad " +
                    "del Rosario. A sus 35 años, ha transformado la vida de más de 300 pacientes mediante la restauración y rehabilitación " +
                    "de sonrisas, utilizando prótesis dentales de alta calidad. Con un enfoque en la estética y la funcionalidad, la Dra. " +
                    "Sandra combina arte y ciencia para devolver la confianza y salud bucal a sus pacientes. Es conocida por su meticuloso " +
                    "trabajo y atención al detalle, asegurándose de que cada prótesis se ajuste perfectamente a las necesidades de cada persona. ",
            R.drawable.doctor_sandra_5_1, 3.5f),
        Specialist("Dr. Daniel V.", "Estética dental",
            "08 am - 01 pm", "112",
            "El Dr. Daniel V. es un experto en estética dental, graduado con distinción de la Universidad de Los Andes. A sus 36 años, " +
                    "ha ayudado a más de 100 pacientes a alcanzar la sonrisa perfecta a través de tratamientos innovadores y personalizados. " +
                    "Con un enfoque en la armonía facial y la estética dental, el Dr. Daniel combina tecnología avanzada con un agudo sentido " +
                    "estético, creando resultados que no solo mejoran la apariencia, sino también la confianza de sus pacientes.",
            R.drawable.doctor_daniel_6, 4.5f),
        Specialist("Dr. Dayana P.", "Cirugía oral",
            "11 am - 06 pm", "201",
            "La Dra. Dayana P. es una cirujana oral altamente calificada, graduada de la Universidad del Bosque. A sus 34 años, " +
                    "ha realizado más de 200 procedimientos exitosos, desde extracciones complejas hasta reconstrucciones orales avanzadas. " +
                    "Su habilidad técnica y su enfoque calmado ante situaciones quirúrgicas delicadas la han destacado en el campo de la cirugía oral. " +
                    "La Dra. Dayana es conocida por su precisión y su compromiso con el bienestar de sus pacientes, asegurando una recuperación " +
                    "rápida y efectiva.",
            R.drawable.doctor_dayana_7_1, 5f),
        Specialist("Dr. Andrea L.", "Radiología oral",
            "08 am - 01 pm", "328",
            "La Dra. Andrea L. es una especialista en radiología oral, graduada con honores de la Universidad de Caldas. A sus 26 años, " +
                    "ha interpretado más de 300 imágenes radiográficas, ayudando a odontólogos y cirujanos a diagnosticar con precisión diversas " +
                    "patologías dentales y maxilofaciales. Con un profundo conocimiento de las técnicas radiológicas avanzadas, la Dra. Andrea " +
                    "se destaca por su precisión y capacidad para detectar detalles cruciales que facilitan tratamientos eficaces.",
            R.drawable.doctor_andrea_8_1, 5f),
        // Agrega más especialistas aquí
    )
    // Variable de estado que almacena el especialista seleccionado
    var selectedSpecialistIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        // Lista horizontal de especialistas
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp), // Reduce el espacio entre la card y los iconos
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(specialists.size) { index ->
                SpecialistIcon(
                    specialist = specialists[index],
                    isSelected = index == selectedSpecialistIndex,
                    onClick = { selectedSpecialistIndex = index }
                )
            }
        }
        // Cuadro para mostrar la información del especialista seleccionado
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(495.dp) // Ajusta la altura a lo que consideres adecuado
                .padding(vertical = 16.dp, horizontal = 16.dp),
            elevation = 10.dp
        ) {
            SpecialistDetails(specialist = specialists[ selectedSpecialistIndex])
        }
    }
}
val CustomGreenFour = Color(0xFF009E0F)
// Composable para el ícono del especialista
@Composable
fun SpecialistIcon(specialist: Specialist, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.White else Color.White
    val iconTintColor = if (isSelected) CustomGreenFour else Color.Black

    // Encerrar el icono en una card si esta seleccionado
    Card(
        backgroundColor = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(3.dp)
            .clickable { onClick() }
            .border(1.dp, if (isSelected) CustomGreenFour else Color.Black, shape = RoundedCornerShape(8.dp)) //Borde si esta seleccionado
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onClick() }
                .padding(8.dp) // Ajusta el espacio entre los iconos
        ) {
            Spacer(modifier = Modifier.height(1.dp)) // Ajusta este valor para bajar los íconos
            Image(
                painter = painterResource(id = specialist.image),
                contentDescription = specialist.name,
                modifier = Modifier
                    .size(80.dp),


                contentScale = ContentScale.Crop
            )
            Text(text = specialist.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
// Composable para mostrar los detalles del especialista seleccionado
@Composable
fun SpecialistDetails(specialist: Specialist) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = specialist.image),
                contentDescription = specialist.name,
                modifier = Modifier.size(130.dp), // Tamaño de la imagen aumentado
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = specialist.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = specialist.specialty, fontSize = 14.sp, color = Color.Gray)
                Text(text = "Horarios: ${specialist.schedule}", fontSize = 10.sp)
                Text(text = "Pacientes atendidos: ${specialist.patientsAttended}", fontSize = 12.sp)
                Row {
                    repeat(5) { index ->
                        Icon(
                            painter = painterResource(id = R.drawable.icon_star),
                            contentDescription = "Star",
                            tint = if (index < specialist.rating.toInt()) Color.Yellow else Color.Gray
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Biografía", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = specialist.biography,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            lineHeight = 20.sp // Ajusta la altura de línea si es necesario
        )
    }
}
