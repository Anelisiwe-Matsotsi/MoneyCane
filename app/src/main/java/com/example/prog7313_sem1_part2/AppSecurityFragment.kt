package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase

class AppSecurityFragment : Fragment(R.layout.app_security){

    //firebase instance to get current user
    val uid = MainActivity.getUserID()

    //firebase instance to save fingerprint toggle state
    private val database = FirebaseDatabase.getInstance().getReference("users") .child(uid).child("fingerprintActive")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSetPin = view.findViewById<CardView>(R.id.setPinCard)
        val switchFingerprint = view.findViewById<Switch>(R.id.fingerprintSwitch)

        database.get().addOnSuccessListener { snapshot ->
            switchFingerprint.isChecked = snapshot.getValue(Boolean::class.java) ?: false}

        switchFingerprint.setOnCheckedChangeListener {_, isChecked ->
            database.setValue(isChecked)
            if(isChecked){
                Toast.makeText(requireContext(),"Fingerprint unlock enabled", Toast.LENGTH_SHORT).show()
            }
        }
        btnSetPin.setOnClickListener {
            findNavController().navigate(R.id.action_AppSecurityFragment_to_CreatePinFragment)
        }
    }

}