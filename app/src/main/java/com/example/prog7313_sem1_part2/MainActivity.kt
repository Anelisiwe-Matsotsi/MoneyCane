package com.example.prog7313_sem1_part2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.savedstate.serialization.saved

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().
            replace(R.id.fragment_container,
                LightWalletFragment()).commit()

        }
    }
}