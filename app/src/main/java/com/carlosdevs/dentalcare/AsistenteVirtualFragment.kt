package com.carlosdevs.dentalcare

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class AsistenteVirtualFragment : Fragment() {

    private lateinit var model: GenerativeModel
    var extractedText: String = ""
    private var isPdfLoaded = mutableStateOf(false) // Nuevo estado para cargar el PDF

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inicializa PDFBox
        PDFBoxResourceLoader.init(requireContext())
        return ComposeView(requireContext()).apply {
            setContent {
                // Configuración de la UI utilizando Jetpack Compose
                geminiAssistant(model, extractedText, isPdfLoaded.value)
            }
        }
    }
    // Método que se llama cuando la vista del fragmento ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Definimos la API del módelo LLM
        val apiKey = ApiKeyProvider.API_KEY

        model = GenerativeModel(
            modelName = "gemini-1.5-pro",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.5f // Controla la creatividad del modelo
                topK = 50 // Limita el número de palabras candidatas consideradas en cada paso de generación
                topP = 0.9f // Controla la nucleus sampling, considera todas las palabras con alto probabilidad en cada paso
                maxOutputTokens = 500 // Controla la longitud máxima de la respuesta
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
            )
        )
        // Llamar a la función
        downloadPdfFirebase()
    }
    // Función para descargar el pdf desde firebase storage
    fun downloadPdfFirebase() {
        val storage = Firebase.storage
        val storageRef = storage.reference.child("pdfs/informacion_dentalCare.pdf")
        val localFile = File.createTempFile("informacion_dentalCare", ".pdf")

        storageRef.getFile(localFile)
            .addOnSuccessListener {
                Log.d("Firebase", "PDF descargado: ${localFile.absolutePath}")
                // Aquí puedes proceder a leer o mostrar el PDF
                extractedText = extractTextFromPdf(localFile)
                isPdfLoaded.value = true // Actualizar el estado cuando el PDF esté listo
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error al descargar PDF: ${exception.message}")
            }
    }
    private fun extractTextFromPdf(file: File): String {
        var text = ""
        try {
            // Carga el documento PDF
            val document = PDDocument.load(file)
            // Crea un extractor de texto
            val pdfStripper = PDFTextStripper()
            // Extrae el texto del documento
            text = pdfStripper.getText(document)
            // Cierra el documento
            document.close()
        } catch (e: IOException) {
            Log.e("PDFBox", "Error al extraer texto del pdf ${e.message}")
        }
        return text
    }
}
// Colores personalizados para la UI
val CustomGreenThree = Color(0xFF009E0F)
val CustomBlackThree = Color(0xFF000000)
val CustomWhiteThree = Color(0xFFFFFFFF)
@Composable
fun geminiAssistant(model: GenerativeModel, extractedText: String, isPdfLoaded: Boolean) {
    var userInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var isLoading by remember { mutableStateOf(false) }

    // Lista de mensajes en formato de burbuja
    val messages = remember {mutableStateListOf<Pair<String, Boolean>>()}

    // Variable de estado para el indicador de carga
    var isLoadingMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 65.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            state = listState,
            reverseLayout = true // Para que el mensaje más reciente esté al final
        ) {
            items(messages) { message ->
                ChatBubble(
                    message = message.first,
                    isUserMessage = message.second,
                    isLoading = message.first.isEmpty() && !message.second // Muestra carga si es de gemini y no tiene mensaje
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            if (isLoading){
                item {
                    ChatBubble(
                        message = "Cargando...", isUserMessage = false
                    )
                }
            }
        }
        // Caja de entrada para el mensaje
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.LightGray.copy(alpha = 0.3f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = CustomGreenThree
                ),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            val userMessage = userInput
                            // Se agrega el mensaje del usuario a la lista
                            messages.add(0, Pair(userMessage, true))
                            userInput = "" // Limpiar la caja de texto
                            messages.add(0, Pair("", false))
                            isLoadingMessage = true

                            // Enviar la pregunta a Gemini y recibir la respuesta
                            coroutineScope.launch {
                                try {
                                    val queryWithPdfContext = "Basado en el siguiente contexto:\n$extractedText\n\nPregunta: $userMessage"
                                    Log.d("ContextQuery", queryWithPdfContext) // Para verificar si el contexto se agrega correctamente
                                    val response = model.generateContent(queryWithPdfContext)
                                    withContext(Dispatchers.Main) {
                                        val geminiResponse = response.text ?: "Lo siento en este momento no puedo generar una respuesta"

                                        val cleanedResponse = cleanedResponse(geminiResponse)
                                        // Agregar la respuesta de Gemini a la lista
                                        messages[0] = Pair(cleanedResponse, false)
                                    }
                                    listState.animateScrollToItem(messages.size - 1)
                                } catch (e: Exception) {
                                    Log.e("Error", "Fallo en generateContent: ${e.message}")
                                    messages[0] = Pair("Error: ${e.message}", false)
                                } finally {
                                    // Desactivar el estado de carga cuando se reciba la respuesta
                                    isLoadingMessage = false
                                }
                            }
                        }
                    },
                    enabled = isPdfLoaded, // Habilitar botón solo si pdf está cargado
                    modifier = Modifier
                        .size(48.dp)
                        .background(CustomGreenThree, shape = RoundedCornerShape(24.dp))
                )  {
                    Icon(
                        painter = painterResource(id = R.drawable.button_send_assistant),
                        contentDescription = "Enviar",
                        tint = CustomWhiteThree
                    )
                }
        }
    }
}
fun cleanedResponse(response: String): String {
    return response.replace(Regex("\\*+|\\-+"), "").trim()
}
@Composable
fun ChatBubble(message: String, isUserMessage: Boolean, isLoading: Boolean = false) {
    val bubbleColor = if (isUserMessage) CustomGreenThree else Color.LightGray.copy(alpha = 0.3f)
    val alignment = if (isUserMessage) Arrangement.End else Arrangement.Start
    val bubbleShape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = alignment
    ) {
        Box(
            modifier = Modifier
                .background(bubbleColor, bubbleShape)
                .padding(8.dp)
                .widthIn(max = 280.dp) // Limitar el ancho máximo
        ) {
            if (isLoading) {
                // Muestra el indicador de carga mientras espera la respuesta
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp) // Tamaño fijo del indicador
                        .align(Alignment.Center),
                    color = CustomGreenThree
                )
            } else {
                // Muestra el mensaje cuando no está en modo de carga
                Text(
                    text = message,
                    color = if (isUserMessage) CustomWhiteThree else Color.Black
                )
            }
        }
    }
}
