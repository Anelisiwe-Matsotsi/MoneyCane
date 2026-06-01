package com.example.prog7313_sem1_part2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView

//(Android Knowledge, 2023)
class GoalAdapter (
    private val goals: ArrayList<Goal>
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>()
{
    //recycler view adapter uses inner class for view holder to have access to adapter data outside this class
    // (Pathak, 2025)
    inner class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGoalName : TextView = itemView.findViewById(R.id.tvGoalName)
        val tvProgress : TextView = itemView.findViewById(R.id.tvGoalProgress)
        val tvReward : TextView = itemView.findViewById(R.id.tvCandyReward)
        val tvDates : TextView = itemView.findViewById(R.id.tvDates)
        val progressBar : ProgressBar = itemView.findViewById(R.id.progressGoal)
       // val btnEdit : ImageButton = itemView.findViewById(R.id.btnEditCategory)

    }

    //(Gaurav, 2025)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_items_goal, parent, false)
        return GoalViewHolder(view)
    }

    //(Gaurav, 2025)
    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]

        //(GeeksforGeeks, 2025)
        holder.tvGoalName.text = goal.goalName

        val typeLabel = goal.goalType.replaceFirstChar { it.uppercase() }
        holder.tvProgress.text= "$typeLabel: R${goal.currentAmount.toInt()} / R${goal.targetAmount.toInt()}"
        //(Kotlin Programming Language, 2026)
        //calculating amount for progress bar

        val percent = if (goal.targetAmount > 0)
            ((goal.currentAmount / goal.targetAmount) * 100).toInt()
        else 0

        holder.progressBar.progress = percent.coerceAtMost(100)
        holder.tvReward.text = "🍭${goal.candyReward} Canes"

        holder.tvDates.text = "${goal.startDate} to ${goal.endDate}"

        //visual feedback
////        if (percent >= 100) {
////            holder.progressBar.progress = 100
//        }

    }
////        //edit icon button on click listener
////        holder.btnEdit.setOnClickListener {
////            onEditClick(goal)
//        }


    //(GeeksforGeeks, 2025)
    override fun getItemCount(): Int = goals.size
}

/*
References

Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Gaurav. 2025. Mastering RecyclerView: Optimizing onCreateViewHolder and onBindViewHolder for Large Datasets. (Version 8) [source code]. Available at: < https://medium.com/@kumargaurav.xqf/mastering-recyclerview-optimizing-oncreateviewholder-and-onbindviewholder-for-large-datasets-a05d9249a1b1 > [Accessed 26 April 2026]
GeeksforGeeks. 2025. RecyclerView in Android with Example. (Version 8) [source code]. Available at: < https://www.geeksforgeeks.org/android/android-recyclerview/ > [Accessed 26 April 2026]
Kotlin Programming Language. 2026. coerceIn | Core API. (Version 8) [source code]. Available at: < https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.ranges/coerce-in.html > [Accessed 26 April 2026]
Pathak, A. 2025. Kotlin Series: Nested and Inner Classes. (Version 8) [source code]. Available at: < https://medium.com/@myofficework000/kotlin-series-nested-and-inner-classes-0f76f6a13abd > [Accessed 26 April 2026]
 */