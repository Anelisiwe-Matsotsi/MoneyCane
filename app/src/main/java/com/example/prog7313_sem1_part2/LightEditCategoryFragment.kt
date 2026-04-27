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

class LightEditCategoryFragment : Fragment() {

    //firebase instance
    private val database = FirebaseDatabase.getInstance().getReference("categories")

    // Category ID passed from LightCategoryFragment
    private var categoryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryId = arguments?.getString("categoryId")
    }

    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_light_edit_category, container, false)

        val editCategoryName = view.findViewById<EditText>(R.id.editCategoryName)
        val editCategoryLimit = view.findViewById<EditText>(R.id.editCategoryLimit)
        val radioGroupType = view.findViewById<RadioGroup>(R.id.radioGroupType)
        val radioExpense = view.findViewById<RadioButton>(R.id.radioExpense)
        val radioIncome = view.findViewById<RadioButton>(R.id.radioIncome)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnDelete = view.findViewById<TextView>(R.id.btnDelete)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        //(Android Developers, 2026)
        // Navigate back to the categories list
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Load the existing category values from Firebase and pre-fill the form
        loadCategory(categoryId) { category ->
            editCategoryName.setText(category.name)
            editCategoryLimit.setText(category.limit.toInt().toString())

            // Pre-select the correct radio button based on the saved type
            if (category.type == "income") {
                radioIncome.isChecked = true
            } else {
                radioExpense.isChecked = true
            }
        }

        // SAVE: update the editable fields without touching "spent"
        btnSave.setOnClickListener {
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

            updateCategory(name, limit, selectedType)
        }

        // DELETE: remove the category from Firebase
        btnDelete.setOnClickListener {
            deleteCategory()
        }

        return view
    }

    private fun loadCategory(id: String?, onLoaded: (Category) -> Unit) {
        if (id == null) return

        //(Firebase, 2026b)
        //fetched correct category unique id to be edited/updated
        database.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val category = snapshot.getValue(Category::class.java)
                if (category != null) {
                    onLoaded(category)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load category", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateCategory(name: String, limit: Double, type: String) {
        val id = categoryId ?: return

        val updates = mapOf<String, Any>(
            "name" to name,
            "limit" to limit,
            "type" to type
        )

        database.child(id).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Category updated!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Update failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    //(Android Knowledge, 2023)
    private fun deleteCategory() {
        val id = categoryId ?: return

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
        fun newInstance(categoryId: String) = LightEditCategoryFragment().apply {
            arguments = Bundle().apply {
                putString("categoryId", categoryId)
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