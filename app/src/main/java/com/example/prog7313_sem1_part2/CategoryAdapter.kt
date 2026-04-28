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
class CategoryAdapter (
    private val categories: ArrayList<Category>,
    private val onEditClick: (Category) -> Unit): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>()
{
        //recycler view adapter uses inner class for view holder to have access to adapter data outside this class
        // (Pathak, 2025)
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName : TextView = itemView.findViewById(R.id.tvCategoryName)
        val tvType : TextView = itemView.findViewById(R.id.tvCategoryType)
        val tvSpent : TextView = itemView.findViewById(R.id.tvCategorySpent)
        val tvLimit : TextView = itemView.findViewById(R.id.tvCategoryLimit)
        val progressBar : ProgressBar = itemView.findViewById(R.id.progressCategory)
        val btnEdit : ImageButton = itemView.findViewById(R.id.btnEditCategory)
    }

    //(Gaurav, 2025)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_items_category, parent, false)
        return CategoryViewHolder(view)
    }

    //(Gaurav, 2025)
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        //(GeeksforGeeks, 2025)
        holder.tvName.text = category.name
        holder.tvType.text = category.type.replaceFirstChar { it.uppercase() } //first letter always capital letter
        holder.tvSpent.text = "R${category.spent.toInt()}"
        holder.tvLimit.text = "R${category.limit.toInt()}"

        //(Kotlin Programming Language, 2026)
        //calculating amount for progress bar
        val progress = if(category.limit > 0)
        {
            ((category.spent / category.limit) * 100).toInt().coerceIn(0,100)
        } else {
            0
        }
        holder.progressBar.progress = progress

        //edit icon button on click listener
        holder.btnEdit.setOnClickListener {
            onEditClick(category)
        }
    }

    //(GeeksforGeeks, 2025)
    override fun getItemCount(): Int = categories.size
}

/*
References

Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Gaurav. 2025. Mastering RecyclerView: Optimizing onCreateViewHolder and onBindViewHolder for Large Datasets. (Version 8) [source code]. Available at: < https://medium.com/@kumargaurav.xqf/mastering-recyclerview-optimizing-oncreateviewholder-and-onbindviewholder-for-large-datasets-a05d9249a1b1 > [Accessed 26 April 2026]
GeeksforGeeks. 2025. RecyclerView in Android with Example. (Version 8) [source code]. Available at: < https://www.geeksforgeeks.org/android/android-recyclerview/ > [Accessed 26 April 2026]
Kotlin Programming Language. 2026. coerceIn | Core API. (Version 8) [source code]. Available at: < https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.ranges/coerce-in.html > [Accessed 26 April 2026]
Pathak, A. 2025. Kotlin Series: Nested and Inner Classes. (Version 8) [source code]. Available at: < https://medium.com/@myofficework000/kotlin-series-nested-and-inner-classes-0f76f6a13abd > [Accessed 26 April 2026]
 */