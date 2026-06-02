package com.example.prog7313_sem1_part2

//(Android Knowledge, 2023)
data class Transaction(
val id: String = "",
val name: String = "",
val type: String = "expense", //or income
val amount: Double = 0.0, //sent or earned
val date: String="",
val content:String="",
val imageUrl: String="",
val categoryId: String="",
val walletId: String=""
)
/*
References

Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]

 */
