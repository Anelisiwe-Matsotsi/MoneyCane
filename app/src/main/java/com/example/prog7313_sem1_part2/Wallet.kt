package com.example.prog7313_sem1_part2

data class Wallet(
    val id: String = "",
    val name: String = "", //Cash, Bank, Savings
    val balance: Double = 0.0, //initial amount when first created
    val people : List<String> //nel@gmail.com, natty@gmail.com
)
/*
References

Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]

 */