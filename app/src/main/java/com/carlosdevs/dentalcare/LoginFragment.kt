package com.carlosdevs.dentalcare

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.carlosdevs.dentalcare.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class LoginFragment : Fragment(R.layout.fragment_login) {

    // Instancia de Firebase Firestore para acceder a la base de datos
    private val db = FirebaseFirestore.getInstance()

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private val TAG = "LoginFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()

        binding.loginFragmentSignupButton.setOnClickListener { loginUser() }

        // Configurar el TextView para ir al FragmentSignup
        val textSignup = view.findViewById<TextView>(R.id.login_fragment_text_signup)
        textSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        // Configurar el TextView para ir al FragmentForget
        val textForget = view.findViewById<TextView>(R.id.login_fragment_label_text_password)
        textForget.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgetFragment)
        }
        return view
    }

    // Función que permite al usuario iniciar sesión
    private fun loginUser() {

        val email = binding.loginFragmentEmail.text.toString()
        val password = binding.loginFragmentPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Guardar el estado de la sesión
                        val sharePref = requireActivity().getSharedPreferences("DentalCare", Context.MODE_PRIVATE)
                        with(sharePref.edit()) {
                            Toast.makeText(requireContext(), "Bienvenido a DentalCare", Toast.LENGTH_SHORT).show()
                            putBoolean("Sesión iniciada", true)
                            apply()
                        }

                        // Guardar o actualizar los datos del usuario
                        saveUserData(user)

                        // Inicio de sesión exitoso, navegar al HomeActivity.
                        findNavController().navigate(R.id.action_loginFragment_to_homeActivity)
                    }
                } else {
                    // Error al iniciar sesión.
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        // Correo electrónico no valido.
                        Toast.makeText(requireContext(), "El Correo Electrónico o la Contraseña es Incorrecta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    // Función que permite traer los datos del usuario desde Firebase
    private fun saveUserData(user: FirebaseUser) {
        // Variable del id
        val userId = user.uid
        // Variable de referencia
        val userDocRef = db.collection("users").document(userId)

        // Obtener los datos del usuario si ya existen
        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Datos existentes del usuario
                val existingData = document.toObject(userData::class.java)
                val updatedData = userData(
                    userId = userId,
                    name = existingData?.name ?: "",
                    number = existingData?.number ?: "",
                    gender = existingData?.gender ?: ""
                )

                // Guardar los datos actualizados
                userDocRef.set(updatedData)
                    .addOnSuccessListener {
                        Log.d(TAG, "Datos de usuario guardados correctamente")
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "Error al guardar datos", e)
                    }
            } else {
                // Si no existen datos, crear uno nuevo
                val newData = userData(
                    userId = userId,
                    name = "",
                    number = "",
                    gender = ""
                )
                userDocRef.set(newData)
                    .addOnSuccessListener {
                        Log.d(TAG, "Datos de usuario guardados correctamente")
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "Error al guardar datos", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.d(TAG, "Error al obtener datos del usuario", e)
        }
    }
}