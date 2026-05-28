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

    //(Firebase, 2026)
    //firebase instance to get current user
    val uid = MainActivity.getUserID()

    //firebase instance to save fingerprint toggle state
    private val database = FirebaseDatabase.getInstance().getReference("users") .child(uid).child("fingerprintActive")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSetPin = view.findViewById<CardView>(R.id.setPinCard)
        val switchFingerprint = view.findViewById<Switch>(R.id.fingerprintSwitch)

        //(Firebase, 2026b)
        database.get().addOnSuccessListener { snapshot ->
            switchFingerprint.isChecked = snapshot.getValue(Boolean::class.java) ?: false}

        //(Code with Adnan, 2022)
        //if switch is turned on then fingerprint biometric will be activated
        switchFingerprint.setOnCheckedChangeListener {_, isChecked ->
            database.setValue(isChecked)
            if(isChecked){
                Toast.makeText(requireContext(),"Fingerprint unlock enabled", Toast.LENGTH_SHORT).show()
            }
        }
        //navigates to screen to create 4 digit pin
        btnSetPin.setOnClickListener {
            findNavController().navigate(R.id.action_AppSecurityFragment_to_CreatePinFragment)
        }
    }

}
/* References

Code with Adnan. 2022. Create Switch/Toggle Button in Android Studio Tutorial 2022. (version 8) [source code]. Available at: <https://youtu.be/ZI5WC5NPf2o?si=Tr2N0iFisGBk6Uw3> [Accessed 27 May 2026]
Firebase. 2026. Read and Write Data on Android. (version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/read-and-write > [Accessed 26 April 2026]
Firebase. 2026b. Work with Lists of Data on Android.(Version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/lists-of-data#child-events > [Accessed 26 April 2026]


 */