package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController

class BudgetLight : Fragment(R.layout.fragment_budget_light) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val BtnAnalysis = view.findViewById<Button>(R.id.btn_analysis)
        val BtnBack = view.findViewById<ImageButton>(R.id.iv_back_arrow)





        BtnAnalysis.setOnClickListener {
            findNavController().navigate(R.id.action_budgetLight_to_analysisLight)//to profile(light)
        }
        BtnBack.setOnClickListener {
            findNavController().navigateUp()//goes to previous fragment
        }





    }
}
//ib_arrow_forward
//action_home2_to_wallet_Create_Light_Fragment2
//action_budgetLight_to_analysisLight