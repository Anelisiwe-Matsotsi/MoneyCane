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
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LightEditWalletFragment : Fragment() {

    //firebase instance
    private val database = FirebaseDatabase.getInstance().getReference("wallets")

    // Wallet ID passed from LightWalletFragment
    private var walletId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        walletId = arguments?.getString("walletId")
    }

    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet_edit_light, container, false)

        val editWalletName = view.findViewById<EditText>(R.id.editWalletName)
        val editWalletBalance = view.findViewById<EditText>(R.id.editWalletBalance)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

//        val radioGroupType = view.findViewById<RadioGroup>(R.id.radioGroupType)
//        val radioExpense = view.findViewById<RadioButton>(R.id.radioExpense)
//        val radioIncome = view.findViewById<RadioButton>(R.id.radioIncome)


        //(Android Developers, 2026)
        // Navigate back to the categories list
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Load the existing wallet values from Firebase and pre-fill the form
        loadWallet(walletId) { wallet ->
            editWalletName.setText(wallet.name)
            editWalletBalance.setText(wallet.balance.toInt().toString())

//            // Pre-select the correct radio button based on the saved type
//            if (category.type == "income") {
//                radioIncome.isChecked = true
//            } else {
//                radioExpense.isChecked = true
//            }
        }

        // SAVE: update the editable fields without touching "spent"
        btnSave.setOnClickListener {
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

            updateWallet(name, limit)
        }

        return view
    }

    private fun loadWallet(id: String?, onLoaded: (Wallet) -> Unit) {
        if (id == null) return

        //(Firebase, 2026b)
        //fetched correct wallet unique id to be edited/updated
        database.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val wallet = snapshot.getValue(Wallet::class.java)
                if (wallet != null) {
                    onLoaded(wallet)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load wallet", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateWallet(name: String, limit: Double) {
        val id = walletId ?: return

        val updates = mapOf<String, Any>(
            "name" to name,
            "balance" to limit
        )

        database.child(id).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Wallet updated!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Update failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    //(Android Knowledge, 2023)
    private fun deleteWallet() {
        val id = walletId ?: return

        database.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Delete failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    //(Bhosale, 2024)
    companion object {
        @JvmStatic
        fun newInstance(walletId: String) = LightEditWalletFragment().apply {
            arguments = Bundle().apply {
                putString("walletId", walletId)
            }
        }
    }
}

/*
References

Android Developers. 2026. Fragment manager | App architecture. (version 8) [source code]. Available at: < https://developer.android.com/guide/fragments/fragmentmanager > [Accessed 26 April 2026]
Android Developers. 2026b. fragments transactions. (version 8) [source code]. Available at: < https://developer.android.com/guide/fragments/transactions > [Accessed 26 April 2026]
Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Bak, K. 2023. Validating user input in kotlin: a guide to adding validation to a new transaction. (version 8) [source code]. Available at: < https://53jk1.medium.com/validating-user-input-in-kotlin-a-guide-to-adding-validation-to-a-new-transaction-2e486f176674 > [Accessed 26 April 2026]
Bhosale, A. 2024. Kotlin under the hood: exploring objects, companion objects, and annotations: @JvmStatic, @JvmField, @JvmOverloads. (version 8) [source code]. Available at: < https://proandroiddev.com/kotlin-under-the-hood-exploring-objects-companion-objects-and-annotations-jvmstatic-71e1bf1128df > [Accessed 26 April 2026]
Firebase. 2026. Read and Write Data on Android. (version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/read-and-write > [Accessed 26 April 2026]
Firebase. 2026b. Work with Lists of Data on Android.(Version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/lists-of-data#child-events > [Accessed 26 April 2026]
StackOverflow. 2023. Set Variable depending on RadioGroup button selected in Kotlin. (Version 8) [source code]. Available at: < https://stackoverflow.com/questions/77541256/set-variable-depending-on-radiogroup-button-selected-in-kotlin > [Accessed 26 April 2026]
 */