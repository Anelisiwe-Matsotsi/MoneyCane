package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.view.View
import android.widget.Button
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

    private fun showBiometric() {
        val biometricManager = BiometricManager.from(requireContext())
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            != BiometricManager.BIOMETRIC_SUCCESS)
        {
            Toast.makeText(requireContext(), "Biometric not available, use passcode", Toast.LENGTH_SHORT).show()
            return
        }

        val executor = ContextCompat.getMainExecutor(requireContext())

        val biometricPrompt = BiometricPrompt(this,executor,
            object: BiometricPrompt.AuthenticationCallback(){

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    /*We will show an error message through a toast message*/
                    Toast.makeText(requireContext(), "An error:" + errString, Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    /*We will show an success message through a toast and naviagte to te next activity*/
                    Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }

                override fun onAuthenticationFailed() { //when it fails user can try again or choose the try passcode instead button
                    super.onAuthenticationFailed()
                    //show authentication failed through toast message
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for MoneyCane")
            .setSubtitle("Touch your fingerprint sensor")
            .setNegativeButtonText("Use Passcode instead")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showPasscode() {
        val promptPinFragment = VerifyPinFragment { success ->
            if (success)
                navigateToHome()
        }
            promptPinFragment.show(parentFragmentManager, "verifyPin")

    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_AuthenticationFragment_to_home)
    }

}
