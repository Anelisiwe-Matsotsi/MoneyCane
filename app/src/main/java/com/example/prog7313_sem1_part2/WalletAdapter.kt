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
class WalletAdapter (
    private val wallets: ArrayList<Wallet>,
    private val onEditClick: (Wallet) -> Unit,
    private val onDeleteClick: (Wallet) -> Unit): RecyclerView.Adapter<WalletAdapter.WalletViewHolder>()
{
    //recycler view adapter uses inner class for view holder to have access to adapter data outside this class
    // (Pathak, 2025)
    inner class WalletViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName : TextView = itemView.findViewById(R.id.tvWalletName)
        val tvBalance : TextView = itemView.findViewById(R.id.tvWalletBalance)
        val btnEdit : ImageButton = itemView.findViewById(R.id.btnEditWallet)
        val btnDelete : ImageButton = itemView.findViewById(R.id.btnDeleteWallet)
    }

    //(Gaurav, 2025)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_items_wallet, parent, false)
        return WalletViewHolder(view)
    }

    //(Gaurav, 2025)
    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        val wallet = wallets[position]

        //(GeeksforGeeks, 2025)
        holder.tvName.text = wallet.name
        holder.tvBalance.text = "R${wallet.balance.toInt()}"


        //edit icon button on click listener
        holder.btnEdit.setOnClickListener {
            onEditClick(wallet)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(wallet)
        }
    }

    //(GeeksforGeeks, 2025)
    override fun getItemCount(): Int = wallets.size
}

/*
References

Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Gaurav. 2025. Mastering RecyclerView: Optimizing onCreateViewHolder and onBindViewHolder for Large Datasets. (Version 8) [source code]. Available at: < https://medium.com/@kumargaurav.xqf/mastering-recyclerview-optimizing-oncreateviewholder-and-onbindviewholder-for-large-datasets-a05d9249a1b1 > [Accessed 26 April 2026]
GeeksforGeeks. 2025. RecyclerView in Android with Example. (Version 8) [source code]. Available at: < https://www.geeksforgeeks.org/android/android-recyclerview/ > [Accessed 26 April 2026]
Kotlin Programming Language. 2026. coerceIn | Core API. (Version 8) [source code]. Available at: < https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.ranges/coerce-in.html > [Accessed 26 April 2026]
Pathak, A. 2025. Kotlin Series: Nested and Inner Classes. (Version 8) [source code]. Available at: < https://medium.com/@myofficework000/kotlin-series-nested-and-inner-classes-0f76f6a13abd > [Accessed 26 April 2026]
 */