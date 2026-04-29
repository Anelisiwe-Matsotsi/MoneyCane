package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth


class More_Light_Fragment : Fragment(R.layout.fragment_more__light_) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val BtnBudget = view.findViewById<ImageButton>(R.id.btn_budget)
        val BtnCatagory = view.findViewById<ImageButton>(R.id.btn_catagory)
        val BtnTransaction = view.findViewById<ImageButton>(R.id.btn_transaction)
        //val goal = view.findViewById<Button>(R.id.btn_set_goal)
        //dont know what this is for yet
        val BtnWallet = view.findViewById<ImageButton>(R.id.btn_wallet)
        val profile = view.findViewById<ImageButton>(R.id.btn_account)
        val Logout = view.findViewById<ImageButton>(R.id.btn_logout)





        BtnBudget.setOnClickListener {
            findNavController().navigate(R.id.action_more_Light_Fragment_to_budgetLight)//to profile(light)
        }
        BtnCatagory.setOnClickListener {
            findNavController().navigate(R.id.action_more_Light_Fragment_to_categoryFragment)//to wallet(light)
        }
        BtnTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_transaction_Fragment)//to budgets(light)
        }
        BtnWallet.setOnClickListener {
            findNavController().navigate(R.id.action_more_Light_Fragment_to_wallet_Create_Light_Fragment2)//to transaction(light)
        }
        profile.setOnClickListener {
            findNavController().navigate(R.id.action_more_Light_Fragment_to_profileLight)
        }
        Logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()//to transaction(light)
            findNavController().navigate(R.id.action_more_Light_Fragment_to_logFragment)
        }




    }
}