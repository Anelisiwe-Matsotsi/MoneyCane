package com.example.prog7313_sem1_part2

import android.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

//Ipek Birinci.2024
//There are 3 tabs in the layout
private const val NUM_TABS = 3

class ViewPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
  //  private val types = arrayOf("All", "Expenses", "Incomes")
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

/*References
Ipek Birinci.2024. Usage of ViewPager2 with TabLayout. [source code] . Available at: <https://medium.com/@ipek.birinci8/usage-of-viewpager2-with-tablayout-72c4a5a1ad64> [Accessed 28 Apr. 2026].
*/