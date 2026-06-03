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

class GoalHistoryFragment : Fragment() {

    /*(Firebase, 2026)*/
    private val goalDatabase = FirebaseDatabase.getInstance().getReference("goals")
    private val transactionDatabase = FirebaseDatabase.getInstance().getReference("transactions")
    private val rewardDatabase = FirebaseDatabase.getInstance().getReference("rewards")
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid ?: "guest"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalAdapter
    private val completedGoalList = ArrayList<Goal>()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_goal_history, container, false)



        //Recycler View
        recyclerView = view.findViewById(R.id.rvGoalHistory)
        adapter = GoalAdapter(completedGoalList)
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        recyclerView.adapter =adapter




        loadGoals()

        return view
    }


    // load goals
    private fun loadGoals() {

        goalDatabase.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!isAdded) return
                    completedGoalList.clear()
                    for (child in snapshot.children) {
                        val goal = child.getValue(Goal::class.java) ?: continue

                        if (goal.completed) {
                            completedGoalList.add(goal)

                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }








    /*Bhosale, 2024*/
    companion object {
        @JvmStatic
        fun newInstance() = GoalHistoryFragment()
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
