package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class SplashFragment : Fragment(R.layout.fragment_splash) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        // handler creates a delay of 2 seconds before moving to the next screen
        val handler= Handler(Looper.getMainLooper())

        Handler(Looper.getMainLooper()).postDelayed({

            val user = FirebaseAuth.getInstance().currentUser

            if(user != null){
                //check if the user set a passcode
                FirebaseDatabase.getInstance().reference.child("users").child(user.uid).child("passcode")
                    .get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            findNavController().navigate(R.id.action_splashFragment_to_AuthenticationFragment)
                        } else {
                            findNavController().navigate(R.id.action_splashFragment_to_home)
                        }
                    }
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_logFragment)
            }
        }, 2500)
    }

}