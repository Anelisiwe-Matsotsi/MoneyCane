package com.example.prog7313_sem1_part2

//(Android Knowledge, 2023)
data class Goal(
    val minMonthlyGoal: Double = 0.0,
    val maxMonthlyGoal: Double = 0.0,
    val totalCanesEarned: Int = 0,
    var rewardGiven: Boolean = false,
    val canesEarned: Int = 0
)
/*
Reference List:
Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
*/