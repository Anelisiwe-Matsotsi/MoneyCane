package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.Calendar

class BudgetLight : Fragment() {

    private val uid = MainActivity.getUserID()

    private val budgetRef = FirebaseDatabase.getInstance().getReference("user_budgets").child(uid)

    private val transactionRef = FirebaseDatabase.getInstance().getReference("transactions").child(uid)

    private val categoryRef = FirebaseDatabase.getInstance().getReference("categories").child(uid)

    private lateinit var adapter: CategoryAdapter

    private val categories = arrayListOf<Category>()

    private lateinit var tvSpentTotal: TextView
    private lateinit var tvBudgetTotal: TextView
    private lateinit var tvBudgetRemaining: TextView
    private lateinit var tvDailyBudget: TextView
    private lateinit var progressTotalBudget: ProgressBar
    private lateinit var etCategory: EditText
    private lateinit var etAmount: EditText

    private var editingCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_budget_light, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        tvSpentTotal = view.findViewById(R.id.tv_total_spent_val)

        tvBudgetTotal = view.findViewById(R.id.tv_total_budget_val)

        tvBudgetRemaining = view.findViewById(R.id.tvBudgetRemaining)

        tvDailyBudget = view.findViewById(R.id.tvDailyBudget)

        progressTotalBudget = view.findViewById(R.id.progressTotalBudget)

        etCategory = view.findViewById(R.id.etBudgetCategory)

        etAmount = view.findViewById(R.id.etBudgetAmount)

        view.findViewById<ImageButton>(R.id.iv_back_arrow).setOnClickListener {

            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val recycler = view.findViewById<RecyclerView>(R.id.rv_budgets)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = CategoryAdapter(categories) { category ->

                editingCategory = category.name

                etCategory.setText(category.name)

                etAmount.setText(category.limit.toString())

                Toast.makeText(requireContext(), "Editing ${category.name}", Toast.LENGTH_SHORT).show()
            }

        recycler.adapter = adapter

        view.findViewById<Button>(R.id.btnSaveBudget).setOnClickListener {

            val category = etCategory.text.toString().trim()

            val amount = etAmount.text.toString().toDoubleOrNull()

            if (category.isEmpty() || amount == null || amount <= 0)
            {

                Toast.makeText(requireContext(), "Enter valid values", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            val budget = Budget(
                category = category,
                budget = amount
            )

            val key =
                editingCategory ?: category

            budgetRef.child(key)
                .setValue(budget)
                .addOnSuccessListener {

                    if (
                        editingCategory != null &&
                        editingCategory != category
                    ) {

                        budgetRef.child(
                            editingCategory!!
                        ).removeValue()
                    }

                    editingCategory = null

                    etCategory.text.clear()
                    etAmount.text.clear()

                    loadBudgets()

                    Toast.makeText(
                        requireContext(),
                        "Budget saved",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        loadBudgets()
    }

    override fun onResume() {
        super.onResume()

        loadBudgets()
    }

    private fun loadBudgets() {

        categories.clear()
        adapter.notifyDataSetChanged()

        budgetRef.addListenerForSingleValueEvent(

            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    for (child in snapshot.children) {

                        val categoryName =
                            child.child("category")
                                .getValue(String::class.java)
                                ?: continue

                        val budgetAmount =
                            child.child("budget")
                                .getValue(Double::class.java)
                                ?: 0.0

                        calculateSpent(
                            categoryName,
                            budgetAmount
                        )
                    }
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {
                }
            })
    }

    private fun calculateSpent(
        categoryName: String,
        budgetAmount: Double
    ) {

        categoryRef.addListenerForSingleValueEvent(

            object : ValueEventListener {

                override fun onDataChange(
                    categorySnapshot: DataSnapshot
                ) {

                    val categoryMap =
                        mutableMapOf<String, String>()

                    for (cat in categorySnapshot.children) {

                        val categoryId =
                            cat.key ?: continue

                        val name =
                            cat.child("name")
                                .getValue(String::class.java)
                                ?: continue

                        categoryMap[categoryId] =
                            name
                    }

                    transactionRef.addListenerForSingleValueEvent(

                        object : ValueEventListener {

                            override fun onDataChange(
                                snapshot: DataSnapshot
                            ) {

                                var spent = 0.0

                                for (child in snapshot.children) {

                                    val categoryId =
                                        child.child("categoryId")
                                            .getValue(String::class.java)

                                    val amount =
                                        child.child("amount")
                                            .getValue(Double::class.java)
                                            ?: 0.0

                                    val transactionCategoryName =
                                        categoryMap[categoryId]

                                    if (
                                        transactionCategoryName.equals(
                                            categoryName,
                                            true
                                        )
                                    ) {
                                        spent += amount
                                    }
                                }

                                val categoryObj =
                                    Category(
                                        name = categoryName,
                                        type =
                                            if (spent >= budgetAmount)
                                                "Exceeded"
                                            else if (
                                                spent >= budgetAmount * 0.8
                                            )
                                                "Near Limit"
                                            else
                                                "Safe",
                                        spent = spent,
                                        limit = budgetAmount
                                    )

                                categories.add(
                                    categoryObj
                                )

                                adapter.notifyDataSetChanged()

                                updateTotals()
                            }

                            override fun onCancelled(
                                error: DatabaseError
                            ) {
                            }
                        })
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {
                }
            })
    }

    private fun updateTotals() {

        var totalSpent = 0.0
        var totalBudget = 0.0

        categories.forEach {

            totalSpent += it.spent
            totalBudget += it.limit
        }

        tvSpentTotal.text =
            "R%.2f".format(totalSpent)

        tvBudgetTotal.text =
            "R%.2f".format(totalBudget)

        val remaining =
            (totalBudget - totalSpent)
                .coerceAtLeast(0.0)

        tvBudgetRemaining.text =
            "R%.2f left of R%.2f"
                .format(
                    remaining,
                    totalBudget
                )

        val progress =
            if (totalBudget > 0) {
                ((totalSpent / totalBudget) * 100)
                    .toInt()
                    .coerceIn(0, 100)
            } else {
                0
            }

        progressTotalBudget.progress =
            progress

        updateDailyBudget(
            remaining
        )
    }

    private fun updateDailyBudget(
        remainingBudget: Double
    ) {

        val calendar =
            Calendar.getInstance()

        val today =
            calendar.get(
                Calendar.DAY_OF_MONTH
            )

        val daysInMonth =
            calendar.getActualMaximum(
                Calendar.DAY_OF_MONTH
            )

        val daysLeft =
            (daysInMonth - today + 1)
                .coerceAtLeast(1)

        val dailyAmount =
            remainingBudget / daysLeft

        tvDailyBudget.text =
            "Spend R%.2f/day to stay within budget"
                .format(
                    dailyAmount
                )
    }
}
//ib_arrow_forward
//action_home2_to_wallet_Create_Light_Fragment2
//action_budgetLight_to_analysisLight