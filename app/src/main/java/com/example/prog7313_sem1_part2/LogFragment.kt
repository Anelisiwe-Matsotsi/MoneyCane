package com.example.prog7313_sem1_part2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.toString

class LogFragment : Fragment(R.layout.fragment_log) {

    private lateinit var rootNode : FirebaseAuth//geeks for geeks. how to call the authentication



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //for later use in the login
        //val sharedPref = requireActivity().getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)
        val btnLogin =view.findViewById<Button>(R.id.btnLogin)
        val Email = view.findViewById<EditText>(R.id.tbUsername)
        val Password = view.findViewById<EditText>(R.id.tbPassword)
        val Register = view.findViewById<Button>(R.id.btnRegister)

        rootNode = FirebaseAuth.getInstance()


        btnLogin.setOnClickListener{
            val email = Email.text.toString()
            val password = Password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "please enter credentials", Toast.LENGTH_LONG).show()
            } else {
                // login existing user
                rootNode.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // login successful
                        Toast.makeText(context, "Login successful!!", Toast.LENGTH_LONG).show()

                        //redirect to the home screen
                        findNavController().navigate(R.id.action_logFragment_to_home)
                    } else {
                        // login failed
                        Toast.makeText(context, "Login failed!!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        Register.setOnClickListener {
            findNavController().navigate(R.id.action_logFragment_to_registerFragmentLight)
            //navigates to the register fragment
        }
    }


}







//https://www.geeksforgeeks.org/android/user-authentication-using-firebase-in-android/