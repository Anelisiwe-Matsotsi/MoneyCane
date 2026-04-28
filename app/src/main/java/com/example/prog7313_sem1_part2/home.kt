package com.example.prog7313_sem1_part2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController


class home : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profile = view.findViewById<ImageButton>(R.id.ib_arrow_forward)
        val wallet = view.findViewById<Button>(R.id.btn_add_wallet)
        val budget = view.findViewById<Button>(R.id.btn_add_budget)
        //val goal = view.findViewById<Button>(R.id.btn_set_goal)
        //dont know what this is for yet
        val transaction = view.findViewById<Button>(R.id.btn_add_transaction)




        profile.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_profileLight)//to profile(light)
        }
        wallet.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_wallet_Create_Light_Fragment2)//to wallet(light)
        }
        budget.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_budgetLight2)//to budgets(light)
        }
        transaction.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_createTransactionFragment2)//to transaction(light)
        }
        transaction.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_createTransactionFragment2)//to transaction(light)
        }




    }
}
//ib_arrow_forward
//action_home2_to_wallet_Create_Light_Fragment2