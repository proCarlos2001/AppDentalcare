package com.carlosdevs.dentalcare

import android.content.Context
import android.os.Bundle
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


class LoginFragment : Fragment(R.layout.fragment_login) {

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

                    // Guardar el estado de la sesión
                    val sharePref = requireActivity().getSharedPreferences("DentalCare", Context.MODE_PRIVATE)
                    with(sharePref.edit()){
                        putBoolean("Sesión iniciada", true)
                        apply()
                    }
                    // Inicio de sesión exitoso, navegar al HomeActivity.
                    findNavController().navigate(R.id.action_loginFragment_to_homeActivity)
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
}