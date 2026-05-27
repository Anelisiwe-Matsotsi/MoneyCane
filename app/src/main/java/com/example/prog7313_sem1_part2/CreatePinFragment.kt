package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class CreatePinFragment : Fragment(R.layout.create_pin){

    //firebase instance to get current user
    val uid = MainActivity.getUserID()

    //firebase instance to save fingerprint toggle state
    private val database = FirebaseDatabase.getInstance().getReference("users") .child(uid).child("passcode")

    private var pin = ""
    private var confirmPin = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pinPrompt = view.findViewById<TextView>(R.id.tvPrompt)
        val tvPin = view.findViewById<TextView>(R.id.tvPinArea)
        val btnConfirm = view.findViewById<Button>(R.id.confirmButton)

        //list number pad numbers into an array
        val numberButtons = listOf(
            R.id.num0,
            R.id.num1,
            R.id.num2,
            R.id.num3,
            R.id.num4,
            R.id.num5,
            R.id.num6,
            R.id.num7,
            R.id.num8,
            R.id.num9,
        )

        //store the pin according to number buttons clicked
        numberButtons.forEachIndexed { number, id ->
            view.findViewById<Button>(id).setOnClickListener {
                if (pin.length < 4) //pin limit is 4 digits
                {
                    pin += number.toString()
                    tvPin.text = pin
                }
            }
        }

        //clear code button clears all digits user has entered- they should start from over
        view.findViewById<Button>(R.id.clearCode).setOnClickListener {
            pin = ""
            tvPin.text = "- - - -"
        }

        btnConfirm.setOnClickListener {
            //error handling if user does not enter 4 digits
            if (pin.length != 4) {
                Toast.makeText(requireContext(), "Enter 4 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //user is prompted for pin then needs to confirm pin the second time
            if (confirmPin.isEmpty()) {
                confirmPin = pin
                pin = ""

                tvPin.text = "- - - -"
                pinPrompt.text = "Confirm PIN"

                Toast.makeText(requireContext(), "Re-eneter your PIN please", Toast.LENGTH_SHORT).show()
            }
            //error handling if PINs do not match with the second prompt for confirmation
            else {
                if (pin == confirmPin) {
                    savePin(pin) //save the pin to firebase if correctly matching
                } else {
                    Toast.makeText(requireContext(), "PINs DO NOT MATCH", Toast.LENGTH_SHORT).show()

                    //intialize pin variables for user to start over
                    pin = ""
                    confirmPin = ""
                    tvPin.text = "- - - -"
                    pinPrompt.text = "Create PIN"

                }
            }
        }
    }

    //hash the PIN so it is encrypted to send to firebase
    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") {"%02x".format(it)  }
    }

    //save hashed PIN to firebase DB
    private fun savePin(pin: String) {
        // push creates a unique key for the new Pin

        val hashedPin = hashPin(pin)

        database.setValue(hashedPin)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "PIN created successfully!", Toast.LENGTH_SHORT).show()
                // Go to MoneyCane Home screen
                //(Android Developers, 2026)
                findNavController().popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save PIN", Toast.LENGTH_SHORT).show()
            }
    }
}