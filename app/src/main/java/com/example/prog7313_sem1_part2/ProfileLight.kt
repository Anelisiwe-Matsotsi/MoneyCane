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
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileLight.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileLight :  Fragment(R.layout.profile_view) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

          val BtnBack = view.findViewById<ImageButton>(R.id.iv_back_arrow)

          val btnLogout =view.findViewById<Button>(R.id.mbtn_logout)
//        val BtnTrasaction = view.findViewById<Button>(R.id.btn_transaction)
//        val BtnWallet = view.findViewById<Button>(R.id.btn_wallet)
//
        BtnBack.setOnClickListener {

            findNavController().navigateUp()

        }
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()//to transaction(light)
            findNavController().popBackStack(R.id.logFragment, false)

        }


    }
}
//btn_catagory
// btn_transaction
//btn_wallet