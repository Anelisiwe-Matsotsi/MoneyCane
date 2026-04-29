package com.example.prog7313_sem1_part2

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

//(Android Knowledge, 2023)
class TransactionAdapter (
    private val transactions: ArrayList<Transaction>,
    private val onEditClick: (Transaction) -> Unit): RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    //recycler view adapter uses inner class for view holder to have access to adapter data outside this class
    // (Pathak, 2025)
    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvTransactionName)
        val tvType: TextView = itemView.findViewById(R.id.tvTransactionType)
        val tvSpent: TextView = itemView.findViewById(R.id.tvTransactionAmount)

        val imageView: ImageView = itemView.findViewById(R.id.image_view)


    }

    //(Gaurav, 2025)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_items_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    //(Gaurav, 2025)
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        //(GeeksforGeeks, 2025)
        holder.tvName.text = transaction.name
        holder.tvType.text =
            transaction.type.replaceFirstChar { it.uppercase() } //first letter always capital letter
        holder.tvSpent.text = "R${transaction.amount.toInt()}"

        if (transaction.imageUrl.isNullOrBlank()) {
            holder.imageView.setImageResource(R.drawable.ic_local_dining)

        } else {
            holder.imageView.load(transaction.imageUrl) {
                placeholder(R.drawable.ic_local_dining)
                error((R.drawable.ic_local_dining))
                crossfade(true)
            }
        }
            //StackOverflow,2019
            if (transaction.type == "income") {
                holder.tvSpent.setTextColor(Color.GREEN)
                holder.tvType.setTextColor(Color.GREEN)
            } else {
                holder.tvSpent.setTextColor(Color.RED)
                holder.tvType.setTextColor(Color.RED)
            }


        }

        //(GeeksforGeeks, 2025)
        override fun getItemCount(): Int = transactions.size
    }

/*
References

Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Gaurav. 2025. Mastering RecyclerView: Optimizing onCreateViewHolder and onBindViewHolder for Large Datasets. (Version 8) [source code]. Available at: < https://medium.com/@kumargaurav.xqf/mastering-recyclerview-optimizing-oncreateviewholder-and-onbindviewholder-for-large-datasets-a05d9249a1b1 > [Accessed 26 April 2026]
GeeksforGeeks. 2025. RecyclerView in Android with Example. (Version 8) [source code]. Available at: < https://www.geeksforgeeks.org/android/android-recyclerview/ > [Accessed 26 April 2026]
Kotlin Programming Language. 2026. coerceIn | Core API. (Version 8) [source code]. Available at: < https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.ranges/coerce-in.html > [Accessed 26 April 2026]
Pathak, A. 2025. Kotlin Series: Nested and Inner Classes. (Version 8) [source code]. Available at: < https://medium.com/@myofficework000/kotlin-series-nested-and-inner-classes-0f76f6a13abd > [Accessed 26 April 2026]
Stack Overflow.2019. How to conditionally change text color in Android? (Version 8) [source code]  Available at:< https://stackoverflow.com/questions/57913995/how-to-conditionally-change-text-color-in-android.

‌ > [Accessed 28 April 2026]
 */