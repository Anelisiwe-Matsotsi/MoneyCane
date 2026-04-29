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
import android.util.Log
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth

class Light_Achievment_Fragment : Fragment() {

    /*(Firebase, 2026)*/
    private val goalDatabase = FirebaseDatabase.getInstance().getReference("goals")
    private val transactionDatabase = FirebaseDatabase.getInstance().getReference("transactions")

    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid ?: "guest"

    private var currentSpending: Double = 0.0
    private var totalCanesEarned: Int = 0
    private var rewardGiven: Boolean = false

    private var minGoal: Double = 0.0
    private var maxGoal: Double = 0.0


    private lateinit var progressBar: ProgressBar
    private lateinit var tvPercent: TextView
    private lateinit var tvCaneCount: TextView
    private lateinit var tvStatus: TextView
    private lateinit var caneIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_light__achievment_, container, false)

        val etMinGoal = view.findViewById<EditText>(R.id.etMinGoal)
        val etMaxGoal = view.findViewById<EditText>(R.id.etMaxGoal)
        val btnSave = view.findViewById<Button>(R.id.btnSaveGoals)

        progressBar = view.findViewById(R.id.progressBar)
        tvPercent = view.findViewById(R.id.textView20)
        tvCaneCount = view.findViewById(R.id.textView30)
        tvStatus = view.findViewById(R.id.tvStatus)
        caneIcon = view.findViewById(R.id.imageView3)

        // Load goals
        /*Stackoverflow, 2018*/
        goalDatabase.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val goal = snapshot.getValue(Goal::class.java)
                if (goal != null) {
                    minGoal = goal.minMonthlyGoal
                    maxGoal = goal.maxMonthlyGoal
                    totalCanesEarned = goal.totalCanesEarned

                    etMinGoal.setText(minGoal.toString())
                    etMaxGoal.setText(maxGoal.toString())
                    tvCaneCount.text = totalCanesEarned.toString()

                    Log.d("FIREBASE_GOAL", "Loaded: Min=$minGoal, Max=$maxGoal, Canes=$totalCanesEarned")
                    refreshUI()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load goals", Toast.LENGTH_SHORT).show()
            }
        })

        // Load transactions
        /*Firebase, 2026*/
        transactionDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var total = 0.0


                for (child in snapshot.children) {
                    val transaction = child.getValue(Transaction::class.java)


                    if (transaction != null && transaction.type.lowercase() == "expense") {
                        total += transaction.amount
                    }
                }

                currentSpending = total
                Log.d("FIREBASE_TRANS", "Total Global Expenses: $currentSpending")
                refreshUI()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error fetching transactions", Toast.LENGTH_SHORT).show()
            }
        })

        btnSave.setOnClickListener {
            val minInput = etMinGoal.text.toString().toDoubleOrNull() ?: 0.0
            val maxInput = etMaxGoal.text.toString().toDoubleOrNull() ?: 0.0

            if (minInput > 0 && maxInput > minInput) {
                minGoal = minInput
                maxGoal = maxInput
                rewardGiven = false // Allow new reward for new goals

                val goalObject = Goal(minGoal, maxGoal, totalCanesEarned)
                goalDatabase.child(userId).setValue(goalObject).addOnSuccessListener {
                    Toast.makeText(context, "Goals Saved", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please enter valid goals (Min < Max)", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun refreshUI() {
        if (::progressBar.isInitialized) {
            updateProgress(progressBar, tvPercent, tvStatus, tvCaneCount, caneIcon)
        }
    }

    /*Stackoverflow, 2019*/
    private fun updateProgress(
        progressBar: ProgressBar,
        tvPercent: TextView,
        tvStatus: TextView,
        tvCaneCount: TextView,
        caneIcon: ImageView
    ) {
        if (maxGoal <= 0) {
            tvStatus.text = "Set a Max Goal to see progress"
            progressBar.progress = 0
            tvPercent.text = "0%"
            return
        }

        val percentage = ((currentSpending * 100.0) / maxGoal).toInt()
        progressBar.progress = percentage.coerceAtMost(100)
        tvPercent.text = "$percentage%"

        /*Stackoverflow, 2021*/
        when {
            percentage > 100 -> {
                tvPercent.setTextColor(Color.RED)
                tvStatus.text = "Budget Exceeded"
            }
            percentage == 100 -> {
                tvPercent.setTextColor(Color.YELLOW)
                tvStatus.text = "At Limit"
            }
            currentSpending >= minGoal && currentSpending <= maxGoal -> {
                tvPercent.setTextColor(Color.GREEN)
                tvStatus.text = "Goal Achieved!"

                // Trigger reward only if within goal range
                if (!rewardGiven) {
                    triggerReward(tvCaneCount, caneIcon)
                }
            }
            else -> {
                tvPercent.setTextColor(Color.GREEN)
                val remaining = maxGoal - currentSpending
                tvStatus.text = "Spend R%.2f/day to stay safe".format(remaining / 30)
            }
        }
    }

    private fun triggerReward(tvCaneCount: TextView, caneIcon: ImageView) {
        rewardGiven = true
        val oldCount = totalCanesEarned
        totalCanesEarned++

        // Update Firebase
        val goalObject = Goal(minGoal, maxGoal, totalCanesEarned)
        goalDatabase.child(userId).setValue(goalObject)

        // Animations
        animateCaneCount(tvCaneCount, oldCount, totalCanesEarned)
        popAnimation(tvCaneCount)
        bounce(caneIcon)
        Toast.makeText(context, "Green Cane Earned!", Toast.LENGTH_SHORT).show()
    }

    /*Medium, 2021*/
    private fun animateCaneCount(textView: TextView, start: Int, end: Int) {
        ValueAnimator.ofInt(start, end).apply {
            duration = 600
            addUpdateListener { textView.text = it.animatedValue.toString() }
            start()
        }
    }

    private fun popAnimation(view: View) {
        view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(150).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
        }.start()
    }

    private fun bounce(view: View) {
        view.animate().translationY(-30f).setDuration(200).withEndAction {
            view.animate().translationY(0f).setDuration(200).start()
        }.start()
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
