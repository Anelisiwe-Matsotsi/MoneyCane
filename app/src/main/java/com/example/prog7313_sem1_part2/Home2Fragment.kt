package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputBinding
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.apply
import kotlin.let

val flowersArray = arrayOf(
    "AllTransactions",
    "Expenses",
    "Incomes"
)

class Home2Fragment : Fragment(R.layout.fragment_home2) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    val viewPager : ViewPager2 = view.findViewById(R.id.viewPager)
        val tabLayout : TabLayout = view.findViewById(R.id.tab_layout)

        val adapter = ViewPageAdapter(childFragmentManager, lifecycle)
        viewPager.adapter=adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = flowersArray[position]
        }.attach()
    }
}