package com.example.prog7313_sem1_part2.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.example.prog7313_sem1_part2.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

/*(StevdzaSan, 2020)*/
class FirstScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first_screen, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        val nextButton = view.findViewById<MaterialCardView>(R.id.next)
        val lottieWelcome = view.findViewById<LottieAnimationView>(R.id.lottieWelcome)

        lottieWelcome.setAnimation(R.raw.partydance)
        lottieWelcome.playAnimation()


        nextButton.setOnClickListener{
            viewPager?.currentItem = 1
        }
        return view
    }

}

/*Reference List:
StevdzaSan.2020.ViewPager2 with Navigation Component - Onboarding Screens | Android Studio Tutorial.[youtube video]. Available at:<https://youtu.be/COZ3j8Dwlog?si=S64kN2qvYr0zS9xf>. [Accessed 1 June 2026].
*/