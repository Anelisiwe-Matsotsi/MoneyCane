package com.example.prog7313_sem1_part2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ProfileLight :  Fragment(R.layout.profile_view) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val BtnBack = view.findViewById<ImageButton>(R.id.iv_back_arrow)

        val btnLogout = view.findViewById<Button>(R.id.mbtn_logout)
        val tvFullName = view.findViewById<TextView>(R.id.tvFullName)
        val tvUsername = view.findViewById<TextView>(R.id.tvUsername)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {

            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)

            // Perform a one-time read using the modern get() method (Firebase. 2026.)
            userRef.get().addOnSuccessListener { snapshot ->

                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    tvUsername.text = user.username
                    tvFullName.text = "${user.name} ${user.surname}".trim()
                    tvEmail.text = user.email
                } else {
                    Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->

                Toast.makeText(context, "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }


            BtnBack.setOnClickListener {

                findNavController().navigateUp()

            }
            btnLogout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()//to transaction(light)
                findNavController().navigate(R.id.action_profileLight_to_logFragment)

            }


        }
    }
}
//Firebase. 2026. Read and Write Data on Android (version 8) [source code]. Available at:https://firebase.google.com/docs/database/android/read-and-write?%3Bauthuser=4&authuser=4&hl=en
//btn_catagory
// btn_transaction
//btn_wallet