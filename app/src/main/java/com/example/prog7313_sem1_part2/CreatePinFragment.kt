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

    //initialize 2 pin variables to be entered- pins must match
    private var pin = ""
    private var confirmPin = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pinPrompt = view.findViewById<TextView>(R.id.tvPrompt)
        val tvPin = view.findViewById<TextView>(R.id.tvPinArea)
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

        //store the pin according to number buttons clicked
        numberButtons.forEachIndexed { number, id -> //(GitBook, 2019)
            view.findViewById<Button>(id).setOnClickListener {
                //(O'Didily, 2023)
                if (pin.length < 4) //pin limit is 4 digits
                {
                    pin += number.toString() //changed PIN to string so it can be hashed

                    //(StackOverflow, 2022)
                    tvPin.text = "*".repeat(pin.length) //PIN entered appears in stars and not actual numbers
                }
            }
        }

        //clear code button clears all digits user has entered- they should start from over
        view.findViewById<Button>(R.id.clearCode).setOnClickListener {
            pin = ""
            tvPin.text = "- - - -"
        }

        //(O'Didily, 2023)
        btnConfirm.setOnClickListener {
            //error handling if user does not enter 4 digits
            if (pin.length != 4) {
                //(StackOverflow, 2021)
                Toast.makeText(requireContext(), "Enter 4 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //(O'Didily, 2023)
            //user is prompted for pin then needs to confirm pin the second time
            if (confirmPin.isEmpty()) {
                confirmPin = pin
                //initialized again so that user can confirm to check if PINs match
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

                    //initialise pin variables for user to start over
                    pin = ""
                    confirmPin = ""
                    tvPin.text = "- - - -"
                    pinPrompt.text = "Create PIN"

                }
            }
        }
    }

    //hash the enteredPin so it is encrypted to send to firebase
    //(SSOJet, 2025)
    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val encodedHash = digest.digest(pin.toByteArray())
        return encodedHash.fold("") { str, byte -> str + "%02x".format(byte) }
    }

    //(Firebase, 2026)
    //save hashed PIN to firebase DB
    private fun savePin(pin: String) {

        val hashedPin = hashPin(pin)

        database.setValue(hashedPin)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "PIN created successfully!", Toast.LENGTH_SHORT).show()

                val resetPin = arguments?.getBoolean("resetPin", false) ?: false

                //(Android Developers, 2026b)
                //if the user resets their pin then they will be redirected to the home screen
                if(resetPin){
                    findNavController().navigate(R.id.action_CreatePinFragment_to_home)
                }
                else {
                    // Go to previous page-app security screen
                    findNavController().popBackStack()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save PIN", Toast.LENGTH_SHORT).show()
            }
    }
}

/* References

Android Developers. 2026. Dialogs. (version 8) [Source Code]. Available at: <https://developer.android.com/develop/ui/views/components/dialogs> [Accessed 27 May 2026]
Android Developers. 2026b. Fragment manager | App architecture. (version 8) [source code]. Available at: < https://developer.android.com/guide/fragments/fragmentmanager > [Accessed 26 April 2026]
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