package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.widget.SearchView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7313_sem1_part2.MyMessagingService.Companion.showNotification
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LightWalletFragment : Fragment() {

    //firebase instance
    private val uid = MainActivity.getUserID()

    private val database = FirebaseDatabase.getInstance().getReference("wallets") .child(uid)

    //list wallets through recycler view
    private val walletList = ArrayList<Wallet>()
    private lateinit var rvWallets: RecyclerView
    private lateinit var adapter: WalletAdapter
    private lateinit var tvTotalBalance: TextView

    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet_light, container, false)

        // setting up recycler view
        rvWallets = view.findViewById(R.id.rvWallets)
        rvWallets.layoutManager = LinearLayoutManager(requireContext())
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance)

        // When the edit pencil is clicked then edit screen pops up
        adapter = WalletAdapter(walletList,
        onEditClick = {wallet -> navigateToEdit(wallet.id) },
        onDeleteClick = {wallet -> deleteWallet(wallet) }) //when delete icon is clicked the wallet deletes and success message will display

        rvWallets.adapter = adapter

        //(Android Developers, 2026)
        //add icon: navigates to Create Wallet interface ---
        val iconAddWallet = view.findViewById<FloatingActionButton>(R.id.iconAddWallet)
        iconAddWallet.setOnClickListener {
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, CreateWalletFragment())
//                .addToBackStack(null)
//                .commit()
            findNavController().navigate(R.id.action_lightWalletFragment_to_createWalletFragment)//navigates to the createWalletFragment

        }

        //(GeeksforGeeks, 2025)
        val searchWallets = view.findViewById<SearchView>(R.id.searchWallets) //initialize variables of list view with ID's
        searchWallets.setOnQueryTextListener(object : SearchView.OnQueryTextListener { //added on query listener
            override fun onQueryTextSubmit(query: String?): Boolean = false //no wallet name matches from list

            //(StackOverflow, 2019)
            //if query text is change, we filter from "filterWallets" (linked to WalletAdapter) with new text
            override fun onQueryTextChange(newText: String?): Boolean {
                filterWallets(newText)
                return true
            }
        })

        listenToWallets()

        return view
    }

    //(CodingSTUFF, 2022)
    //filters the wallets populated in the arraylist
    private fun filterWallets(query: String?) {
        if (query.isNullOrEmpty()) {
            adapter = WalletAdapter(walletList,
                onEditClick = {wallet -> navigateToEdit(wallet.id) },
                onDeleteClick = {wallet -> deleteWallet(wallet) })
            rvWallets.adapter = adapter
        } else {
            //filtering letter by letter ignoring the letter casing
            val filtered = ArrayList(walletList.filter { item ->
                item.name.contains(query, ignoreCase = true)
            })

            //card view searched still shows the edit and delete fucntions
            rvWallets.adapter = WalletAdapter(filtered,
                onEditClick = { wallet -> navigateToEdit(wallet.id) },
                onDeleteClick = { wallet -> deleteWallet(wallet) })
        }
    }

    //prevents spamming existing wallet notifications
    private var initialLoad = true

    //recycler view updates everytime if a wallet is added or edited
    private fun listenToWallets() {
        database.addValueEventListener(object : ValueEventListener {

            //(Firebase, 2026b)
            //fetched correct wallet's unique id to be edited/updated
            override fun onDataChange(snapshot: DataSnapshot) {
                walletList.clear()
                for (child in snapshot.children) {
                    val wallet = child.getValue(Wallet::class.java)
                    if (wallet != null) {
                        //logged in user sees notification of new shared wallet created by owner
                        if(!initialLoad && !walletList.any { it.id == wallet.id} && wallet.createdBy != uid) {
                            showNotification(
                                context=requireContext(),
                                title = "New Shared Wallet!",
                                message = "You have been added to a new wallet called '${wallet.name}'."
                            )
                        }
                        walletList.add(wallet)
                    }
                }
                adapter.notifyDataSetChanged()
                updateTotalBalance() //wallets total updates after wallets are added/edited
                initialLoad = false
            }

            //database error
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //Calculate total balances of all wallets
    private fun updateTotalBalance() {
        val total = walletList.sumOf { item -> item.balance }
        tvTotalBalance.text = "R${total.toInt()}"
    }

    //navigates to edit fragment by fetching the wallet id through bundle
    private fun navigateToEdit(walletId: String) {
        val args = Bundle()
        args.putString("walletId", walletId)
        findNavController().navigate(R.id.action_lightWalletFragment_to_lightEditWalletFragment, args)

    }

    //(StackOverflow,2019b)
    private fun deleteWallet(wallet: Wallet) {
        database.child(wallet.id).removeValue() //gets wallet ID to delete
            .addOnSuccessListener { //success message when delete icon works
                Toast.makeText(requireContext(), "${wallet.name} deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Delete failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    //(Bhosale, 2024)
    companion object {
        @JvmStatic //can access methods directly without needing to reference an instance variable
        fun newInstance() = LightWalletFragment()
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