package com.example.prog7313_sem1_part2.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.example.prog7313_sem1_part2.R
import com.google.android.material.card.MaterialCardView

class RewardsScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rewards_screen, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        val cardNext = view.findViewById<MaterialCardView>(R.id.next)
        val lottieRewards = view.findViewById<LottieAnimationView>(R.id.lottieRewards)

        lottieRewards.setAnimation(R.raw.lollipop_candy)
        lottieRewards.playAnimation()
        
        // Transition to the final budget setup screen
        cardNext.setOnClickListener {
            viewPager?.currentItem = 3
        }

        return view
    }
}