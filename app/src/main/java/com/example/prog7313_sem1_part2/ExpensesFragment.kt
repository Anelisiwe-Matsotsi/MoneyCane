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

class ExpensesFragment : Fragment() {

    //firebase instance
    private val database = FirebaseDatabase.getInstance().getReference("transactions")

    //list categories through recycler view
    private val transactionList = ArrayList<Transaction>()
    private lateinit var rvTransactions: RecyclerView
    private lateinit var adapter: TransactionAdapter
private  var startDate: String= ""
    private  var endDate: String= ""
    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expenses, container, false)
         val  calendarView =view.findViewById<CalendarView>(R.id.calendarView)
        val btnStart= view.findViewById<Button>(R.id.btnStartDate)
        val btnEnd= view.findViewById<Button>(R.id.btnEndDate)

        btnStart.setOnClickListener {
            calendarView.setOnDateChangeListener { _,year, month, dayOfMonth ->
                startDate ="$year-${month+1}-$dayOfMonth"
                btnStart.text ="Start: $startDate"
                Toast.makeText(requireContext(), "Start date selected,", Toast.LENGTH_SHORT).show()

            }
        }
        btnEnd.setOnClickListener {
            calendarView.setOnDateChangeListener { _,year, month, dayOfMonth ->
                endDate ="$year-${month+1}-$dayOfMonth"
                btnEnd.text ="Start: $endDate"
                Toast.makeText(requireContext(), "End date selected,", Toast.LENGTH_SHORT).show()

                //filters only after end date is selected
            listenToTransactions()
            }
        }



        // setting up recycler view
        rvTransactions = view.findViewById(R.id.rvTransactions)
        rvTransactions.layoutManager = LinearLayoutManager(requireContext())






        adapter= TransactionAdapter(transactionList) {transaction ->

        }
        rvTransactions.adapter = adapter

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

private fun isWithinSelectedPeriod(date: String): Boolean{
    if (startDate.isEmpty() || endDate.isEmpty()) return true
    return date >=startDate && date <=endDate
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

                    if(transaction !=null && transaction.type == "expense" && isWithinSelectedPeriod(transaction.date)) {

                        transactionList.add(transaction)
                    }
                }
                adapter.notifyDataSetChanged()

            }

            //database error
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //navigates to edit fragment by fetching the category id through bundle
    /*private fun navigateToEdit(categoryId: String) {
        val editFragment = LightEditCategoryFragment()
        val args = Bundle()
        args.putString("transactionId", categoryId)
        editFragment.arguments = args

        //(Android Developers, 2026b)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, editFragment)
            .addToBackStack(null) //(Android Developers, 2026)
            .commit()
    }*/

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