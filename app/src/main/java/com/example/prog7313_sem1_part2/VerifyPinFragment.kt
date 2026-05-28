package com.example.prog7313_sem1_part2

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

//created dialog fragment so that the biometric dialog can appear when user enters MoneyCane
// (Android Developers, 2026)
class VerifyPinFragment( private val onResult: (Boolean) ->
Unit) : DialogFragment(){

    //firebase instance to get current user
    val uid = MainActivity.getUserID()

    //firebase instance to save fingerprint toggle state
    private val database = FirebaseDatabase.getInstance().getReference("users") .child(uid).child("passcode")

    private var enteredPin = ""
    private lateinit var tvPin: TextView //global access

    //(IT Wala, 2023)
    //the prompt for the PIN will float over the welcome page using alert dialogfragment.
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.pin_dialog, null)

        tvPin = view.findViewById<TextView>(R.id.tvPinArea)
        val btnConfirm = view.findViewById<Button>(R.id.confirmButton)

        //list number pad numbers into an a single variable numberButtons like an array
        //(GeeksforGeeks, 2025)
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
        numberButtons.forEachIndexed { number, id -> //(GitBook, 2019)
            view.findViewById<Button>(id).setOnClickListener {

                //user inout validation to check the length of pin entered and that pin area has value
                //(O'Didily, 2023)
                if (enteredPin.length < 4) //enteredPin limit is 4 digits
                {
                    enteredPin += number.toString() //changed PIN to string so it can be hashed

                    //(StackOverflow, 2022)
                    tvPin.text = "*".repeat(enteredPin.length) //PIN entered appears in stars and not actual numbers
                }
            }
        }

        //clear code button clears all digits user has entered- they should start from over
        view.findViewById<Button>(R.id.clearCode).setOnClickListener {
            enteredPin = ""
            tvPin.text = "- - - -"
        }

        //(O'Didily, 2023)
        btnConfirm.setOnClickListener {
            //error handling if user does not enter 4 digits
            if (enteredPin.length < 4) {
                //(StackOverflow, 2021)
                Toast.makeText(requireContext(), "Enter 4 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verifyPin(enteredPin) //calls method that checks entered pin against stored pin
        }

        //return the dialog where user enters their pin
        //(Rout, 2019)
        return MaterialAlertDialogBuilder(requireContext()).setView(view).create()

    }

    //hash the enteredPin so it is encrypted to send to firebase
    //(SSOJet, 2025)
    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val encodedHash = digest.digest(pin.toByteArray())
        return encodedHash.fold("") { str, byte -> str + "%02x".format(byte) }
    }

    //check if entered pin matches hashed stored Pin in firebase DB
    private fun verifyPin(pin: String) {

        val hashedPin = hashPin(pin)

        //(Firebase, 2026b)
            database.get().addOnSuccessListener { snapshot ->
                val stored = snapshot.getValue(String::class.java)

                //(O'Didily, 2023)
                if (stored == hashedPin) {
                    Toast.makeText(requireContext(), "MoneyCane to the rescue!", Toast.LENGTH_SHORT).show()

                    //dismiss removes the dialog prompting for pin
                    //(StackOverflow, 2012)
                    dismiss()
                    onResult(true) //passing back a successful function
                } else {
                    Toast.makeText(requireContext(), "INCORRECT PIN", Toast.LENGTH_SHORT).show()
                    enteredPin = ""
                    tvPin.text = ""
                    onResult(false) //passing unsuccessful function for wrong PIN
                }
            }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),"Error Occurred: Could not verify PIN", Toast.LENGTH_SHORT).show()
                }
    }}
/* References

Android Developers. 2026. Dialogs. (version 8) [Source Code]. Available at: <https://developer.android.com/develop/ui/views/components/dialogs> [Accessed 27 May 2026]
Firebase. 2026. Read and Write Data on Android. (version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/read-and-write > [Accessed 26 April 2026]
Firebase. 2026b. Work with Lists of Data on Android.(Version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/lists-of-data#child-events > [Accessed 26 April 2026]
GeeksforGeeks. 2025. Kotlin list: listOf(). (version 8) [Source Code]. Available at:<https://www.geeksforgeeks.org/kotlin/kotlin-list-listof/> [Accessed 27 May 2027]
GitBook. 2019. forEachIndexed | kotlin - GitBook. (version 8) [Source Code]. Available at:<https://gold.gitbook.io/kotlin/collections/aggregate-operations/foreachindexed> [Accessed 27 May 2026]
IT Wala. 2023. Android- Custom alert dialog tutorial in kotlin. (version 8) [Source Code]. Available at:<https://youtu.be/R7UjCvSyEVY?si=fB4Z-DEalQNcgBOl> [Accessed 27 May 2026]
O'Didily, M. 2023. How to get user input and validate it using kotlin (simple). (version 8) [Source Code]. Available at:<https://youtu.be/CpdT0C-4dRU?si=aQ5OHn9jcQRCb_eY> [Accessed 27 May 2026]
Rout, N. 2019. Hands-on with Material Components for Android: Dialogs. (version 8) [Source Code]. Available at: <https://medium.com/over-engineering/hands-on-with-material-components-for-android-dialogs-75c6d726f83a> [Accessed 27 May 2026]
SSOJet. 2025. SHA-256 in kotlin. (version 8) [Source Code]. Available at: <https://ssojet.com/hashing/sha-256-in-kotlin> [Accessed 27 May 2026]
StackOverflow. 2022. Using keypress and the or function [duplicate]. (version 8) [Source Code]. Available at: <https://stackoverflow.com/questions/72714435/using-keypress-and-the-or-function> [Accessed 28 May 2026]
StackOverflow. 2021. Android Kotlin: what does return @onclicklistener does?. (version 8) [Source Code]. Available at: <https://stackoverflow.com/questions/68453505/android-kotlin-what-does-returnonclicklistener-does> [Accessed 27 May 2026]
StackOverflow. 2012. Hot to correctly dismiss a DialogFragment?. (version 8) [Source Code]. Available at: <https://stackoverflow.com/questions/11201022/how-to-correctly-dismiss-a-dialogfragment> [Accessed 27 May 2026]

 */