package com.example.prog7313_sem1_part2.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prog7313_sem1_part2.R
import com.example.prog7313_sem1_part2.onboarding.screens.FirstScreenFragment
import com.example.prog7313_sem1_part2.onboarding.screens.SecondScreenFragment
import com.example.prog7313_sem1_part2.onboarding.screens.ThirdScreenFragment

/*(StevdzaSan, 2020)*/
class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            FirstScreenFragment(),
            SecondScreenFragment(),
            ThirdScreenFragment()
        )
        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        view.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager).adapter = adapter


        return view
    }


}

/*Reference List:
StevdzaSan.2020.ViewPager2 with Navigation Component - Onboarding Screens | Android Studio Tutorial.[youtube video]. Available at:<https://youtu.be/COZ3j8Dwlog?si=S64kN2qvYr0zS9xf>. [Accessed 1 June 2026].
*/