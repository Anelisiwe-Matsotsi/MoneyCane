package com.example.prog7313_sem1_part2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class home : Fragment(R.layout.fragment_home) {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profile = view.findViewById<ImageButton>(R.id.ib_arrow_forward)
        val wallet = view.findViewById<Button>(R.id.btn_add_wallet)
        val budget = view.findViewById<Button>(R.id.btn_add_budget)
        //val goal = view.findViewById<Button>(R.id.btn_set_goal)
        //dont know what this is for yet
        val transaction = view.findViewById<Button>(R.id.btn_add_transaction)
        //  val transactionView= view.findViewById<Button>(R.id.btn_view_all)
        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val walletBalance = view.findViewById<TextView>(R.id.tvWalletBalance)

        //initial state
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        //how much it shows
        bottomSheetBehavior.peekHeight = 110

        bottomSheetBehavior.isFitToContents = true


        //load transaction fragment into sheet
        childFragmentManager.beginTransaction()
            .replace(R.id.transaction_container, Home2Fragment())
            .commit()

        //close button
        val closebtn = view.findViewById<ImageButton>(R.id.btnCloseSheet)

        closebtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        //display updated value of wallet
        totalWalletBalance(walletBalance)

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
                if(isAdded){ //wallet balance will be 0 if database fails
                    walletBalance.text = "R0.00"
                }
            }
        })



    }
}
//ib_arrow_forward
//action_home2_to_wallet_Create_Light_Fragment2


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