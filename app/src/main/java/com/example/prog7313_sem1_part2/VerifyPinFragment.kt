package com.example.prog7313_sem1_part2

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class VerifyPinFragment( private val onResult: (Boolean) ->
Unit) : DialogFragment(){

    //firebase instance to get current user
    val uid = MainActivity.getUserID()

    //firebase instance to save fingerprint toggle state
    private val database = FirebaseDatabase.getInstance().getReference("users") .child(uid).child("passcode")

    private var enteredPin = ""
    private lateinit var tvPin: TextView //global access

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.pin_dialog, null)

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

        //store the enteredPin according to number buttons clicked
        numberButtons.forEachIndexed { number, id ->
            view.findViewById<Button>(id).setOnClickListener {
                if (enteredPin.length < 4) //enteredPin limit is 4 digits
                {
                    enteredPin += number.toString()
                    tvPin.text = enteredPin
                }
            }
        }

        //clear code button clears all digits user has entered- they should start from over
        view.findViewById<Button>(R.id.clearCode).setOnClickListener {
            enteredPin = ""
            tvPin.text = "- - - -"
        }

        btnConfirm.setOnClickListener {
            //error handling if user does not enter 4 digits
            if (enteredPin.length < 4) {
                Toast.makeText(requireContext(), "Enter 4 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verifyPin(enteredPin)
        }

        return MaterialAlertDialogBuilder(requireContext()).setView(view).create()

    }

    //hash the enteredPin so it is encrypted to send to firebase
    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") {"%02x".format(it)  }
    }

    //check if entered pin matches hashed stored Pin in firebase DB
    private fun verifyPin(pin: String) {
        // push creates a unique key for the new enteredPin

        val hashedPin = hashPin(pin)

            database.get().addOnSuccessListener { snapshot ->
                val stored = snapshot.getValue(String::class.java)

                if (stored == hashedPin) {
                    Toast.makeText(requireContext(), "MoneyCane to the rescue!", Toast.LENGTH_SHORT)
                        .show()
                    dismiss()
                    onResult(true)
                } else {
                    Toast.makeText(requireContext(), "INCORRECT PIN", Toast.LENGTH_SHORT).show()
                    enteredPin = ""
                    tvPin.text = ""
                    onResult(false)
                }
            }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),"Error Occurred: Could not verify PIN", Toast.LENGTH_SHORT).show()
                }
    }}
