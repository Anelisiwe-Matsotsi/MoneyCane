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
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class home : Fragment(R.layout.fragment_home) {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profile = view.findViewById<ImageButton>(R.id.ib_arrow_forward)
        val wallet = view.findViewById<Button>(R.id.btn_add_wallet)
        val budget = view.findViewById<Button>(R.id.btn_add_budget)
        val tvWelcomeName = view.findViewById<TextView>(R.id.tv_welcome_name)
        //val goal = view.findViewById<Button>(R.id.btn_set_goal)
        //dont know what this is for yet
        val transaction = view.findViewById<Button>(R.id.btn_add_transaction)
      //  val transactionView= view.findViewById<Button>(R.id.btn_view_all)
        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        //initial state
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        //how much it shows
        bottomSheetBehavior.peekHeight=110

        bottomSheetBehavior.isFitToContents=true

        //to get the users name to appear
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)
            userRef.get().addOnSuccessListener { snapshot ->
                // Using your User data class
                val user = snapshot.getValue(User::class.java)
                if (user != null && user.name.isNotEmpty()) {
                    tvWelcomeName.text = user.name
                } else {
                    tvWelcomeName.text = "User"
                }
            }.addOnFailureListener {
                tvWelcomeName.text = "User"
                // Optional: show error toast
                Toast.makeText(context, "Failed to load name", Toast.LENGTH_SHORT).show()
            }
        } else {
            tvWelcomeName.text = "User"
        }




        //load transaction fragment into sheet
        childFragmentManager.beginTransaction()
            .replace(R.id.transaction_container, Home2Fragment())
            .commit()

        //close button
        val closebtn =view.findViewById<ImageButton>(R.id.btnCloseSheet)

        closebtn.setOnClickListener {
            bottomSheetBehavior.state= BottomSheetBehavior.STATE_COLLAPSED
        }


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
}
//ib_arrow_forward
//action_home2_to_wallet_Create_Light_Fragment2