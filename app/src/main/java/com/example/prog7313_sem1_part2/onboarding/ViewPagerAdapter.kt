package com.example.prog7313_sem1_part2.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/*(StevdzaSan, 2020)*/
class ViewPagerAdapter(list: ArrayList<Fragment>, fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle){

    private val fragmentList = list

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }



}

/*Reference List:
StevdzaSan.2020.ViewPager2 with Navigation Component - Onboarding Screens | Android Studio Tutorial.[youtube video]. Available at:<https://youtu.be/COZ3j8Dwlog?si=S64kN2qvYr0zS9xf>. [Accessed 1 June 2026].
*/