package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlin.apply
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.animation.ValueAnimator
import android.widget.GridLayout
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class Light_Achievment_Fragment : Fragment() {

    /*(Firebase, 2026)*/
    private val goalDatabase = FirebaseDatabase.getInstance().getReference("goals")
    private val transactionDatabase = FirebaseDatabase.getInstance().getReference("transactions")
    private val rewardDatabase = FirebaseDatabase.getInstance().getReference("rewards")
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid ?: "guest"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalAdapter
    private val goalList = ArrayList<Goal>()


    //define the worth of the candy rewards
    //Jetbrains,2024
    //All images from FLATICON
    private val gardenLevels = listOf(
        Pair(5, R.drawable.cotton_candybluecloud), //patch 1 is worth 10 canes
        Pair(10, R.drawable.rainbow), //patch 2 is worth 30 canes
        Pair(15, R.drawable.unicorn), //patch 3 is worth 45 canes
        Pair(20, R.drawable.cotton_candysmile), //patch 4 is worth 60 canes
        Pair(25, R.drawable.cotton_candycane), //patch 5 is worth 65 canes
        Pair(30, R.drawable.gummy), //patch 6 is worth 80
        Pair(50, R.drawable.candies), //patch 7 is worth 80 canes
        Pair(60, R.drawable.easter_bunny), //patch 8 is worth 80 canes
        Pair(70, R.drawable.wedding_cake), //patch 9 is worth 80 canes
        Pair(80, R.drawable.gingerbread_house), //patch 10 is worth 80 canes
        Pair(90, R.drawable.bunny), //patch 11 is worth 80 canes
        Pair(100, R.drawable.gingerbread_housecane), //patch 12 is worth 80 canes

    )


    private lateinit var progressBar: ProgressBar
    private lateinit var tvPercent: TextView
    private lateinit var tvCaneCount: TextView
    private lateinit var gardenGrid: GridLayout
    private var totalCanes =0
    private lateinit var tvStatus: TextView
    private lateinit var caneIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view= inflater.inflate(R.layout.fragment_light__achievment_, container, false)



        tvCaneCount = view.findViewById(R.id.textView30)
        gardenGrid =view.findViewById(R.id.gardenGrid)
        caneIcon = view.findViewById(R.id.imageView3)
        //Recycler View
        recyclerView = view.findViewById(R.id.rvGoals)
        adapter = GoalAdapter(goalList)
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        recyclerView.adapter =adapter
        view.findViewById<Button>(R.id.btnHistory).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GoalHistoryFragment())
                .addToBackStack(null).commit()
        }
        view.findViewById<Button>(R.id.btnCreateGoal).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateGoalFragment())
                .addToBackStack(null).commit()
        }



        loadReward()
        loadGoals()

        return view
    }

    //Load reward
    private fun loadReward() {

        rewardDatabase.child(userId).child("reward")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                  val  reward= snapshot.getValue(Reward::class.java) ?: Reward()

                  animateCanes(totalCanes, reward.totalCanes)
                    totalCanes = reward.totalCanes
                    updateGardenInfo()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
    // load goals
    private fun loadGoals() {

        goalDatabase.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {


                override fun onDataChange(snapshot: DataSnapshot) {
                  if (!isAdded) {
                      return
                  }
                    goalList.clear()
                    for (child in snapshot.children) {
                        val goal = child.getValue(Goal::class.java) ?: continue

                        if (goal.currentAmount >= goal.targetAmount && !goal.completed) {
                            handleCompletion(goal)
                        } else if(!goal.completed) {
                            goalList.add(goal)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }



    //check completion
    private fun handleCompletion(goal: Goal) {
        if(!isAdded || context == null) return
       val  newCanes = totalCanes + 5
       rewardDatabase.child(userId).child("reward").child("totalCanes").setValue(newCanes)
           .addOnSuccessListener {
               totalCanes=newCanes
               goalDatabase.child(userId).child(goal.goalId).child("completed").setValue(true) //card goes away when goal is complete
               loadGoals()

               //Mascott
               requireActivity().supportFragmentManager.setFragmentResult(
                   "mascot_request", Bundle()
                       .apply {
                           putString("type", "goal")
                       }
               )
               activity?.runOnUiThread {
                   if (isAdded) {
                       Toast.makeText(context, "Goal Done! +5canes!", Toast.LENGTH_SHORT).show()
                   }
               }
           }
    }



//Android Developers, 2026
    //Reward Garden
    private fun updateGardenInfo() {

        //loop through each patch
        for (i in 0 until gardenGrid.childCount) {
            val patch = gardenGrid.getChildAt(i) as ImageView
            if (i < gardenLevels.size) {
                val (requiredCanes, resource) = gardenLevels[i]

                if (totalCanes >= requiredCanes) {
                    //unlock the sweet for this level
                    patch.setImageResource(resource)
                    patch.alpha = 1.0f
                    patch.imageAlpha= 255
                    if (i >= 6) patch.elevation = 10f else patch.elevation = 0f
                } else {
                    // show the locked state
                    patch.setImageResource(R.drawable.lock)
                    patch.alpha = 0.05f
                }
            }
        }
    }




    //    /*Medium, 2021*/
    private fun animateCanes( old: Int, new: Int) {
        ValueAnimator.ofInt(old, new).apply {
            duration = 1000
            addUpdateListener { tvCaneCount.text = it.animatedValue.toString() }
            start()
        }
    }



/*Bhosale, 2024*/
    companion object {
        @JvmStatic
        fun newInstance() = Light_Achievment_Fragment()
    }
}
/*Reference List:
Android Developers. 2026. ViewGroup | API reference.(Version 8) [source code]. Available at: < https://developer.android.com/reference/android/view/ViewGroup > [Accessed 1 June 2026].
Bhosale, A. 2024. Kotlin under the hood: exploring objects, companion objects, and annotations: @JvmStatic, @JvmField, @JvmOverloads.(version 8) [source code].Available at:<https://proandroiddev.com/kotlin-under-the-hood-exploring-objects-companion-objects-and-annotations-jvmstatic-71e1bf1128df>.[Accessed 26 April 2026].
Firebase. 2026. Work with Lists of Data on Android.(Version 8) [source code]. Available at:<https://firebase.google.com/docs/database/android/lists-of-data#child-events>.[Accessed 26 April 2026].
Medium.2021.Value Animator : Android Animation.[online].Available at:<https://medium.com/@rajatnfo/value-animator-android-animation-6841e856e5a8>.[Accessed 27 April 2026].
JetBrains. 2024. Collections overview | Kotlin Documentation.(Version 8) [source code]. Available at: < https://kotlinlang.org/docs/collections-overview.html > [Accessed 1 June 2026].
Stackoverflow.2018.Firebase Database setValue() and updateChildren() overwrites previous value.[online].Available at:<https://stackoverflow.com/questions/52697580/firebase-database-setvalue-and-updatechildren-overwrites-previous-value>.[Accessed 27 April 2026].
Stackoverflow.2019.How to calculate percentage in Kotlin.[online].Available at:<https://stackoverflow.com/questions/58156090/how-to-calculate-percentage-in-kotlin>.[Accessed 26 April 2026].
Stackoverflow.2011.How to set the text color of TextView in code?[online].Available at:<https://stackoverflow.com/questions/4602902/how-to-set-the-text-color-of-textview-in-code>.[Accessed 26 April 2026].
Stackoverflow.2021.How to use when statement in Kotlin?(Version 8) [source code]..Available at:<https://stackoverflow.com/questions/68081321/how-to-use-when-statement-in-kotlin>.[Accessed 26 April 2026].
*/
