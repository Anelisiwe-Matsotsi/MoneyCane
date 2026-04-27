package com.example.prog7313_sem1_part2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.navigation.fragment.findNavController

class LogFragment : Fragment(R.layout.fragment_log) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //for later use in the login
        val sharedPref = requireActivity().getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)
        val btnLogin =view.findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener{

                findNavController().navigate(R.id.action_logFragment_to_home)

        }


    }





}