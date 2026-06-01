package com.example.prog7313_sem1_part2

import java.sql.Date
import java.sql.RowId

//(Android Knowledge, 2023)
data class Goal(
    val goalId: String = "",
    val goalName: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val goalType: String = "", //"expense" or "income"
    val startDate: String = "",
    val endDate: String = "",
    val candyReward:Int = 5,
    val completed: Boolean = false,

)
/*
Reference List:
Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
*/