package com.example.prog7313_sem1_part2

import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase

class ShareWalletFragment : Fragment() {

    //firebase instance
    private val uid = MainActivity.getUserID()

    private val database = FirebaseDatabase.getInstance().getReference("wallets") .child(uid)

}