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
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase

class CreateWalletFragment : Fragment() {

    // Firebase Realtime Database reference
    private val database = FirebaseDatabase.getInstance().getReference("wallets")
    private val uid = MainActivity.getUserID()

    //stores the User information from data class chosen by current user to share wallet with
    private var selectedPeopleList: List<User> = emptyList() //type parameter is inferred from the expected type (Kotlin Programming Language, 2026)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //(Android Developers, 2026b)
        //Passing email & UserID & fcmToken between parent and child fragment
        parentFragmentManager.setFragmentResultListener("sharedWalletInvite", this) {sharedWalletInvite, bundle ->
            //(StackOverflow,2023b)
            //getStringArrayList- store selected emails to pass it to next activity as a group

            //getting the full serialized user object to store in an array form to link the User ID to the correct email to send the notification linked to the correct FCM token
            val emails = bundle.getSerializable("selectedEmails") as? ArrayList<User>
            if (emails != null) {
                selectedPeopleList = emails

                //number of emails chosen is displayed before creating wallet
                val tvShareWallet = view?.findViewById<TextView>(R.id.tvShareWallet)
                tvShareWallet?.text = "Share Wallet with Other people: ${selectedPeopleList.size} selected >" //(StackOverflow, 2018)
            }
        }
    }

    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_wallet, container, false)

        val editWalletName = view.findViewById<EditText>(R.id.editWalletName)
        val editWalletBalance = view.findViewById<EditText>(R.id.editWalletBalance)
        val btnAddWallet = view.findViewById<Button>(R.id.btnAddWallet)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val tvShareWallet = view.findViewById<TextView>(R.id.tvShareWallet)

//        val radioGroupType = view.findViewById<RadioGroup>(R.id.radioGroupType)
//        val radioExpense = view.findViewById<RadioButton>(R.id.radioExpense)
//        val radioIncome = view.findViewById<RadioButton>(R.id.radioIncome)


        //(Android Developers, 2026)
        // Navigate back to the categories list
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        //when user clicks the textview they are redirected to the invite people UI to choose wallet members
        tvShareWallet.setOnClickListener {
            findNavController().navigate(R.id.action_createWalletFragment_to_shareWalletFragment)
        }

        btnAddWallet.setOnClickListener {
            val name = editWalletName.text.toString().trim()
            val balanceText = editWalletBalance.text.toString().trim()

            //(Bak, 2023)
            // Input validation- if edit text is empty then prompt user again
            if (name.isEmpty()) {
                editWalletName.error = "Please enter a wallet name"
                return@setOnClickListener
            }
            if (balanceText.isEmpty()) {
                editWalletBalance.error = "Please enter an initial balance"
                return@setOnClickListener
            }

            val limit = balanceText.toDoubleOrNull()
            if (limit == null || limit <= 0) {
                editWalletBalance.error = "Please enter a valid amount"
                return@setOnClickListener
            }

//            // Determine selected type from the RadioGroup
//            //(StackOverflow, 2023)
//            val selectedType = when (radioGroupType.checkedRadioButtonId) {
//                R.id.radioIncome -> "income"
//                else -> "expense"
//            }

            saveWallet(name, limit, selectedPeopleList)
        }

        return view
    }

    //Firebase, 2026)
    //wallet is saved in firebase DB
    private fun saveWallet(name: String, limit: Double, sharedPeople: List<User>) {
        // push creates a unique key for the new wallet
        val getDatabase = FirebaseDatabase.getInstance().reference
        val walletRef = getDatabase.child(uid).push()//saves the data under the user ID, as a child
        val walletId = walletRef.key ?: return

        //fetch the email address to save other wallet data into new wallet only
        val getEmails = sharedPeople.map{it.email}

        val newWallet = Wallet(
            id = walletId,
            name = name,
            balance = limit,
            people = getEmails
        )

        //multi-node database update map pathing
        val childUpdates = HashMap<String, Any>()

        //saves to the wallet creators Wallets UI
        childUpdates["/wallets/$uid/$walletId"] = newWallet

        //saves to the other added wallet members wallets UI
        for (user in sharedPeople) {
            if(user.uid.isNotEmpty()) {
                childUpdates["/wallets/${user.uid}/$walletId"] = newWallet
            }
        }

        getDatabase.updateChildren(childUpdates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "$name wallet created!", Toast.LENGTH_SHORT).show()

                //notification is sent to emails chosen via token
                sendNotification(name, sharedPeople)

                // Return to the wallet list
                //(Android Developers, 2026)
                findNavController().popBackStack()
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Failed to save: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendNotification(walletName: String, receivers: List<User>) {
        for (user in receivers) {
            if(user.fcmToken.isNotEmpty()) {

            }
        }
    }
}

/*
References

Android Developers. 2026. Fragment manager | App architecture. (version 8) [source code]. Available at: < https://developer.android.com/guide/fragments/fragmentmanager > [Accessed 26 April 2026]
Android Developers. 2026b. Communicate with fragments. (version 8) [Source Code]. Available at: <https://developer.android.com/guide/fragments/communicate#pass-parent-child> [Accessed 2 June 2026]
Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Bak, K. 2023. Validating user input in kotlin: a guide to adding validation to a new transaction. (version 8) [source code]. Available at: < https://53jk1.medium.com/validating-user-input-in-kotlin-a-guide-to-adding-validation-to-a-new-transaction-2e486f176674 > [Accessed 26 April 2026]
Firebase. 2026. Read and Write Data on Android. (version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/read-and-write > [Accessed 26 April 2026]
Kotlin Programming Language. 2026. EmptyList | Core API- Kotlin Programming Language. (version 8) [Source Code]. Available at: <https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/empty-list.html> [Accessed 1 June 2026]
StackOverflow. 2023. Set Variable depending on RadioGroup button selected in Kotlin. (Version 8) [source code]. Available at: < https://stackoverflow.com/questions/77541256/set-variable-depending-on-radiogroup-button-selected-in-kotlin > [Accessed 26 April 2026]
StackOverflow. 2023b. getStringArrayList(key) in android generating null result. (version 8) [Source Code]. Available at: <https://stackoverflow.com/questions/11402050/getstringarraylistkey-in-android-generating-null-result> [Accessed 1 June 2026]
StackOverflow. 2018. Checking the array size and list size inside a when statement in Kotlin. (version 8) [Source Code]. Available at: <https://stackoverflow.com/questions/48753475/checking-the-array-size-and-list-size-inside-a-when-statement-in-kotlin> [Accessed 1 June 2026]
 */