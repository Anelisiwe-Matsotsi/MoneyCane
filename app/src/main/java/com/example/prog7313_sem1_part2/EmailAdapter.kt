package com.example.prog7313_sem1_part2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color

//(Android Knowledge, 2023)
class EmailAdapter (
    private val users: ArrayList<User>) : RecyclerView.Adapter<EmailAdapter.EmailViewHolder>()
{
        // (GeeksforGeeks, 2025b)
        //the email address the current user chose to share the wallet with is saved in the collection
        private val selectedEmails = mutableSetOf<String>()

    //recycler view adapter uses inner class for view holder to have access to adapter data outside this class
    // (Pathak, 2025)
    inner class EmailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEmail : TextView = itemView.findViewById(R.id.tvEmail)
        val emailCard : LinearLayout = itemView.findViewById(R.id.emailCard)
    }

    //(Gaurav, 2025)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_items_shared_wallet, parent, false)
        return EmailViewHolder(view)
    }

    //(Gaurav, 2025)
    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        val user = users[position]

        //(GeeksforGeeks, 2025)
        holder.tvEmail.text = user.email

        //(StackOverflow, 2018)
        //setting the background color when user selects an email address after search
        if (selectedEmails.contains(user.email)){
            holder.emailCard.setBackgroundColor(Color.parseColor("#A3C2A3"))
        }
        else {
            holder.emailCard.setBackgroundColor(Color.WHITE)
        }

        //(Android Developers,2026)
        //color changes after the user clicks and removes it when user clicks again x2
        holder.emailCard.setOnClickListener {
            if (selectedEmails.contains(user.email)) {
                selectedEmails.remove(user.email)
            }
            else {
                selectedEmails.add(user.email)
            }
            notifyItemChanged(position) //card selected is changed (StackOverflow, 2018)
        }

    }

    //(GeeksforGeeks, 2025)
    override fun getItemCount(): Int = users.size
}

/*
References

Android Developers. 2023. Use collection in Kotlin. (version 8) [Source Code]. Available at: <https://developer.android.com/codelabs/basic-android-kotlin-collections#2> [Accessed 1 June 2026]
Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Gaurav. 2025. Mastering RecyclerView: Optimizing onCreateViewHolder and onBindViewHolder for Large Datasets. (Version 8) [source code]. Available at: < https://medium.com/@kumargaurav.xqf/mastering-recyclerview-optimizing-oncreateviewholder-and-onbindviewholder-for-large-datasets-a05d9249a1b1 > [Accessed 26 April 2026]
GeeksforGeeks. 2025. RecyclerView in Android with Example. (Version 8) [source code]. Available at: < https://www.geeksforgeeks.org/android/android-recyclerview/ > [Accessed 26 April 2026]
GeeksforGeeks. 2025b. Kotlin mutableListOf(). (version 8) [Source Code]. Available at:<https://www.geeksforgeeks.org/kotlin/kotlin-mutablelistof/> [Accessed 1 June 2026]
Kotlin Programming Language. 2026. coerceIn | Core API. (Version 8) [source code]. Available at: < https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.ranges/coerce-in.html > [Accessed 26 April 2026]
Pathak, A. 2025. Kotlin Series: Nested and Inner Classes. (Version 8) [source code]. Available at: < https://medium.com/@myofficework000/kotlin-series-nested-and-inner-classes-0f76f6a13abd > [Accessed 26 April 2026]
StackOverflow. 2018. How to change the backgroundcolor of a cardview when it is selected?. (version 8) [Source Code]. Available at: <https://stackoverflow.com/questions/47441893/how-to-change-the-backgroundcolor-of-a-cardview-when-it-is-selected> [Accessed 1 June 2026]
*/