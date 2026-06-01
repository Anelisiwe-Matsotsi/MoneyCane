package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.app.DatePickerDialog// Android Developers, Date Picker Dialog
import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Period
import java.util.Calendar // Android Developers, Calender
import java.util.Date
import java.util.Locale

class ExpensesFragment : Fragment() {

    //firebase instance
    val uid = MainActivity.getUserID()

    //firebase instance
    private val database = FirebaseDatabase.getInstance().getReference("transactions") .child(uid)

    //list categories through recycler view
    private val transactionList = ArrayList<Transaction>()
    private lateinit var rvTransactions: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private var selectedYear: Int = -1
    private var selectedMonth: Int = -1
    private var currentPeriod = "all"

    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expenses, container, false)

        // setting up recycler view
        rvTransactions = view.findViewById(R.id.rvTransactions)
        rvTransactions.layoutManager = LinearLayoutManager(requireContext())

        adapter = TransactionAdapter(transactionList) { transaction ->

        }
        rvTransactions.adapter = adapter

        //call Select period buttons
        val btnall = view.findViewById<TextView>(R.id.btnAll)
        val btnToday = view.findViewById<TextView>(R.id.btnToday)
        val btnSelectMonth = view.findViewById<TextView>(R.id.btnSelectMonth)
        btnall.setOnClickListener { setPeriod("all") }
        btnToday.setOnClickListener { setPeriod("today") }
        btnSelectMonth.setOnClickListener {
            val months = arrayOf(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            )


            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)

            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Month")
                .setItems(months) { _, which ->

                    selectedMonth = which
                    selectedYear = currentYear
                    currentPeriod= "monthly"

                    btnSelectMonth.text ="${months[which]}$currentYear"
                    listenToTransactions()
                }
                .show()
        }
          /*  val datePicker = DatePickerDialog(
                requireContext(), { _, year, month, _ ->
                    selectedYear = year
                    selectedMonth = month
                    currentPeriod = "monthly"
                    btnSelectMonth.text = "Month: ${month + 1}/$year"

                    listenToTransactions()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)

            )
            datePicker.show()
        }*/

        //(Android Developers, 2026)
        //add icon: navigates to Create Category interface ---
        val iconAddTransaction = view.findViewById<FloatingActionButton>(R.id.iconAddTransaction)
        iconAddTransaction.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Transaction_Fragment())
                .addToBackStack(null)
                .commit()
        }

        listenToTransactions()

        return view
    }


    private fun setPeriod(
        period: String
    ) {
        currentPeriod = period

        listenToTransactions()

    }

    //recycler view updates everytime if a category is added or edited
    private fun listenToTransactions() {
        database.addValueEventListener(object : ValueEventListener {

            //(Firebase, 2026b)
            //fetched correct category unique id to be edited/updated
            override fun onDataChange(snapshot: DataSnapshot) {
                transactionList.clear()
                for (child in snapshot.children) {
                    val transaction = child.getValue(Transaction::class.java)

                    if (transaction != null && transaction.type == "expense") {
                        if (isWithinSelectedPeriod
                                (transaction.date)
                        ) {

                            transactionList.add(transaction)
                        }
                    }
                }
                //SORT BY DATE- NEWEST TO OLDEST
                transactionList.sortByDescending{it.date}
                adapter.notifyDataSetChanged()

            }

            //database error
            override fun onCancelled(error: DatabaseError) {
                if(isAdded) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    //Java Calender ApI
    private fun isWithinSelectedPeriod(date: String): Boolean {
        if (currentPeriod == "all") return true

        val transactionDate = parseDate(date) ?: return false


        val cale = Calendar.getInstance().apply { time = transactionDate }

        return when (currentPeriod) {
            "today" -> {
                val today = Calendar.getInstance()
                cale.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        cale.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            }

            "monthly" -> {
                if (selectedMonth == -1 || selectedYear == -1) return false
                cale.get(Calendar.YEAR) == selectedYear &&
                        cale.get(Calendar.MONTH) == selectedMonth
            }

            else -> true
        }
    }

    private fun parseDate(date: String): Date? {
        val formats = listOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
            SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()),
            SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        )
        for (format in formats) {
            try {
                return format.parse(date)
            } catch (e: Exception) {
                continue

            }

        }
        return null
    }
        //(Bhosale, 2024)
        companion object {
            @JvmStatic //can access methods directly without needing to reference an instance variable
            fun newInstance() = ExpensesFragment()
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