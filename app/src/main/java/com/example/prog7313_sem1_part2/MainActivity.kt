package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.savedstate.serialization.saved
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bnv_main)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when(destination.id) {

                R.id.logFragment,R.id.splashFragment -> { bottomNav.visibility = View.GONE
                }

                else -> {
                    bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container)
        return navController.navigateUp()

    }
    companion object {//links to AllTransactionsFragment, ExpensesFragment, IncomesFragment
        fun getUserID(): String {
            val user = Firebase.auth.currentUser

            user?.let {
                val uid = it.uid
                return uid
            }
            return ""
        }
    }
}
//reference https://www.youtube.com/watch?v=0LLlMz2NPhE
//https://www.linkedin.com/posts/chauhan-paresh-915340236_androiddevelopment-kotlin-navigationcomponent-activity-7377347549923409920-t3b9
//https://medium.com/@appdevinsights/companion-object-in-kotlin-c3a1203cd63c how to add and what are companion objects