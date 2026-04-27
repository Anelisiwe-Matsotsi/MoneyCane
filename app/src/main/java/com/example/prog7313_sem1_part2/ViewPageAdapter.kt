package com.example.prog7313_sem1_part2

import android.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


private const val NUM_TABS = 3
class ViewPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
private val types= arrayOf("All transactions", "Expenses", "Incomes")
    override fun getItemCount(): Int {
        return NUM_TABS
}
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return AllTransactionsFragment()
            1 -> return ExpensesFragment()
        }
        return IncomesFragment()
    }
}