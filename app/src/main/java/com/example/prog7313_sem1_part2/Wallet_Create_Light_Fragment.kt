package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import kotlin.apply
import kotlin.let


class Wallet_Create_Light_Fragment : Fragment(R.layout.fragment_wallet__create__light_) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn_create_wallet = view.findViewById<Button>(R.id.editProfileB)

        val btn_edit_wallet = view.findViewById<ImageButton>(R.id.profileB6)

        val btn_back = view.findViewById<ImageButton>(R.id.profileB2)





//        btn_create_wallet.setOnClickListener {
//            findNavController().navigate(R.id.action_wallet_Create_Light_Fragment2_to_createBudgetFragment2)//to create wallet(light)
//        }
//        btn_edit_wallet.setOnClickListener {
//            findNavController().navigate(R.id.action_wallet_Create_Light_Fragment2_to_edit_Light_Fragment2)//to edit wallet(light)
//        }
//        btn_back.setOnClickListener {
//            findNavController().navigateUp()
//        }





    }
}
//editProfileB
//profileB6