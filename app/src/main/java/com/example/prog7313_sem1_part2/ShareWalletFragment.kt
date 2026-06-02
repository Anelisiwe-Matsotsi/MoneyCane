package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase

class ShareWalletFragment : Fragment() {

    //firebase instance
    private val uid = MainActivity.getUserID()

    private val database = FirebaseDatabase.getInstance().getReference("wallets") .child(uid)

//    //(Gaurav, 2025)
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.share_wallet, container, false)
//
//        //(CodingSTUFF, 2022)
//        //filters the wallets populated in the arraylist
//        private fun filterEmails(query: String?) {
//            if (query.isNullOrEmpty()) {
//                adapter = EmailAdapter(emailList,
////                    onEditClick = {wallet -> navigateToEdit(wallet.id) },
////                    onDeleteClick = {wallet -> deleteWallet(wallet) })
//                rvEmails.adapter = adapter
//            } else {
//                //filtering letter by letter ignoring the letter casing
//                val filtered = ArrayList(emailList.filter { item ->
//                    item.name.contains(query, ignoreCase = true)
//                })
//
//                //card view searched still shows the edit and delete functions
//                rvEmails.adapter = WalletAdapter(filtered
////                    ,onEditClick = { wallet -> navigateToEdit(wallet.id) },
////                    onDeleteClick = { wallet -> deleteWallet(wallet) })
//            }
//        }
//
//        //(GeeksforGeeks, 2025)
//        val searchEmails =
//            view.findViewById<SearchView>(R.id.searchEmails) //initialize variables of list view with ID's
//        searchEmails.setOnQueryTextListener(object :
//            SearchView.OnQueryTextListener { //added on query listener
//            override fun onQueryTextSubmit(query: String?): Boolean =
//                false //no email name matches from list
//
//            //(StackOverflow, 2019)
//            //if query text is change, we filter from "filterWallets" (linked to WalletAdapter) with new text
//            override fun onQueryTextChange(newText: String?): Boolean {
//                filterEmails(newText)
//                return true
//            }
//        })
//    }
}