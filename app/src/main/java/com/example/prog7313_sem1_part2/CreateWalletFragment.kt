package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase

class CreateWalletFragment : Fragment() {

    // Firebase Realtime Database reference
    private val database = FirebaseDatabase.getInstance().getReference("wallets")
    private val uid = MainActivity.getUserID()

    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_wallet, container, false)

        val editWalletName = view.findViewById<EditText>(R.id.editWalletName)
        val editWalletBalance = view.findViewById<EditText>(R.id.editWalletBalance)
        val btnAddWallet = view.findViewById<Button>(R.id.btnAddWallet)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

//        val radioGroupType = view.findViewById<RadioGroup>(R.id.radioGroupType)
//        val radioExpense = view.findViewById<RadioButton>(R.id.radioExpense)
//        val radioIncome = view.findViewById<RadioButton>(R.id.radioIncome)


        //(Android Developers, 2026)
        // Navigate back to the categories list
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        btnAddWallet.setOnClickListener {
            val name = editWalletName.text.toString().trim()
            val balanceText = editWalletBalance.text.toString().trim()

            //(Bak, 2023)
            // Input validation- if edit text is empty then prompt user again
            if (name.isEmpty()) {
                editWalletName.error = "Please enter a wallet name"
                return@setOnClickListener
            }
            if (balanceText.isEmpty()) {
                editWalletBalance.error = "Please enter an initial balance"
                return@setOnClickListener
            }

            val limit = balanceText.toDoubleOrNull()
            if (limit == null || limit <= 0) {
                editWalletBalance.error = "Please enter a valid amount"
                return@setOnClickListener
            }

//            // Determine selected type from the RadioGroup
//            //(StackOverflow, 2023)
//            val selectedType = when (radioGroupType.checkedRadioButtonId) {
//                R.id.radioIncome -> "income"
//                else -> "expense"
//            }

            saveWallet(name, limit)
        }

        return view
    }

    //Firebase, 2026)
    //wallet is saved in firebase DB
    private fun saveWallet(name: String, limit: Double) {
        // push creates a unique key for the new wallet

        val walletRef = database.child(uid).push()//saves the data under the user ID, as a child
        val walletId = walletRef.key ?: return


        val newWallet = Wallet(
            id = walletId,
            name = name,
            balance = limit
        )

        walletRef.setValue(newWallet)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "$name wallet created!", Toast.LENGTH_SHORT).show()
                // Return to the wallet list
                //(Android Developers, 2026)
                findNavController().popBackStack()
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Failed to save: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

/*
References

Android Developers. 2026. Fragment manager | App architecture. (version 8) [source code]. Available at: < https://developer.android.com/guide/fragments/fragmentmanager > [Accessed 26 April 2026]
Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Bak, K. 2023. Validating user input in kotlin: a guide to adding validation to a new transaction. (version 8) [source code]. Available at: < https://53jk1.medium.com/validating-user-input-in-kotlin-a-guide-to-adding-validation-to-a-new-transaction-2e486f176674 > [Accessed 26 April 2026]
Firebase. 2026. Read and Write Data on Android. (version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/read-and-write > [Accessed 26 April 2026]
StackOverflow. 2023. Set Variable depending on RadioGroup button selected in Kotlin. (Version 8) [source code]. Available at: < https://stackoverflow.com/questions/77541256/set-variable-depending-on-radiogroup-button-selected-in-kotlin > [Accessed 26 April 2026]
 */