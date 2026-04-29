package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth


class RegisterFragmentLight : Fragment(R.layout.fragment_register_light) {
    private lateinit var rootNode : FirebaseAuth//geeks for geeks. how to call the authentication


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = view.findViewById<ImageButton>(R.id.ib_Back)
        val save = view.findViewById<Button>(R.id.btnSignUp)
        val email = view.findViewById<EditText>(R.id.etEmail)

        val password = view.findViewById<EditText>(R.id.etPassword)
        val confirm_password = view.findViewById<EditText>(R.id.etConfirmPassword)

        rootNode = FirebaseAuth.getInstance()


        save.setOnClickListener {

            val Email = email.text.toString()
            val Password = password.text.toString()
            val Confirm_password = confirm_password.text.toString()

            if (Email.isEmpty() || Password.isEmpty()) {
                Toast.makeText(context, "please enter credentials", Toast.LENGTH_LONG).show()
            } else if (Password != Confirm_password) {
                Toast.makeText(
                    context,"Passwords do not match, please try again",Toast.LENGTH_LONG
                ).show()
            } else {
                // create new user or register new user
                rootNode.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()

                        findNavController().navigateUp()
                    } else {
                        // Registration failed
                        Toast.makeText(context,"Registration failed!!" + " Please try again later",Toast.LENGTH_LONG).show()
                    }
                }


                //findNavController().navigate(R.id.action_home2_to_createTransactionFragment2)//to transaction(light)
            }
            back.setOnClickListener {
                findNavController().navigateUp()//to transaction(light)
            }


        }
    }
}