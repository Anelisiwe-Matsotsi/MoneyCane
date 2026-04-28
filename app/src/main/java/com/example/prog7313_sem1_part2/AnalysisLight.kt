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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnalysisLight.newInstance] factory method to
 * create an instance of this fragment.
 */
class   AnalysisLight :  Fragment(R.layout.fragment_analysis_light) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val btnBack = view.findViewById<ImageButton>(R.id.iv_back_arrow)

        btnBack.setOnClickListener {
            findNavController().navigateUp()//goes to the previous fragment
        }


    }
}
//iv_back_arrow