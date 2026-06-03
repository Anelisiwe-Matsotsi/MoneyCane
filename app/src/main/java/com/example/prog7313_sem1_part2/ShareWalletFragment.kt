package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.os.bundleOf

class ShareWalletFragment : Fragment() {

    //firebase instance
    private val uid = MainActivity.getUserID()

    private val database = FirebaseDatabase.getInstance().getReference("users")
    private lateinit var rvEmails: RecyclerView
    private lateinit var adapter: EmailAdapter
    private val emailList = ArrayList<User>() //list emails through recycler view
    private val registeredUsers = ArrayList<User>() //registered emails appear in view when searched

    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.share_wallet, container, false)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        // setting up recycler view
        rvEmails = view.findViewById(R.id.rvEmails)
        rvEmails.layoutManager = LinearLayoutManager(requireContext())

        adapter = EmailAdapter(registeredUsers)

        rvEmails.adapter = adapter

        btnBack.setOnClickListener {
            //get the list of people the wallet is being shared with to pass the variable to the create wallet fragment
            val selectedPeople = adapter.getSelectedEmails()

            //matching the selected peuple to their user data to get fcmtoken
            val selectedUser = emailList.filter {it.email in selectedPeople}

            //creating bundle to pass selected emails to Wallet fragment
            val bundle = bundleOf ("selectedEmails" to ArrayList(selectedUser)) //passing all the data from User.kt

            parentFragmentManager.setFragmentResult("sharedWalletInvite", bundle)
            findNavController().navigateUp()
        }

        listenToEmails() //calls all the registered users to be loaded into array for selection

        //(GeeksforGeeks, 2025)
        val searchEmails = view.findViewById<SearchView>(R.id.searchEmails) //initialize variables of list view with ID's
        searchEmails.setOnQueryTextListener(object : SearchView.OnQueryTextListener { //added on query listener
            override fun onQueryTextSubmit(query: String?): Boolean = false //no email name matches from list

            //(StackOverflow, 2019)
            //if query text is change, we filter from "filterWallets" (linked to WalletAdapter) with new text
            override fun onQueryTextChange(newText: String?): Boolean {
                filterEmails(newText)
                return true
            }
        })
        return view
    }

    //(CodingSTUFF, 2022)
    //filters the wallets populated in the arraylist
    private fun filterEmails(query: String?) {
        registeredUsers.clear()
        if (query.isNullOrEmpty()) {
            registeredUsers.addAll(emailList)
        } else {
            //(StackOverflow, 2019)
            //filtering letter by letter ignoring the letter casing
            val filtered = ArrayList(emailList.filter { item ->
                item.email.contains(query, ignoreCase = true)
            })
            registeredUsers.addAll(filtered)
        }
        adapter.notifyDataSetChanged()
    }

    private fun listenToEmails() {
        database.addValueEventListener(object : ValueEventListener {

            //(Firebase, 2026b)
            //fetched correct wallet's unique id to be edited/updated
            override fun onDataChange(snapshot: DataSnapshot) {
                emailList.clear()
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    if (user != null) {
                        emailList.add(user)
                    }
                }
                filterEmails(null) //shows all emails before user searches
            }

            //database error
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error loading email addresses", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

/*
References

Android Developers. 2026. Fragment manager | App architecture. (version 8) [source code]. Available at: < https://developer.android.com/guide/fragments/fragmentmanager > [Accessed 26 April 2026]
Android Developers. 2026b. fragments transactions. (version 8) [source code]. Available at: < https://developer.android.com/guide/fragments/transactions > [Accessed 26 April 2026]
Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Bak, K. 2023. Validating user input in kotlin: a guide to adding validation to a new transaction. (version 8) [source code]. Available at: < https://53jk1.medium.com/validating-user-input-in-kotlin-a-guide-to-adding-validation-to-a-new-transaction-2e486f176674 > [Accessed 26 April 2026]
Bhosale, A. 2024. Kotlin under the hood: exploring objects, companion objects, and annotations: @JvmStatic, @JvmField, @JvmOverloads. (version 8) [source code]. Available at: < https://proandroiddev.com/kotlin-under-the-hood-exploring-objects-companion-objects-and-annotations-jvmstatic-71e1bf1128df > [Accessed 26 April 2026]
CodingSTUFF. 2022. SearchView with RecyclerView in Kotlin - Android Studio Tutorial (2022). (Version 8) [source code]. Available at: < https://youtu.be/SD097oVVrPE?si=DlBMjYGsFJOLdLWo > [Accessed 26 April 2026]
Firebase. 2026. Read and Write Data on Android. (version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/read-and-write > [Accessed 26 April 2026]
Firebase. 2026b. Work with Lists of Data on Android.(Version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/lists-of-data#child-events > [Accessed 26 April 2026]
GeeksforGeeks. 2025. SearchView in Android with Kotlin. (Version 8) [source code]. Available at: < https://www.geeksforgeeks.org/kotlin/searchview-in-android-with-kotlin/ > [Accessed 27 April 2026]
StackOverflow. 2023. Set Variable depending on RadioGroup button selected in Kotlin. (Version 8) [source code]. Available at: < https://stackoverflow.com/questions/77541256/set-variable-depending-on-radiogroup-button-selected-in-kotlin > [Accessed 26 April 2026]
StackOverflow. 2019. Filter for SearchView in Kotlin. (version 8) [Source Code]. Available at: < https://stackoverflow.com/questions/55537368/filter-for-searchview-in-kotlin > [Accessed 27 April 2026]
StackOverflow. 2019b. Deleting random 2nd child in firebase realtime database that is created with push in Android Studio. (version 8) [source code]. Available at: < https://stackoverflow.com/questions/54385451/deleting-random-2nd-child-in-firebase-realtime-database-that-is-created-with-pus > [Accessed 27 April 2026]
 */