package com.carlosdevs.dentalcare

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PagosViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance() // Instancia de Firestore
    private val auth = FirebaseAuth.getInstance() // Instancia de autenticación Firebase
    private val _pagos = MutableLiveData<List<pagosData>>() // LiveData para observar los pagos
    private val _reportes = MutableLiveData<MutableList<pagosData>>(mutableListOf())
    val reportes: LiveData<MutableList<pagosData>> = _reportes

    val pagos: LiveData<List<pagosData>> get() = _pagos

    // Función para guardar un pago
    fun guardarPago(pagosData: pagosData) {
        val user = auth.currentUser
        val fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("d/M/yyyy h:mm a"))

        if (user != null) {
            val pagoMap = hashMapOf(
                "nombre" to pagosData.nombre,
                "motivo" to pagosData.motivo,
                "monto" to pagosData.monto,
                "metodoPago" to pagosData.metodoPago,
                "fechaPago" to fechaActual,
                "userId" to user.uid,
            )
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    db.collection("pagos")
                        .add(pagoMap)
                        .await()
                    Log.d("PagoViewModel", "Pago guardado exitosamente")
                } catch (e: Exception) {
                    Log.e("PagoViewModel", "Error al guardar pago", e)
                }
            }
        } else {
            Log.e("PagoViewModel", "Usuario no autenticado")
        }
    }

    // Función para cargar los pagos del usuario
    fun cargarPagos() {
        val user = auth.currentUser
        if (user != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = db.collection("pagos")
                        .whereEqualTo("userId", user.uid)
                        .get()
                        .await()

                    val pagosList = result.documents.mapNotNull { document ->
                        document.toObject(pagosData::class.java)?.copy(idPago = document.id)
                    }
                    _pagos.postValue(pagosList)
                } catch (e: Exception) {
                    Log.e("PagoViewModel", "Error al cargar pagos", e)
                }
            }
        } else {
            Log.e("PagoViewModel", "Usuario no autenticado")
        }
    }

    // Función para obtener el precio de un motivo específico
    fun obtenerPrecio(motivo: String): Int {
        val precios = mapOf(
            "Periodoncia" to 50000,
            "Ortodoncia" to 120000,
            "Endodoncia" to 80000,
            "Odontopediatría" to 100000,
            "Prostodoncia" to 150000,
            "Estética dental" to 200000,
            "Cirugía oral" to 130000,
            "Radiología oral" to 60000
        )
        return precios.entries.find { it.key.equals(motivo.trim(), ignoreCase = true) }?.value ?: 0
    }
    fun addReporte(reporte: pagosData) {
        _pagos.value?.add(reporte)
        _pagos.postValue(_pagos.value)
    }
}

private fun <E> List<E>?.add(reporte: E) {
    TODO("Not yet implemented")
}
