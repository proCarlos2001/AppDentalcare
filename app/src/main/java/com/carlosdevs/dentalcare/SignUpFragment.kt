package com.carlosdevs.dentalcare

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.carlosdevs.dentalcare.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private val TAG = "SignUpFragment"
    private lateinit var binding: FragmentSignupBinding
    private var gender : String = ""
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()

        binding.apply {
            binding.signupFragmentRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                gender = view.findViewById<RadioButton>(checkedId).text.toString()
                Log.d("TT", gender)
            }
            // Se crea el usuario si todas las validaciones son correctas.
            binding.signupFragmentSignupButton.setOnClickListener { createUser() }
        }
        // Configurar el TextView para ir al FragmentLogin.
        val textLogin = view.findViewById<TextView>(R.id.signup_fragment_text_login)
        textLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
        // Configurar el botón de retroceso para salir de la app.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
        return view
    }

    private fun createUser() {
        val email = binding.signupFragmentEmail.text.toString()
        val password = binding.signupFragmentPassword.text.toString()

        // Validar campos de email y contraseña...
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val uid = user?.uid

                    val name = binding.signupFragmentName.text.toString()
                    val number = binding.signupFragmentNumber.text.toString()
                    val gender = this.gender

                    val userData = hashMapOf(
                        "name" to name,
                        "number" to number,
                        "gender" to gender
                    )

                    val db = FirebaseFirestore.getInstance()
                    if (uid != null) {
                        db.collection("users").document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Log.d(TAG, "Error al guardar datos adicionales del usuario en Firestore")
                                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error al guardar datos adicionales del usuario en Firestore", e)
                                Toast.makeText(requireContext(), "Error al registrar usuario. Por favor intenta de nuevo más tarde", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Log.w(TAG, "Error al registrar usuario", task.exception)
                    Toast.makeText(requireContext(), "Error al registrar usuario. Por favor intenta de nuevo más tarde", Toast.LENGTH_SHORT).show()
                }
            }
    }
}