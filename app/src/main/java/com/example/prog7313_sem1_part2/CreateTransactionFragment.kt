package com.example.prog7313_sem1_part2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import kotlin.apply
import kotlin.let


class CreateTransactionFragment :  Fragment(R.layout.fragment_transaction_) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //for later use in the login
        val btnSelectCatagory = view.findViewById<Button>(R.id.btn_select_catagory)
        val btnsave = view.findViewById<Button>(R.id.materialButton2)

        btnSelectCatagory.setOnClickListener {

            //findNavController().navigate(R.id.)
            //for when we decide what we are doing here
        }
        btnsave.setOnClickListener {


            //put logic above ^^^^^^^^^^^^^
            findNavController().navigateUp()//this just returns to the previous fragment from the nav_graph

        }



    }
}
//btn_select_catagory
//materialButton2