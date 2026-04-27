package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllTransactionsFragment : Fragment() {

    //firebase instance
    private val database = FirebaseDatabase.getInstance().getReference("transactions")

    //list categories through recycler view
    private val transactionList = ArrayList<Transaction>()
    private lateinit var rvTransactions: RecyclerView
    private lateinit var adapter: TransactionAdapter

    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_light_category, container, false)

        // setting up recycler view
        rvTransactions = view.findViewById(R.id.rvTransactions)
        rvTransactions.layoutManager = LinearLayoutManager(requireContext())


        rvTransactions.adapter = adapter

        //(Android Developers, 2026)
        //add icon: navigates to Create Category interface ---
        val iconAddTransaction = view.findViewById<FloatingActionButton>(R.id.btnSave)
        iconAddTransaction.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Transaction_Fragment())
                .addToBackStack(null)
                .commit()
        }

        listenToCategories()

        return view
    }

    //recycler view updates everytime if a category is added or edited
    private fun listenToCategories() {
        database.addValueEventListener(object : ValueEventListener {

            //(Firebase, 2026b)
            //fetched correct category unique id to be edited/updated
            override fun onDataChange(snapshot: DataSnapshot) {
                transactionList.clear()
                for (child in snapshot.children) {
                    val transaction = child.getValue(Transaction::class.java)
                    if (transaction != null) {
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
        fun newInstance() = AllTransactionsFragment()
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