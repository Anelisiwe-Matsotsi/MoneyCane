package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase

class WeeklyGoalSetupFragment : Fragment(R.layout.fragment_weekly_goal_setup) {

    private val uid = MainActivity.getUserID()
    private val goalsRef = FirebaseDatabase.getInstance().getReference("weekly_goals").child(uid)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etMinGoal = view.findViewById<EditText>(R.id.et_min_goal)
        val etMaxGoal = view.findViewById<EditText>(R.id.et_max_goal)
        val btnSaveGoal = view.findViewById<Button>(R.id.btn_save_goal)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back_goal)

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        btnSaveGoal.setOnClickListener {
            val minGoalText = etMinGoal.text.toString().trim()
            val maxGoalText = etMaxGoal.text.toString().trim()

            if (minGoalText.isEmpty() || maxGoalText.isEmpty()) {
                Toast.makeText(context, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val minGoal = minGoalText.toDoubleOrNull()
            val maxGoal = maxGoalText.toDoubleOrNull()

            if (minGoal == null || maxGoal == null) {
                Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (minGoal > maxGoal) {
                Toast.makeText(context, "Minimum goal cannot be greater than Maximum goal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create data map matching the keys used by home.kt
            val goalData = mapOf(
                "minGoal" to minGoal,
                "maxGoal" to maxGoal
            )

            btnSaveGoal.isEnabled = false
            goalsRef.setValue(goalData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Weekly goals saved successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack() // Navigate back to main dashboard
                }
                .addOnFailureListener {
                    btnSaveGoal.isEnabled = true
                    Toast.makeText(context, "Failed to save targets. Try again.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}