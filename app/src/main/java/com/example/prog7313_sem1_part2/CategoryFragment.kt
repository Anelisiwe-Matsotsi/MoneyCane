package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment :   Fragment(R.layout.fragment_category) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //for later use in the login
        val btn_add_catagory = view.findViewById<FloatingActionButton>(R.id.iconAddCategory)

        btn_add_catagory.setOnClickListener {

            findNavController().navigate(R.id.action_categoryFragment_to_editCategoryFragment)
        }




    }
}
//val fab = view.findViewById<FloatingActionButton>(R.id.fab_add)
//
//fab.setOnClickListener {
//    findNavController().navigate(R.id.nextFragment)
//}