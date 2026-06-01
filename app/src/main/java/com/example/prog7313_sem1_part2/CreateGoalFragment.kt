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

class CreateGoalFragment : Fragment() {

    private val uid = MainActivity.getUserID()
    // Firebase Realtime Database reference

    private val database = FirebaseDatabase.getInstance().getReference("goals")

    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_goal, container, false)

        val etName = view.findViewById<EditText>(R.id.etGoalName)
        val etTarget = view.findViewById<EditText>(R.id.etTargetAmount)
        val  rgType = view.findViewById<RadioGroup>(R.id.rgGoalType)
        val btnSave = view.findViewById<Button>(R.id.btnSaveGoal)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        //(Android Developers, 2026)
        // Navigate back to the goals list
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val targetText = etTarget.text.toString().trim()

            //(Bak, 2023)
            // Input validation- if edit text is empty then prompt user again
            if (name.isEmpty()) {
                etName.error = "Please enter a category name"
                return@setOnClickListener
            }
            if (targetText.isEmpty()) {
                etTarget.error = "Please enter your target amount"
                return@setOnClickListener
            }

            val target = targetText.toDoubleOrNull()
            if (target == null || target <= 0) {
                etTarget.error = "Please enter a valid amount"
                return@setOnClickListener
            }

            // Determine selected type from the RadioGroup
            //(StackOverflow, 2023)
            val selectedType = when (rgType.checkedRadioButtonId) {
                R.id.radioIncome -> "income"
                else -> "expense"
            }

            saveGoal(name, target, selectedType)
        }

        return view
    }

    //Firebase, 2026)
    //category is saved in firebase DB
    private fun saveGoal(name: String, target: Double, type: String, ) {
        // push creates a unique key for the new category

        val goalRef = database.child(uid).push()
        val goalId = goalRef.key ?: return

        val newGoal = Goal(
            goalId = goalId,
            goalName = name,
            targetAmount = target,
            currentAmount = 0.0,
            goalType = type,
            startDate = "2026/06/01",
            endDate = "2026/06/30"
        )

        goalRef.setValue(newGoal)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "$name goal created!", Toast.LENGTH_SHORT).show()
                // Return to the goal list
                //(Android Developers, 2026)
               if (isAdded) {
                   parentFragmentManager.popBackStack()
               }
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