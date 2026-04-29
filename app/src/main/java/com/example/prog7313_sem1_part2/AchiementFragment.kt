package com.example.prog7313_sem1_part2

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlin.apply
import kotlin.let
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.animation.ValueAnimator
import android.widget.ImageView


class AchiementFragment : Fragment() {

    /*Firebase, 2026*/
    private val database = FirebaseDatabase.getInstance().getReference("goals")
//    private val expenseDatabase = FirebaseDatabase.getInstance().getReference("expenses")

    private var currentSpending: Double = 850.0 //default for now
    private var totalCanesEarned: Int = 0


    private var rewardGiven: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_light__achievment_, container, false)

        val etMinGoal = view.findViewById<EditText>(R.id.etMinGoal)
        val etMaxGoal = view.findViewById<EditText>(R.id.etMaxGoal)
        val btnSave = view.findViewById<Button>(R.id.btnSaveGoals)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvPercent = view.findViewById<TextView>(R.id.textView20)
        val tvCaneCount = view.findViewById<TextView>(R.id.textView30)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
        val caneIcon = view.findViewById<ImageView>(R.id.imageView3)

        btnSave.setOnClickListener {

            val minText = etMinGoal.text.toString()
            val maxText = etMaxGoal.text.toString()

            if (minText.isNotEmpty() && maxText.isNotEmpty()) {

                val minGoal = minText.toDouble()
                val maxGoal = maxText.toDouble()

                // Reset reward when goals change
                rewardGiven = false

                // Calculate Progress
                /*Stackoverflow,2019*/
                val percentage = ((currentSpending / maxGoal) * 100).toInt()

                // Cap progress bar at 100%
                progressBar.progress = percentage.coerceAtMost(100)
                tvPercent.text = "$percentage%"

                // Status
                /*Stackoverflow, 2021*/
                when {
                    percentage > 100 -> {
                        tvPercent.setTextColor(Color.RED) /*Stackoverflow, 2011*/
                        tvStatus.text = "Budget Exceeded"
                    }
                    percentage == 100 -> {
                        tvPercent.setTextColor(Color.YELLOW)
                        tvStatus.text = "At Limit"
                    }
                    else -> {
                        tvPercent.setTextColor(Color.GREEN)
                        tvStatus.text = "Within Budget"
                    }
                }

                // Reward Logic
                if (!rewardGiven && currentSpending >= minGoal && currentSpending <= maxGoal) {

                    rewardGiven = true

                    val oldCount = totalCanesEarned
                    totalCanesEarned += 1

                    animateCaneCount(tvCaneCount, oldCount, totalCanesEarned)
                    popAnimation(tvCaneCount)
                    bounce(caneIcon)

                    tvStatus.text = "Goal Achieved!"
                    Toast.makeText(context, "Green Cane Earned!", Toast.LENGTH_SHORT).show()
                }

                val myGoalObject = Goal(minGoal, maxGoal, totalCanesEarned)

                /*Stackoverflow, 2018*/
                database.child("current_user").setValue(myGoalObject)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Data Saved", Toast.LENGTH_SHORT).show()
                    }

            } else {
                Toast.makeText(context, "Fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // Animation
    /*Medium, 2021*/
    private fun animateCaneCount(textView: TextView, start: Int, end: Int) {
        val animator = ValueAnimator.ofInt(start, end)
        animator.duration = 600
        animator.addUpdateListener {
            val value = it.animatedValue as Int
            textView.text = value.toString()
        }
        animator.start()
    }

    private fun popAnimation(view: View) {
        view.scaleX = 0.5f
        view.scaleY = 0.5f
        view.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(150)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun bounce(view: View) {
        view.animate()
            .translationY(-20f)
            .setDuration(150)
            .withEndAction {
                view.animate()
                    .translationY(0f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    /*Bhosale, 2024*/
    companion object {
        @JvmStatic
        fun newInstance() = Light_Achievment_Fragment()
    }
}
/*Reference List:
Bhosale, A. 2024. Kotlin under the hood: exploring objects, companion objects, and annotations: @JvmStatic, @JvmField, @JvmOverloads.(version 8) [source code].Available at:<https://proandroiddev.com/kotlin-under-the-hood-exploring-objects-companion-objects-and-annotations-jvmstatic-71e1bf1128df>.[Accessed 26 April 2026].
Firebase. 2026. Work with Lists of Data on Android.(Version 8) [source code]. Available at:<https://firebase.google.com/docs/database/android/lists-of-data#child-events>.[Accessed 26 April 2026].
Medium.2021.Value Animator : Android Animation.[online].Available at:<https://medium.com/@rajatnfo/value-animator-android-animation-6841e856e5a8>.[Accessed 27 April 2026].
Stackoverflow.2018.Firebase Database setValue() and updateChildren() overwrites previous value.[online].Available at:<https://stackoverflow.com/questions/52697580/firebase-database-setvalue-and-updatechildren-overwrites-previous-value>.[Accessed 27 April 2026].
Stackoverflow.2019.How to calculate percentage in Kotlin.[online].Available at:<https://stackoverflow.com/questions/58156090/how-to-calculate-percentage-in-kotlin>.[Accessed 26 April 2026].
Stackoverflow.2011.How to set the text color of TextView in code?[online].Available at:<https://stackoverflow.com/questions/4602902/how-to-set-the-text-color-of-textview-in-code>.[Accessed 26 April 2026].
Stackoverflow.2021.How to use when statement in Kotlin?[online].Available at:<https://stackoverflow.com/questions/68081321/how-to-use-when-statement-in-kotlin>.[Accessed 26 April 2026].
*/
