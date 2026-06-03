package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random


class home : Fragment(R.layout.fragment_home) {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val uid = MainActivity.getUserID()
    private val budgetRef = FirebaseDatabase.getInstance().getReference("user_budgets").child(uid)
    private val transactionRef =
        FirebaseDatabase.getInstance().getReference("transactions").child(uid)
    private val categoryRef = FirebaseDatabase.getInstance().getReference("categories").child(uid)
    private val goalsRef = FirebaseDatabase.getInstance().getReference("weekly_goals").child(uid)

    private var grandTotalBudget = 0.0
    private var grandTotalSpent = 0.0
    private var weeklyMinGoal = 0.0
    private var weeklyMaxGoal = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profile = view.findViewById<ImageButton>(R.id.ib_arrow_forward)
        val wallet = view.findViewById<Button>(R.id.btn_add_wallet)
        val budget = view.findViewById<ImageButton>(R.id.btn_add_budget)
        val tvWelcomeName = view.findViewById<TextView>(R.id.tv_welcome_name)
        val btnSetGoal = view.findViewById<Button>(R.id.btn_set_goal)
        //dont know what this is for yet
        val transaction = view.findViewById<Button>(R.id.btn_add_transaction)
        //  val transactionView= view.findViewById<Button>(R.id.btn_view_all)
        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val walletBalance = view.findViewById<TextView>(R.id.tvWalletBalance)
        val tvMonthlyBudgetLeft = view.findViewById<TextView>(R.id.tv_monthly_budget_left)
        val pbMonthlyBudget = view.findViewById<ProgressBar>(R.id.pb_monthly_budget)
        val tvMonthlyBudgetOf = view.findViewById<TextView>(R.id.tv_monthly_budget_of)
        val tvWeeklyGoalLeft = view.findViewById<TextView>(R.id.tv_weekly_goal_left)
        val tvWeeklyGoalOf = view.findViewById<TextView>(R.id.tv_weekly_goal_of)
        val pbWeeklyGoal = view.findViewById<ProgressBar>(R.id.pb_weekly_goal)

        //Darkmode light mode
        val themeIcon = view.findViewById<ImageButton>(R.id.themeBtn)
        //Android Developers, 2026 - check what mode system is currently on
        val currentModeDark = (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        if (currentModeDark) {
            themeIcon.setImageResource(R.drawable.ic_light_mode)
            tvGreeting.text = "Hello, how is your day?"
        } else {
            themeIcon.setImageResource(R.drawable.ic_dark_mode)
            tvGreeting.text = "Hello, ready for some sweet budgets?"
        }

        themeIcon.setOnClickListener {
            val isDarkMode = (resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES

            //Stackoverflow,2022- switching system to light/dark mode
            if (isDarkMode) {
                //switch to light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                // Update icon to moon
                themeIcon.setImageResource(R.drawable.ic_dark_mode)
                tvGreeting.text = "Hello, ready for some sweet budgets?"

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                // Update icon to sun
                themeIcon.setImageResource(R.drawable.ic_light_mode)
                tvGreeting.text = "Hello, how is your day?"
            }
        }


        //initial state
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        //how much it shows
        bottomSheetBehavior.peekHeight = 110

        bottomSheetBehavior.isFitToContents = true

        //to get the users name to appear
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)
            userRef.get().addOnSuccessListener { snapshot ->
                // Using your userInfo data class
                val userInfo = snapshot.getValue(userInfo::class.java)
                if (userInfo != null && userInfo.name.isNotEmpty()) {
                    tvWelcomeName.text = userInfo.name
                } else {
                    tvWelcomeName.text = "userInfo"
                }
            }.addOnFailureListener {
                tvWelcomeName.text = "userInfo"
                // Optional: show error toast
                Toast.makeText(context, "Failed to load name", Toast.LENGTH_SHORT).show()
            }
        } else {
            tvWelcomeName.text = "userInfo"
        }


        //load transaction fragment into sheet
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.transaction_container, Home2Fragment())
                .commit()
        }
        //close button
        val closebtn = view.findViewById<ImageButton>(R.id.btnCloseSheet)

        closebtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        //display updated value of wallet
        totalWalletBalance(walletBalance)
        calculateLiveMonthlyDashboard(tvMonthlyBudgetLeft, pbMonthlyBudget, tvMonthlyBudgetOf, tvWeeklyGoalLeft, pbWeeklyGoal, tvWeeklyGoalOf)

        profile.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_profileLight)//to profile(light)
        }
        wallet.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_lightWalletFragment)//to wallet(light)
        }
        budget.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_budgetLight2)//to budgets(light)
        }
        transaction.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_transaction_Fragment)//to create transaction(light)
        }
        btnSetGoal.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_weeklyGoalSetupFragment) //to set goals
        }


//        transactionView.setOnClickListener {
//            findNavController().navigate(R.id.action_home_to_home2Fragment)//to transaction page(light)
//        }


    }

    private fun totalWalletBalance(walletBalance: TextView) {

        //get current user ID & wallet data
        val uid = MainActivity.getUserID()
        val walletRef = FirebaseDatabase.getInstance().getReference("wallets").child(uid)

        //wallet balance live updates after transactions are made or wallet amount updated
        walletRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                var total = 0.0 //total is set to double type

                //(Firebase, 2026b)
                //loop through all wallets to get the sum of them together
                for (child in snapshot.children) {
                    val wallet = child.getValue(Wallet::class.java) //gets value of each wallet
                    if (wallet != null) {
                        total += wallet.balance
                    }
                }
                walletBalance.text = "R${total.toInt()}" //UI gets updated with live wallet balance
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) { //wallet balance will be 0 if database fails
                    walletBalance.text = "R0.00"
                }
            }
        })
    }
    private fun calculateLiveMonthlyDashboard(tvLeft: TextView, progressBar: ProgressBar, tvOf: TextView, tvWeeklyLeft: TextView, pbWeekly: ProgressBar, tvWeeklyOf: TextView) {
        goalsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(goalSnapshot: DataSnapshot) {

                weeklyMinGoal = goalSnapshot.child("minGoal").getValue(Double::class.java) ?: 0.0
                weeklyMaxGoal = goalSnapshot.child("maxGoal").getValue(Double::class.java) ?: 2000.0

                loadBudgetsAndTransactions(tvLeft, progressBar, tvOf, tvWeeklyLeft, pbWeekly, tvWeeklyOf)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun loadBudgetsAndTransactions(tvLeft: TextView, progressBar: ProgressBar, tvOf: TextView, tvWeeklyLeft: TextView, pbWeekly: ProgressBar, tvWeeklyOf: TextView) {
    budgetRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(budgetSnapshot: DataSnapshot) {
                if (!isAdded) return
                grandTotalBudget = 0.0
                grandTotalSpent = 0.0

                val temporaryBudgetList = mutableListOf<Pair<String, Double>>()
                for (child in budgetSnapshot.children) {
                    val categoryName = child.child("category").getValue(String::class.java) ?: continue
                    val budgetAmount = child.child("budget").getValue(Double::class.java) ?: 0.0

                    grandTotalBudget += budgetAmount
                    temporaryBudgetList.add(Pair(categoryName, budgetAmount))
                }


                // Stackoverflow, 2023b
                categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(categorySnapshot: DataSnapshot) {
                        val categoryMap = mutableMapOf<String, String>()
                        for (cat in categorySnapshot.children) {
                            val categoryId = cat.key ?: continue
                            val name = cat.child("name").getValue(String::class.java) ?: continue
                            categoryMap[categoryId] = name
                        }

                        transactionRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(transactionSnapshot: DataSnapshot) {
                                for (child in transactionSnapshot.children) {
                                    val catId = child.child("categoryId").getValue(String::class.java) ?: continue
                                    val amount = child.child("amount").getValue(Double::class.java) ?: 0.0
                                    val transactionCategoryName = categoryMap[catId]

                                    val matchesActiveBudget = temporaryBudgetList.any {
                                        it.first.equals(transactionCategoryName, ignoreCase = true)
                                    }
                                    if (matchesActiveBudget) {
                                        grandTotalSpent += amount
                                    }
                                }
                                updateDashboardVisuals(tvLeft, progressBar, tvOf)
                                updateWeeklyGoalVisuals(tvWeeklyLeft, pbWeekly, tvWeeklyOf)
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateDashboardVisuals(tvLeft: TextView, progressBar: ProgressBar, tvOf: TextView) {
        if (!isAdded) return

        val remaining = (grandTotalBudget - grandTotalSpent).coerceAtLeast(0.0)

        tvLeft.text = "R %.2f left of R %.2f".format(remaining, grandTotalBudget)
        tvOf.text = "of R %.2f".format(grandTotalBudget) //Stackoverflow, 2018

        //Stackoverflow, 2019
        val progressPercent = if (grandTotalBudget > 0) {
            ((grandTotalSpent / grandTotalBudget) * 100).toInt().coerceIn(0, 100) //Kotlin, 2026
        } else {
            0
        }
        progressBar.progress = progressPercent
    }
    private fun updateWeeklyGoalVisuals(tvWeeklyLeft: TextView, pbWeekly: ProgressBar, tvWeeklyOf: TextView) {
        if (!isAdded) return

        val remainingUntilMax = (weeklyMaxGoal - grandTotalSpent).coerceAtLeast(0.0)

        tvWeeklyLeft.text = "R %.2f left to Max".format(remainingUntilMax)
        tvWeeklyOf.text = "Min: R %.0f | Max: R %.0f".format(weeklyMinGoal, weeklyMaxGoal)

        // Progress bar tracks where current spending sits within your max ceiling parameters
        val progressPercent = if (weeklyMaxGoal > 0) {
            ((grandTotalSpent / weeklyMaxGoal) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
        pbWeekly.progress = progressPercent
    }
}
//ib_arrow_forward
//action_home2_to_wallet_Create_Light_Fragment2


/*
References
Android Developers. 2026. Implement dark theme | Views. [source code]. Available at: < https://developer.android.com/develop/ui/views/theming/darktheme > [Accessed 3 June 2026].
Android Developers. 2026. Fragment manager | App architecture. (version 8) [source code]. Available at: < https://developer.android.com/guide/fragments/fragmentmanager > [Accessed 26 April 2026]
Android Developers. 2026b. fragments transactions. (version 8) [source code]. Available at: < https://developer.android.com/guide/fragments/transactions > [Accessed 26 April 2026]
Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Bak, K. 2023. Validating user input in kotlin: a guide to adding validation to a new transaction. (version 8) [source code]. Available at: < https://53jk1.medium.com/validating-user-input-in-kotlin-a-guide-to-adding-validation-to-a-new-transaction-2e486f176674 > [Accessed 26 April 2026]
Bhosale, A. 2024. Kotlin under the hood: exploring objects, companion objects, and annotations: @JvmStatic, @JvmField, @JvmOverloads. (version 8) [source code]. Available at: < https://proandroiddev.com/kotlin-under-the-hood-exploring-objects-companion-objects-and-annotations-jvmstatic-71e1bf1128df > [Accessed 26 April 2026]
Firebase. 2026. Read and Write Data on Android. (version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/read-and-write > [Accessed 26 April 2026]
Firebase. 2026b. Work with Lists of Data on Android.(Version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/lists-of-data#child-events > [Accessed 26 April 2026]
Kotlin. 2026. coerceIn. [online]. Available at: <https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.ranges/coerce-in.html>. [Accessed 29 May 2026].
Stackoverflow. 2018. Kotlin – String formatting. [online]. Available at: <https://stackoverflow.com/questions/23086291/kotlin-string-formatting>. [Accessed 29 May 2026].
Stackoverflow. 2019. How to update Progress Bar Percentage with Data Binding. [online]. Available at: <https://stackoverflow.com/questions/54644318/how-to-update-progress-bar-percentage-with-data-binding> .[Accessed 29 May 2026].
StackOverflow. 2023. Set Variable depending on RadioGroup button selected in Kotlin. (Version 8) [source code]. Available at: < https://stackoverflow.com/questions/77541256/set-variable-depending-on-radiogroup-button-selected-in-kotlin > [Accessed 26 April 2026]
 Stackoverflow. 2023b. The best practices using ViewModel and Flow to communicate with Firebase. [online]. Available at: < https://stackoverflow.com/questions/75769524/the-best-practices-using-viewmodel-and-flow-to-communicate-with-firebase >[Accessed 2 June 2026].
 Stack Overflow. 2022. Proper way to Implement Dark Theme Android Java. [source code]. Available at: https://stackoverflow.com/questions/70723977/proper-way-to-implement-dark-theme-android-java [Accessed 3 June 2026].
 */