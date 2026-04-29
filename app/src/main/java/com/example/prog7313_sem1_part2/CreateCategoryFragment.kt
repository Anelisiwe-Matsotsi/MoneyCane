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

class CreateCategoryFragment : Fragment() {

    private val uid = MainActivity.getUserID()
    // Firebase Realtime Database reference

    private val database = FirebaseDatabase.getInstance().getReference("categories")

    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_category, container, false)

        val editCategoryName = view.findViewById<EditText>(R.id.editCategoryName)
        val editCategoryLimit = view.findViewById<EditText>(R.id.editCategoryLimit)
        val radioGroupType = view.findViewById<RadioGroup>(R.id.radioGroupType)
        val radioExpense = view.findViewById<RadioButton>(R.id.radioExpense)
        val radioIncome = view.findViewById<RadioButton>(R.id.radioIncome)
        val btnAddCategory = view.findViewById<Button>(R.id.btnAddCategory)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        //(Android Developers, 2026)
        // Navigate back to the categories list
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        btnAddCategory.setOnClickListener {
            val name = editCategoryName.text.toString().trim()
            val limitText = editCategoryLimit.text.toString().trim()

            //(Bak, 2023)
            // Input validation- if edit text is empty then prompt user again
            if (name.isEmpty()) {
                editCategoryName.error = "Please enter a category name"
                return@setOnClickListener
            }
            if (limitText.isEmpty()) {
                editCategoryLimit.error = "Please enter a monthly limit"
                return@setOnClickListener
            }

            val limit = limitText.toDoubleOrNull()
            if (limit == null || limit <= 0) {
                editCategoryLimit.error = "Please enter a valid amount"
                return@setOnClickListener
            }

            // Determine selected type from the RadioGroup
            //(StackOverflow, 2023)
            val selectedType = when (radioGroupType.checkedRadioButtonId) {
                R.id.radioIncome -> "income"
                else -> "expense"
            }

            saveCategory(name, limit, selectedType)
        }

        return view
    }

    //Firebase, 2026)
    //category is saved in firebase DB
    private fun saveCategory(name: String, limit: Double, type: String) {
        // push creates a unique key for the new category

        val categoryRef = database.child(uid).push()
        val categoryId = categoryRef.key ?: return

        val newCategory = Category(
            id = categoryId,
            name = name,
            limit = limit,
            type = type,
            spent = 0.0
        )

        categoryRef.setValue(newCategory)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "$name category created!", Toast.LENGTH_SHORT).show()
                // Return to the categories list
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