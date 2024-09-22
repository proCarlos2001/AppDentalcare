package com.carlosdevs.dentalcare

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UserViewModel : ViewModel() {

    // Instancia de Firebase Firestore para acceder a la base de datos
    private val db = FirebaseFirestore.getInstance()

    // Instancia de Firebase Authentication para la autenticación de usuarios
    private val auth = FirebaseAuth.getInstance()

    // Instancia para acceder al storage de Firebase
    private val storage = FirebaseStorage.getInstance()

    // LiveData para manejar la lista de los usuarios
    private val _user = MutableLiveData<userData>()
    val user: LiveData<userData> = _user

    // Variable temporal para almacenar la URL de la imagen seleccionada
    private var localImageUri: String? = null

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val user = auth.currentUser
        if (user != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // Filtra las citas por el userId del usuario autenticado
                    val document = db.collection("users")
                        .document(user.uid)  // Filtra por el userId
                        .get()
                        .await()

                    val userData = document.toObject(userData::class.java)
                    _user.postValue(userData)
                } catch (e: Exception) {
                    _user.postValue(null)
                }
            }
        } else {
            _user.postValue(null)
        }
    }
    fun uploadImage(uri: Uri) {
        val user = auth.currentUser
        if (user != null) {
            // Almacenar la URL local temporalmente
            localImageUri = uri.toString()
            _user.value = _user.value?.copy(photoUrl = localImageUri!!)

            val storageRef = storage.reference.child("userImages/${user.uid}/${UUID.randomUUID()}")

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val uploadTask = storageRef.putFile(uri).await()
                    val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
                    db.collection("users").document(user.uid).update("photoUrl", downloadUrl).await()

                    // Limpiar la URL temporal y actualizar el LiveData con la nueva URL de Firebase
                    localImageUri = null
                    _user.postValue(_user.value?.copy(photoUrl = downloadUrl))
                } catch (e: Exception) {
                    Log.d("UserViewModel", "Error al cargar la imagen", e)
                }
            }
        }
    }
}