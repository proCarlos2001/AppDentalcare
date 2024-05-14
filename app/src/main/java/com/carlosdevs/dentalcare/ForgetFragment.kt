package com.carlosdevs.dentalcare

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.carlosdevs.dentalcare.databinding.FragmentForgetBinding
import com.google.firebase.auth.FirebaseAuth


class ForgetFragment : Fragment(R.layout.fragment_forget) {

    private val TAG = "ForgetFragment"
    private lateinit var binding: FragmentForgetBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()

        binding.forgetFragmentSignupButton.setOnClickListener { sendPasswordResetEmail() }

        // Configurar el TextView para ir al FragmentSignup
        val textSignup = view.findViewById<TextView>(R.id.forget_fragment_text_signup)
        textSignup.setOnClickListener {
            findNavController().navigate(R.id.action_forgetFragment_to_signUpFragment)
        }
        return view
    }

    private fun sendPasswordResetEmail() {

        val email = binding.forgetFragmentEmail.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Se ha enviado un correo electrónico para reestablecer tu contraseña", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_forgetFragment_to_loginFragment)
                } else {
                    Toast.makeText(requireContext(), "Error al enviar el correo electrónico de restablecimiento de contraseña. Por favor intenta de nuevo", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error al enviar el correo electrónico de restablecimiento de contraseña", task.exception)
                }
            }
    }
}