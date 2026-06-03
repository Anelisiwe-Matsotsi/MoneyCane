package com.example.prog7313_sem1_part2

import java.io.Serializable

data class User(
    val uid: String = "",
    val email: String = "",
    val fcmToken: String = ""
) : Serializable //(Sony, 2024)
                //Serializable converts the User.kt object into a byte stream to transfer it to the wallet fragment in an array like way

/*
References
Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Sony, V.,K. 2024. Serializable vs Parcelable in Android Kotlin: Whats the difference?. (version 8) [Source Code]. Available at: <https://medium.com/@iamvishnusoni/serializable-vs-parcelable-in-android-kotlin-whats-the-difference-10bb1894ce39> [Accessed 2 June 2026]
*/