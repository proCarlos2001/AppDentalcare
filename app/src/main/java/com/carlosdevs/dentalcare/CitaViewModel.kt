import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosdevs.dentalcare.citasData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale

// viewModel para manejar las operaciones relacionadas con las citas
class CitaViewModel : ViewModel() {

    // Instancia de Firebase Firestore para acceder a la base de datos
    private val db = FirebaseFirestore.getInstance()

    // Instancia de Firebase Authentication para la autenticación de usuarios
    private val auth = FirebaseAuth.getInstance()

    private val _citaEliminada = MutableLiveData<Boolean>()
    val citaEliminada: LiveData<Boolean> = _citaEliminada

    // LiveData para manejar el resultado de guardar una cita
    private val _saveCitaResult = MutableLiveData<Result<String>>()
    val saveCitaResult: LiveData<Result<String>> = _saveCitaResult

    // LiveData para manejar la lista de citas
    private val _citas = MutableLiveData<List<citasData>>()
    val citas: LiveData<List<citasData>> = _citas

    // Variable que almacena el formato de fechas y horas
    private val formatter = DateTimeFormatter.ofPattern("d/M/yyyy h:mm a", Locale.US)

    // Función para guardar una nueva cita en Firestore
    fun saveCita(citaData: citasData) {
        val currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"))
        val user = auth.currentUser

        if (user != null) {
            val citaMap = hashMapOf(
                "nombre" to citaData.nombre,
                "correo" to citaData.correo,
                "motivo" to citaData.motivo,
                "fecha" to citaData.fecha,
                "hora" to citaData.hora,
                "estado" to citaData.estado,
                "fechaRegistro" to currentDate,
                "userId" to user.uid
            )

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    db.collection("citas")
                        .add(citaMap)
                        .await()
                    _saveCitaResult.postValue(Result.success("Cita guardada exitosamente"))
                } catch (e: Exception) {
                    _saveCitaResult.postValue(Result.failure(e))
                }
            }
        } else {
            _saveCitaResult.postValue(Result.failure(Exception("No hay un usuario autenticado")))
        }
    }

    // Función para obtener las citas desde Firestore
    fun fetchCitas() {
        val user = auth.currentUser
        if (user != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // Filtra las citas por el userId del usuario autenticado
                    val result = db.collection("citas")
                        .whereEqualTo("userId", user.uid)  // Filtra por el userId
                        .get()
                        .await()

                    val citasList = result.documents.mapNotNull { document ->
                        val cita = document.toObject(citasData::class.java)?.copy(idCita = document.id) // Agregar el ID de la cita
                        cita?.let {
                            val estadoActualizado = getCitaEstado(it.fecha, it.hora)
                            val citaConEstado = it.copy(estado = estadoActualizado) // Guarda el estado actual de la cita

                            if (it.estado != estadoActualizado) {
                                try {
                                    document.reference.update("estado", estadoActualizado)  // Actualiza el estado de la cita en Firestore
                                    Log.d("CitaViewModel", "Estado de cita actualizado: ${cita.fecha} ${cita.hora} -> $estadoActualizado")
                                } catch (e: Exception) {
                                    Log.e("CitaViewModel", "Error al actualizar estado de la cita", e)
                                }
                            }
                            citaConEstado
                        }
                    }
                    _citas.postValue(citasList)
                } catch (e: Exception) {
                    Log.w("Firestore", "Error al obtener citas", e)
                }
            }
        } else {
            Log.w("Firestore", "No hay un usuario autenticado")
        }
    }

    // Función que determina el estado de una cita basada en la fecha y hora
    private fun getCitaEstado(fecha: String, hora: String): String {  // Parametros (Fecha y Hora)
        return try {
            val currentDateTime = LocalDateTime.now()  // Variable que almecena la fecha y hora actual
            val dateTimeString = "$fecha $hora"
            Log.d("CitaViewModel", "Parsing date and time: $dateTimeString")  // Log para depuración
            Log.d("CitaViewModel", "Using format: ${formatter.toString()}")  // Log para verificar patrón del formato

            // Compara la fecha y hora de la cita con la fecha y hora actuales
            val citaDateTime = LocalDateTime.parse(dateTimeString, formatter)

            // Condición (Cuando), devuelve el estado de la cita
            when {
                // Si la fecha y hora de la cita ya paso devuelve "Relizada"
                citaDateTime.isBefore(currentDateTime) -> {
                    Log.d("CitaViewModel", "Cita pasada: $dateTimeString")  // Log para hacer pruebas
                    "Realizada"
                }
                // Si la fecha y hora son las mismas devuelve "En curso"
                citaDateTime.isEqual(currentDateTime) -> {
                    Log.d("CitaViewModel", "Cita en curso: $dateTimeString") // Log para hacer pruebas
                    "En Curso"
                }
                // En caso de que no se cumplan las condiciones anteriores devuelve "Pendiente"
                else -> {
                    Log.d("CitaViewModel", "Cita pendiente: $dateTimeString")  // Log para hacer pruebas
                    "Pendiente"
                }
            }
        // Devuelve un mensaje de "Error en fecha" si hay un error en el formato
        } catch (e: DateTimeParseException) {
            Log.e("CitaViewModel", "Error en el formato: ${e.message}")  // Log para hacer pruebas
            "Error en fecha"
        }
    }
    fun eliminarCita(idCita: String) {
        val user = FirebaseAuth.getInstance().currentUser // Obtener el usuario autenticado

        // Verificar si el usuario está autenticado
        if (user != null) {
            val db = FirebaseFirestore.getInstance()

            db.collection("citas").document(idCita)
                .delete()
                .addOnSuccessListener {
                    _citaEliminada.value = true // Notifica que la cita fue eliminada
                }
                .addOnFailureListener { exception ->
                    _citaEliminada.value = false // Notifica que hubo un error
                    Log.e("CitaViewModel", "Error al eliminar la cita", exception)
                }
        } else {
            _citaEliminada.value = false // Notifica que el usuario no está autenticado
            Log.e("CitaViewModel", "Usuario no autenticado")
        }
    }
    // Función para resetear el estado si es necesario
    fun resetCitaEliminada() {
        _citaEliminada.value = null
    }
}