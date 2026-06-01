package com.example.prog7313_sem1_part2

data class InvitePeople(
    val InviteId: String = "",
    val id: String ="", //wallets ID
    val name: String = "", //Wallet name- Cash, Bank, Savings
    val people : List<String>, //nel@gmail.com, natty@gmail.com- people being sent the request
    val status: String = "", //pending, accepted, declined
    val senderEmail : String = "" //user that made the invite & wallet
)

/*
References

Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]

 */