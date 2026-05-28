package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase

class AuthenticationFragment : Fragment(R.layout.prompt_security) {

    //firebase instance to get current user
    val uid = MainActivity.getUserID()

    //firebase instance to save fingerprint toggle state
    private val database = FirebaseDatabase.getInstance().getReference("users") .child(uid).child("passcode")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPasscode = view.findViewById<Button>(R.id.btnPIN)
        val tvForgotPasscode = view.findViewById<TextView>(R.id.tvForgotPasscode)

        //if its a new user then go to login page
        if (uid.isEmpty())
        {
            findNavController().navigate(R.id.action_AuthenticationFragment_to_logFragment)
            return
        }

        //check if the user activated fingerprint biometric auth toggle
        checkFingerprint(uid) { activeFingerprint ->
            if (activeFingerprint) {
                showBiometric()
            }
        }
        //Passcode prompt is a fallback if biometric does not work
        btnPasscode.setOnClickListener {
            showPasscode()
        }

        //user can edit passcode when they click the forgot passcode
        tvForgotPasscode.setOnClickListener {
            // user goes to the login screen to verify their email and password to change their passcode
            // (StackOverflow, 2021)
            val args = Bundle()
            args.putBoolean("newPin", true) //user wants to create a new pin therefore true
            findNavController().navigate(R.id.action_AuthenticationFragment_to_logFragment)
        }
    }

    private fun checkFingerprint(uid: String, check: (Boolean) -> Unit) {
        FirebaseDatabase.getInstance().reference.child("users").child(uid)
            .child("fingerprintActive")
            .get().addOnSuccessListener { snapshot ->
                check(snapshot.getValue(Boolean::class.java) ?: false)
            }
            .addOnFailureListener {
                check(false)
            }
    }

    //(Mullatoez, 2021)
    private fun showBiometric() {
        //biometric manager controls the scanning of biometrics
        val biometricManager = BiometricManager.from(requireContext())
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            != BiometricManager.BIOMETRIC_SUCCESS)
        { //usign passocde as fallback
            Toast.makeText(requireContext(), "Biometric not available, use passcode", Toast.LENGTH_SHORT).show()
            return
        }

        //(Kabaria, 2022)
        //running on main thread UI
        val executor = ContextCompat.getMainExecutor(requireContext())

        val biometricPrompt = BiometricPrompt(this,executor,
            object: BiometricPrompt.AuthenticationCallback(){

                //biometric prompt dialog closes and user must click the use passcode instead button in the welcome screen
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    /*We will show an error message through a toast message*/
                    Toast.makeText(requireContext(), "Click the enter passcode button", Toast.LENGTH_SHORT).show()
                }

                //if biometric or pin is correct then user successfully goes to MoneyCane Home page
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    /*We will show an success message through a toast and naviagte to te next activity*/
                    Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }

                //user must try again-gets kicked out of app
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    //show authentication failed through toast message
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        //prompt info is displayed on OS built in biometric dialog
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for MoneyCane")
            .setSubtitle("Touch your fingerprint sensor")
            .setNegativeButtonText("Use Passcode instead")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    //passcode must be verified
    private fun showPasscode() {
        val promptPinFragment = VerifyPinFragment { success ->
            if (success)
                navigateToHome()
        }
        //prompt screen pops up like a dialog
            promptPinFragment.show(parentFragmentManager, "verifyPin")

    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_AuthenticationFragment_to_home)
    }

}
/* References

Kabaria, M. 2022. SSBiometricsAuthentication. (version 8) [Source Code]. Available at: <https://github.com/SimformSolutionsPvtLtd/SSBiometricsAuthentication/tree/main> [Accessed 27 May 2026]
Mullatoez. 2021. Android Fingerprint Authentication in Kotlin | Biometric Authentication | Android Studio Tutorial. (version 8) [Source Code]. Available at: <https://youtu.be/sU9p6Pt6I2k?si=0cR9e-3ETh-qYew0> [Accessed 27 May 2026]
StackOverflow. 2021. How do i pass an argument to the view model?. (version 8) [Source Code]. Available at: <https://stackoverflow.com/questions/68191852/how-do-i-pass-an-argument-to-the-view-model> [Accessed 27 May 2026]

*/
